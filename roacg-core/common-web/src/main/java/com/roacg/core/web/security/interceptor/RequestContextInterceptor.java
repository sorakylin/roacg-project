package com.roacg.core.web.security.interceptor;

import com.roacg.core.base.enmus.ServiceCode;
import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.consts.RoAuthConst;
import com.roacg.core.utils.JsonUtil;
import com.roacg.core.utils.context.RoContext;
import com.roacg.core.utils.sejwt.cryptography.Codec;
import com.roacg.core.utils.sejwt.jwt.SeJwt;
import com.roacg.core.utils.sejwt.jwt.en.JwtAttribute;
import com.roacg.core.utils.sejwt.jwt.token.Token;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 2020-05-04: 暂时只做请求的上下文设置  不做效验
 */
public class RequestContextInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader(RoAuthConst.TOKEN_HEADER);

        //没有token 或者token格式不对，直接跳过
        if (Objects.isNull(token)) return true;
        if (token.length() <= RoAuthConst.TOKEN_PREFIX.length()) return true;

        token = token.replaceFirst(RoAuthConst.TOKEN_PREFIX, "");

        Token tokenInstance = SeJwt.asToken(token);

        tokenInstance.getAttributeValue(JwtAttribute.Header.ALG)
                .map(alg -> Codec.Type.valueOf(alg))
                .map(Codec.Type::create)
                .ifPresent(tokenInstance::useSpecifiedCodec);


        //令牌不正确 跳过
        if (!tokenInstance.validateToken()) return true;

        //只处理颁发者为网关的token
        String iss = tokenInstance.getAttributeValue(JwtAttribute.Payload.ISS).orElse("");
        if (Objects.equals(iss, ServiceCode.GATEWAY.getApplicationName())) {
            tokenInstance.getAttributeValue(RoAuthConst.TOKEN_USER_KEY)
                    .flatMap(userJson -> JsonUtil.fromJsonToObject(userJson, RequestUser.class))
                    .ifPresent(RoContext::setRequestUser); //设置进上下文
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RoContext.clearRequestUser();
    }
}

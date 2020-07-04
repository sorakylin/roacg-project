package com.roacg.core.web.interceptor;

import com.google.common.net.HttpHeaders;
import com.roacg.core.model.enums.RoApiStatusEnum;
import com.roacg.core.model.exception.RoApiException;
import com.roacg.core.model.resource.RoApiResponse;
import com.roacg.core.utils.JsonUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Objects;

import static com.roacg.core.model.enums.RoApiStatusEnum.FORBIDDEN;
import static com.roacg.core.model.enums.RoApiStatusEnum.UNAUTHORIZED;

@Slf4j
public class GlobalExceptionHandler implements HandlerExceptionResolver {

    @Override
    @SneakyThrows
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object obj, Exception ex) {

//    	ex.printStackTrace();//打印异常，方便问题追踪

        ModelAndView mv = new ModelAndView();
        if (!isJsonRequest(request)) {
            return mv;
        }

        response.setStatus(HttpStatus.OK.value());
        // 设置ContentType
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 避免乱码
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");

        String result;

        if (ex instanceof RoApiException) {
            RoApiException apiException = (RoApiException) ex;
            int code = apiException.getCode();
            if (Objects.equals(UNAUTHORIZED.getCode(), code) || Objects.equals(FORBIDDEN.getCode(), code)) {
                response.setStatus(code);
            }

            result = JsonUtil.toJson(RoApiResponse.fail(apiException)).get();
        } else {

            log.error("系统内发生未知异常", ex);

            RoApiResponse<Exception> fail = RoApiResponse.fail(RoApiStatusEnum.SYSTEM_ERROR, ex.getMessage(), ex);
            result = JsonUtil.toJson(fail).get();
        }

        @Cleanup
        PrintWriter writer = response.getWriter();

        writer.write(result);
        writer.flush();
        return mv;
    }


    private boolean isJsonRequest(HttpServletRequest request) {

        String accept = request.getHeader(HttpHeaders.ACCEPT);
        String requestWith = request.getHeader(HttpHeaders.X_REQUESTED_WITH);

        return (
                Objects.nonNull(accept) && accept.contains(MediaType.APPLICATION_JSON_VALUE) ||
                        Objects.nonNull(requestWith) && requestWith.contains("XMLHttpRequest")
        );
    }

}
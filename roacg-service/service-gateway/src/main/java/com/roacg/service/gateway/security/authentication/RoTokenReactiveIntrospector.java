package com.roacg.service.gateway.security.authentication;

import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.roacg.core.model.auth.CredentialsType;
import com.roacg.core.model.auth.RequestUser;
import com.roacg.core.model.consts.RoAuthConst;
import com.roacg.core.utils.JsonUtil;
import com.roacg.core.utils.context.RoContext;
import com.roacg.service.gateway.route.data.OAuth2TokenResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.*;

/**
 * 自定义的 token 内省
 * 主要作为 OAuth2 Client 向认证服务器请求认证, 在此基础上面封装好对应的权限
 *
 * <p>
 * Create by skypyb on 2020/03/18
 */
public class RoTokenReactiveIntrospector implements ReactiveOpaqueTokenIntrospector {


    private URI introspectionUri;
    private WebClient webClient;

    private String authorityPrefix = "SCOPE_";

    /**
     * Creates a {@code OpaqueTokenReactiveAuthenticationManager} with the provided parameters
     *
     * @param introspectionUri The introspection endpoint uri
     * @param clientId         The client id authorized to introspect
     * @param clientSecret     The client secret for the authorized client
     */
    public RoTokenReactiveIntrospector(String introspectionUri, String clientId, String clientSecret) {
        Assert.hasText(introspectionUri, "introspectionUri cannot be empty");
        Assert.hasText(clientId, "clientId cannot be empty");
        Assert.notNull(clientSecret, "clientSecret cannot be null");

        this.introspectionUri = URI.create(introspectionUri);
        this.webClient = WebClient.builder()
                .defaultHeaders(h -> h.setBasicAuth(clientId, clientSecret, StandardCharsets.UTF_8))
                .build();
    }

    /**
     * Creates a {@code OpaqueTokenReactiveAuthenticationManager} with the provided parameters
     *
     * @param introspectionUri The introspection endpoint uri
     * @param webClient        The client for performing the introspection request
     */
    public RoTokenReactiveIntrospector(String introspectionUri, WebClient webClient) {
        Assert.hasText(introspectionUri, "introspectionUri cannot be null");
        Assert.notNull(webClient, "webClient cannot be null");

        this.introspectionUri = URI.create(introspectionUri);
        this.webClient = webClient;
    }

    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {

        return Mono.just(token)
                .flatMap(this::makeRequest)//携带token请求 AuthorizationServer
                .flatMap(this::adaptToNimbusResponse)//检查Http响应正确性 (看是不是200)
                .map(this::parseNimbusResponse)//封装Http响应为Token内省响应
                .map(this::castToNimbusSuccess)//检查Token内省响应正确性
                .doOnNext(response -> validate(token, response))//效验返回值 (active == true?)
                .map(this::convertClaimsSet)//解析返回值中携带的信息，封装成认证对象
                .onErrorMap(e -> !(e instanceof OAuth2IntrospectionException), this::onError);
    }

    private Mono<ClientResponse> makeRequest(String token) {
        return this.webClient.post()
                .uri(this.introspectionUri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromFormData("token", token))
                .exchange();
    }

    private Mono<HTTPResponse> adaptToNimbusResponse(ClientResponse responseEntity) {
        HTTPResponse response = new HTTPResponse(responseEntity.rawStatusCode());
        response.setHeader(HttpHeaders.CONTENT_TYPE, responseEntity.headers().contentType().get().toString());
        if (response.getStatusCode() != HTTPResponse.SC_OK) {
            return responseEntity.bodyToFlux(DataBuffer.class)
                    .map(DataBufferUtils::release)
                    .then(Mono.error(new OAuth2IntrospectionException(
                            "Introspection endpoint responded with " + response.getStatusCode())));
        }
        return responseEntity.bodyToMono(String.class)
                .doOnNext(response::setContent)
                .map(body -> response);
    }

    private TokenIntrospectionResponse parseNimbusResponse(HTTPResponse response) {
        try {
            return TokenIntrospectionResponse.parse(response);
        } catch (Exception ex) {
            throw new OAuth2IntrospectionException(ex.getMessage(), ex);
        }
    }

    private TokenIntrospectionSuccessResponse castToNimbusSuccess(TokenIntrospectionResponse introspectionResponse) {
        if (!introspectionResponse.indicatesSuccess()) {
            throw new OAuth2IntrospectionException("Token introspection failed");
        }
        return (TokenIntrospectionSuccessResponse) introspectionResponse;
    }

    private void validate(String token, TokenIntrospectionSuccessResponse response) {
        // relying solely on the authorization server to validate this token (not checking 'exp', for example)
        if (response.isActive()) {

            Object tokenUser = response.getParameters().get(RoAuthConst.TOKEN_USER_KEY);

            //将响应的用户信息设置进请求上下文
            JsonUtil.fromJsonToObject(tokenUser.toString(), OAuth2TokenResponse.TokenUserInfo.class)
                    .map(user -> RequestUser.builder()
                            .id(user.getUid())
                            .name(user.getUserName())
                            .authorities(user.getUserAuthorities())
                            .credentialsType(CredentialsType.TOKEN)
                            .credentials(token)
                            .build()
                    )
                    .ifPresent(RoContext::setRequestUser);
            return;
        }


        StringBuilder errMsg = new StringBuilder("Provided token isn't active!");

        String responseMsg = response.getStringParameter("msg");
        if (responseMsg != null) {
            errMsg.append(" ").append(responseMsg.strip());
        }

        //如果exp!=null, 则表示是个过期令牌，提示下过期时间
        Date exp = response.getExpirationTime();
        if (exp != null) {
            String dateFormat = DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss")
                    .format(exp.toInstant().atZone(ZoneId.systemDefault()));

            String tempMsg = errMsg.toString().strip();

            char c = tempMsg.charAt(tempMsg.length() - 1);
            //如果不是那几个ASCII的标点符号，就拼个句号
            if (c < 33 || c > 47) {
                errMsg.append(".");
            }

            errMsg.append(" Expires in ").append(dateFormat);
        }


        throw new BadOpaqueTokenException(errMsg.toString());
    }

    private OAuth2AuthenticatedPrincipal convertClaimsSet(TokenIntrospectionSuccessResponse response) {
        Map<String, Object> claims = response.toJSONObject();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (response.getAudience() != null) {
            List<String> audiences = new ArrayList<>();
            for (Audience audience : response.getAudience()) {
                audiences.add(audience.getValue());
            }
            claims.put(AUDIENCE, Collections.unmodifiableList(audiences));
        }
        if (response.getClientID() != null) {
            claims.put(CLIENT_ID, response.getClientID().getValue());
        }
        if (response.getExpirationTime() != null) {
            Instant exp = response.getExpirationTime().toInstant();
            claims.put(EXPIRES_AT, exp);
        }
        if (response.getIssueTime() != null) {
            Instant iat = response.getIssueTime().toInstant();
            claims.put(ISSUED_AT, iat);
        }
        if (response.getIssuer() != null) {
            claims.put(ISSUER, issuer(response.getIssuer().getValue()));
        }
        if (response.getNotBeforeTime() != null) {
            claims.put(NOT_BEFORE, response.getNotBeforeTime().toInstant());
        }
        if (response.getScope() != null) {
            List<String> scopes = Collections.unmodifiableList(response.getScope().toStringList());
            claims.put(SCOPE, scopes);

            for (String scope : scopes) {
                authorities.add(new SimpleGrantedAuthority(this.authorityPrefix + scope));
            }
        }

        return new DefaultOAuth2AuthenticatedPrincipal(claims, authorities);
    }

    private URL issuer(String uri) {
        try {
            return new URL(uri);
        } catch (Exception ex) {
            throw new OAuth2IntrospectionException("Invalid " + ISSUER + " value: " + uri);
        }
    }

    private OAuth2IntrospectionException onError(Throwable e) {
        return new OAuth2IntrospectionException(e.getMessage(), e);
    }
}
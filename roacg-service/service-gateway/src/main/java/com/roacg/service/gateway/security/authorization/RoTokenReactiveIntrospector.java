package com.roacg.service.gateway.security.authorization;

import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionSuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
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
import java.util.*;

import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.*;

/**
 * 自定义的 token 内省
 * 主要作为 OAuth2 Client 向认证服务器请求授权, 在此基础上面封装好对应的权限
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {

        return Mono.just(token)
                .flatMap(this::makeRequest)
                .flatMap(this::adaptToNimbusResponse)
                .map(this::parseNimbusResponse)
                .map(this::castToNimbusSuccess)
                .doOnNext(response -> validate(token, response))
                .map(this::convertClaimsSet)
                .onErrorMap(e -> !(e instanceof OAuth2IntrospectionException), this::onError);
    }

    private Mono<ClientResponse> makeRequest(String token) {
        return this.webClient.post()
                .uri(this.introspectionUri)
//                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE)
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
        if (!response.isActive()) {
            throw new BadOpaqueTokenException("Provided token isn't active");
        }
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
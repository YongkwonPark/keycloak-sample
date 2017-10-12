package com.woowahan.commons.web.method.support;

import org.keycloak.AuthorizationContext;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

public class KeycloakMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(KeycloakSecurityContext.class) ||
                parameter.getParameterType().isAssignableFrom(IDToken.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        KeycloakSecurityContext securityContext = getKeycloakSecurityContext(webRequest);
        if (parameter.getParameterType().isAssignableFrom(IDToken.class)) {
            return securityContext.getIdToken();
        }

        return securityContext;
    }

    private KeycloakSecurityContext getKeycloakSecurityContext(NativeWebRequest webRequest) {
        Object attribute = webRequest.getAttribute(KeycloakSecurityContext.class.getName(), RequestAttributes.SCOPE_SESSION);
        if (Objects.isNull(attribute)) {
            attribute = new UnauthorizedKeycloakSecurityContext();
        }

        return (KeycloakSecurityContext) attribute;
    }


    class UnauthorizedKeycloakSecurityContext extends KeycloakSecurityContext {

        @Override
        public AccessToken getToken() {
            throw new UnauthorizedAccessException();
        }

        @Override
        public String getTokenString() {
            throw new UnauthorizedAccessException();
        }

        @Override
        public AuthorizationContext getAuthorizationContext() {
            throw new UnauthorizedAccessException();
        }

        @Override
        public IDToken getIdToken() {
            throw new UnauthorizedAccessException();
        }

        @Override
        public String getIdTokenString() {
            throw new UnauthorizedAccessException();
        }

        @Override
        public String getRealm() {
            throw new UnauthorizedAccessException();
        }

    }

    class UnauthorizedAccessException extends RuntimeException {

    }

}

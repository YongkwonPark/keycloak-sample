package com.woowahan.commons.web.filter;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.servlet.OIDCServletHttpFacade;
import org.keycloak.representations.IDToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FilterSecurityInterceptor extends OncePerRequestFilter {

    private List<String> authorizedGroups = Collections.emptyList();

    public FilterSecurityInterceptor() {  }

    public FilterSecurityInterceptor(List<String> authorizedGroups) {
        this.authorizedGroups = authorizedGroups;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        OIDCServletHttpFacade facade = new OIDCServletHttpFacade(request, response);
        KeycloakSecurityContext securityContext = facade.getSecurityContext();
        if (Objects.isNull(securityContext)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "인증된 사용자만 시스템을 사용할 수 있습니다.");
            return;
        }

        if (!hasGroup(securityContext, authorizedGroups)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "허가된 사용자만 시스템을 사용할 수 있습니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private boolean hasGroup(KeycloakSecurityContext securityContext, List<String> groups) {
        IDToken token = securityContext.getIdToken();
        List<String> candidates = (List<String>) token.getOtherClaims().getOrDefault("groups", Collections.emptyList());

        return groups.stream().anyMatch(candidates::contains);
    }


    public List<String> getAuthorizedGroups() {
        return authorizedGroups;
    }

    public void setAuthorizedGroups(List<String> authorizedGroups) {
        this.authorizedGroups = authorizedGroups;
    }

}

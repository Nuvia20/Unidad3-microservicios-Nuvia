package com.universidad.authserver.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Convierte peticiones POST a /oauth2/token con grant_type=password
 * en un OAuth2PasswordGrantAuthenticationToken.
 *
 * Devuelve null para cualquier otro grant_type, permitiendo que los
 * conversores por defecto (authorization_code, refresh_token, etc.)
 * sigan funcionando.
 */
public class PasswordGrantAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (!"password".equals(request.getParameter("grant_type"))) {
            return null;
        }
        Map<String, Object> parameters = new HashMap<>();
        for (String name : request.getParameterMap().keySet()) {
            String[] values = request.getParameterMap().get(name);
            parameters.put(name, values.length == 1 ? values[0] : values);
        }
        // El cliente ya fue autenticado por OAuth2ClientAuthenticationFilter,
        // que lo deja en el SecurityContext (clientPrincipal requerido por
        // OAuth2AuthorizationGrantAuthenticationToken desde 1.5.x).
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        return new OAuth2PasswordGrantAuthenticationToken(parameters, clientPrincipal);
    }
}

package com.universidad.authserver.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;

/**
 * Token de autenticación para el grant type "password" (no soportado
 * nativamente por Spring Authorization Server).
 *
 * El clientPrincipal es la autenticación del cliente OAuth2 que
 * OAuth2ClientAuthenticationFilter deja en el SecurityContext.
 */
public class OAuth2PasswordGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    public OAuth2PasswordGrantAuthenticationToken(Map<String, Object> parameters,
                                                  Authentication clientPrincipal) {
        super(AuthorizationGrantType.PASSWORD, clientPrincipal, parameters);
    }
}

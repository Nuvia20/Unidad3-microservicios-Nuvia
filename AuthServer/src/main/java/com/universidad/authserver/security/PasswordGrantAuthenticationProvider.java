package com.universidad.authserver.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Proveedor de autenticación para el grant type "password".
 *
 * Flujo:
 *  1. Valida client_id / client_secret contra el RegisteredClient.
 *  2. Autentica al usuario con el AuthenticationManager (BCrypt).
 *  3. Genera un JWT (RS256) con los claims estándar (iss, sub, exp, iat, jti, scope, client_id).
 *  4. Devuelve un OAuth2AccessTokenAuthenticationToken, que el handler
 *     por defecto convierte en la respuesta JSON de /oauth2/token.
 */
public class PasswordGrantAuthenticationProvider implements AuthenticationProvider {

    private final RegisteredClientRepository registeredClientRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final PrivateKey privateKey;
    private final String issuer;
    private final String keyId;

    public PasswordGrantAuthenticationProvider(RegisteredClientRepository registeredClientRepository,
                                               AuthenticationManager authenticationManager,
                                               PasswordEncoder passwordEncoder,
                                               PrivateKey privateKey,
                                               String issuer,
                                               String keyId) {
        this.registeredClientRepository = registeredClientRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.privateKey = privateKey;
        this.issuer = issuer;
        this.keyId = keyId;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2PasswordGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2PasswordGrantAuthenticationToken token =
                (OAuth2PasswordGrantAuthenticationToken) authentication;
        Map<String, Object> params = token.getAdditionalParameters();
        Authentication clientPrincipal = token.getPrincipal() instanceof Authentication
                ? (Authentication) token.getPrincipal()
                : null;

        String clientId = clientPrincipal != null
                ? clientPrincipal.getName()
                : asString(params.get("client_id"));
        String clientSecret = asString(params.get("client_secret"));
        if (clientSecret == null && clientPrincipal != null) {
            clientSecret = asString(clientPrincipal.getCredentials());
        }
        String username = asString(params.get("username"));
        String password = asString(params.get("password"));
        String scope = asString(params.get("scope"));

        if (clientId == null || clientSecret == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }
        if (username == null || password == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client == null || !passwordEncoder.matches(clientSecret, client.getClientSecret())) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }
        if (!client.getAuthorizationGrantTypes().contains(AuthorizationGrantType.PASSWORD)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE);
        }

        // Autenticación del usuario (user / pass123)
        Authentication principal = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Intersección de scopes solicitados y autorizados
        Set<String> authorizedScopes = client.getScopes();
        Set<String> requestedScopes = (scope == null || scope.isBlank())
                ? authorizedScopes
                : new LinkedHashSet<>(Arrays.asList(scope.trim().split("\\s+")));
        Set<String> tokenScopes = new LinkedHashSet<>(authorizedScopes);
        tokenScopes.retainAll(requestedScopes);
        if (tokenScopes.isEmpty()) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
        }

        // Generación del JWT (RS256)
        Instant now = Instant.now();
        Duration ttl = client.getTokenSettings().getAccessTokenTimeToLive();
        Instant expiresAt = now.plus(ttl);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .subject(username)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiresAt))
                .jwtID(UUID.randomUUID().toString())
                .claim("client_id", clientId)
                .claim("scope", String.join(" ", tokenScopes))
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256, JOSEObjectType.JWT, keyId,
                null, null, null, null, null, null, null, null, null, null);
        JWSObject jws = new JWSObject(header, new Payload(claims.toJSONObject()));
        try {
            jws.sign(new RSASSASigner(privateKey));
        } catch (JOSEException e) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.SERVER_ERROR);
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                jws.serialize(),
                now,
                expiresAt,
                tokenScopes);

        return new OAuth2AccessTokenAuthenticationToken(client, principal, accessToken);
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }
}

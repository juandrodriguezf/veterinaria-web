package com.vetopia.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

/**
 * SERVICIO - JwtService
 * Genera, firma y valida los JSON Web Tokens de la aplicación. El token
 * viaja solo con el nombre de usuario (subject) y las fechas de emisión y
 * expiración, como en la clase.
 *
 * La clave HS512 se genera aleatoriamente al arrancar y vive en memoria, de
 * modo que al reiniciar el backend los tokens anteriores dejan de ser
 * válidos y hay que volver a iniciar sesión.
 */
@Service
public class JwtService {

    /** Clave HS512 generada una sola vez por arranque. */
    private final SecretKey clave = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /** Vigencia del token en milisegundos. */
    @Value("${app.jwt.expiracion-ms}")
    private long expiracionMs;

    /**
     * Construye y firma el token del usuario autenticado. Los permisos se
     * consultan de nuevo en cada petición a partir del username, no se
     * guardan como claims manipulables.
     */
    public String generarToken(Authentication autenticacion) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);
        return Jwts.builder()
                .subject(autenticacion.getName())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    /** Extrae el nombre de usuario (subject) de un token válido. */
    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    /** Indica si el token está bien firmado y no ha expirado. */
    public boolean esValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException excepcion) {
            return false;
        }
    }

    /** Verifica la firma, la estructura y la expiración, y devuelve los claims. */
    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

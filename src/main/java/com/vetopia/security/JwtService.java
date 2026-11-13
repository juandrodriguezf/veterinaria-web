package com.vetopia.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;

/**
 * SERVICIO - JwtService
 * Genera, firma y valida los JSON Web Tokens de la aplicación. El token
 * viaja con el nombre de usuario (subject) y con los datos mínimos que el
 * frontend necesita para navegar: rol, id del perfil y nombre.
 *
 * La clave HS512 se configura en application.properties (Base64) y no se
 * regenera en cada arranque, de modo que los tokens siguen siendo válidos
 * tras un reinicio en desarrollo.
 */
@Service
public class JwtService {

    /** Clave secreta en Base64 (mínimo 64 bytes para HS512). */
    @Value("${app.jwt.secret}")
    private String secretoBase64;

    /** Vigencia del token en milisegundos. */
    @Value("${app.jwt.expiracion-ms}")
    private long expiracionMs;

    /** Clave derivada una sola vez a partir del secreto configurado. */
    private SecretKey clave;

    @PostConstruct
    void inicializarClave() {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
    }

    /**
     * Construye y firma el token del usuario autenticado. Los claims de rol,
     * perfil y nombre permiten que Angular restaure la sesión sin guardar
     * identificadores manipulables en localStorage.
     */
    public String generarToken(String username, String rol, Integer perfilId, String nombre) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .claim("perfilId", perfilId)
                .claim("nombre", nombre)
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

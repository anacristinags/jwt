package api.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * Gera um token JWT com base nas informações do usuário.
     * @param username O nome de usuário (será o 'subject' do token).
     * @param role A role (perfil) do usuário (adicionada como 'claim').
     * @return O token JWT assinado.
     */
    public String generateToken(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secretKey)); // Assina com HMAC256 e sua chave secreta
    }

    /**
     * Valida um token JWT.
     * @param token O token JWT a ser validado.
     * @return true se o token for válido e não expirado, false caso contrário.
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            System.err.println("Erro na validação do token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extrai o nome de usuário (subject) de um token JWT.
     * @param token O token JWT.
     * @return O username.
     */
    public String getUsernameFromToken(String token) {
        return JWT.decode(token).getSubject();
    }

    /**
     * Extrai todas as claims de um token JWT.
     * @param token O token JWT.
     * @return Um mapa com as claims do token.
     */
    public Map<String, Object> getAllClaimsFromToken(String token) {
        return JWT.decode(token).getClaims().entrySet().stream()
                   .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue().asString()), HashMap::putAll);
    }
}
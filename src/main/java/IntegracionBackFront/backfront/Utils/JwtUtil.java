package IntegracionBackFront.backfront.Utils;

import com.google.api.client.util.Value;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String jwtSecreto;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration}")
    private long expirationMs;

    private final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public String create(String id, String correo, String rol){
        //Decodificar el secreto BASE64 y crea una clave HMAC-SHA segura
        SecretKey signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto));

        //Obtener la fecha actual y calcular la fecha de expiracion
        Date now = new Date();
        Date expiration = new Date(now.getTime()+expirationMs);

        //Construir el token con sus componentes
        return Jwts.builder()
                .setId(id) //Id del token
                .setIssuedAt(now) //Fecha de emision
                .setSubject(correo) //Sujeto(contenido del token)
                .setIssuer(issuer) //Emisor del token
                .setExpiration(expirationMs>=0 ? expiration: null) //Fecha de expiracion
                .signWith(signingKey, SignatureAlgorithm.HS256) //Llave unica del token (firma)
                .compact(); //Convierte el String a un String compacto
    }

    public String getValue(String jwt){
        Claims claims = parseClaims(jwt);
        return claims.getSubject();
    }

    public String getKey(String jwt){
        Claims claims = parseClaims(jwt);
        return claims.getId();
    }

    public Claims parseToken(String jwt) throws ExpiredJwtException, MalformedJwtException {
        return parseClaims(jwt);
    }

    public String extractTokenFromRequest(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("authToken")){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean validate(String token){
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            log.warn("Token invalido: {}", e.getMessage());
            return false;
        }
    }


    private Claims parseClaims(String jwt){
        //Configurar el parse con la clave y firma del token
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecreto)))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

}

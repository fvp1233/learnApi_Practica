package IntegracionBackFront.backfront.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtCookieAuthFilter.class);
    private static final String AUTH_COOKIE_NAME = "authToken";
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtCookieAuthFilter(JwtUtil jwtUtils){
        this.jwtUtil = jwtUtils;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Excluir los endpoints que no requieren de autenticacion
        if (request.getRequestURI().equals("/api/auth/login")){
            filterChain.doFilter(request, response);
            return;
        }
        //Intentar procesar la autenticacion JWT
        try {
            //Extraer el token de las cookies de la solicitud
            String token = extractTokenFromCookies(request);

            //Verificar que el token existe y no viene vacio
            if (token ==null ||token.isBlank()){
                sendError(response, "token no encontrado", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            //Parsear y validar el token usando JWTUtils
            Claims claims = jwtUtil.parseToken(token);

            //Crear objet ode autenticacion
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    Arrays.asList(()-> "ROLE_USER")
            );

            //Establecer la autenticacion en el contexto de seguridad de Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //Continuar con la cadena de filtros
            filterChain.doFilter(request, response);
        }
        catch (ExpiredJwtException e) {
            // El token ha expirado
            log.warn("Token expirado: {}", e.getMessage());
            sendError(response, "Token expirado", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            // El token tiene un formato incorrecto
            log.warn("Token malformado: {}", e.getMessage());
            sendError(response, "Token inválido", HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            // Cualquier otro error inesperado
            log.error("Error de autenticación", e);
            sendError(response, "Error de autenticación", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");    // Establece el tipo de contenido
        response.setStatus(status);                     // Establece código de estado HTTP
        response.getWriter().write(String.format(
                "{\"error\": \"%s\", \"status\": %d}", message, status)); // Escribir respuesta JSON
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return null;
        }
        return Arrays.stream(cookies)
                .filter(c -> AUTH_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    //Obtiene los permisos o roles del usuario desde el token
    private Collection<? extends GrantedAuthority> getAuthorities(String token){
        return Collections.emptyList();
    }
}

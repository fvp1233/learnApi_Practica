package IntegracionBackFront.backfront.Config.Security;

import IntegracionBackFront.backfront.Utils.JwtCookieAuthFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtCookieAuthFilter jwtCookieAuthFilter;

    public SecurityConfig(JwtCookieAuthFilter jwtCookieAuthFilter) {
        this.jwtCookieAuthFilter = jwtCookieAuthFilter;
    }

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //Aqui van todos los endpoints publicos que no requieren de un JWT
        http
                .csrf(csrf -> csrf.disable())
    }
}

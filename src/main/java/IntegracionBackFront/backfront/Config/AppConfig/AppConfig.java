package IntegracionBackFront.backfront.Config.AppConfig;

import IntegracionBackFront.backfront.Utils.JwtCookieAuthFilter;
import IntegracionBackFront.backfront.Utils.JwtUtil;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public JwtCookieAuthFilter jwtCookieAuthFilter(JwtUtil jwtUtil){
        return new JwtCookieAuthFilter(jwtUtil);
    }

}

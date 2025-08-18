package IntegracionBackFront.backfront.Config.Cloudinary;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    //Variables para almacenar las credenciales de Cloudinary
    private String cloudName;
    private String apiKey;
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary(){
    //Crear un objeto Dotenv
        Dotenv dotenv = Dotenv.load();

        //Crear un map para guardar la clave valor del archivo .env
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name",dotenv.get("CLOUDINARY_CLOUD_NAME")); //Nombre de la nube en Clodinary
        config.put("api_key", dotenv.get("CLOUDINARY_API_KEY")); //API KEY para la autencicacion
        config.put("api_secret", dotenv.get("CLOUDINARY_API_SECRET")); //API secret (clave secreta)

        //Retornamos una nuva instancia de Cloudinary con la configuracion cargada
        return new Cloudinary(config);

    }
}

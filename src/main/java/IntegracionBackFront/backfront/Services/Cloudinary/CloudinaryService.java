package IntegracionBackFront.backfront.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {



    //Constante que define el tamaño permito para los archivos (5MB)
    private static final long Max_File_Size = 5 * 1024 * 1024;
    //Constante para definir los tipos de archivos admitidos
    private static final String[] Allowed_Extenionss = {".jpg", ".jpeg", ".png"};
    //Cliente de Cloudinary inyectado como dependencia
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    //Metodo para subir imagenes al array de Cloudinary

    /**
     *
     * @param file
     * @return  String URL de la imagen
     * @throws IOException
     */
    public String uploadImage(MultipartFile file) throws IOException {
        //1. Validar el archivo
        validateImage(file);

        //Sube el archivo a Cloudinary con configuraciones basicas
        //Configura el tipo de recurso auto-detectado
        //Configura la calidad automatica con nivel "good"
        Map<?, ?> uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.asMap(
                        "resource_type", "auto",
                        "quality", "auto:good"
                ));
        //Retornamos la URL segura de la imagen
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Sube una imagen a una capeta en especifico
     * @param file
     * @param folder carpeta destino
     * @return URL segura (HTTPS) de la imagen subida
     * @throws IOException
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        validateImage(file);
        // Generar unombre unico para el archivo
        //Conservar la extension original
        // Agregar un prefijo y un UUID para evitar colisiones

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = "img_" + UUID.randomUUID() + fileExtension;

        //Configuracion para subir imagen
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder, //Carpeta de destino
                "public_id", uniqueFileName, //Nombre unico para el archivo
                "use_fileName", false, //No usar el nombre original
                "unique_fileName", false, //No generar nombre unico(proceso hecho anteriorimente)
                "overwrite", false, //No sobreescribir archivos
                "resource_type", "auto", //Auto-detectar tipo de recurso
                "quality", "auto:good" //Optimizacion de calidad automatica
        );
        //Subir el archivo
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        //Retornamos la URL segura
        return (String) uploadResult.get("secure_url");
    }

    /**
     *
     * @param file
     */
    private void validateImage(MultipartFile file){
        //1. Verificar si el archivo esta vacio
        if (file.isEmpty()){
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        //2. Verificar el tamaño de la imagen
        if (file.getSize() > Max_File_Size){
            throw new IllegalArgumentException("El archivo no puede superar los 5 MB");
        }
        //3. Obtener y validar el nombre original del archivo
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null){
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }
        //4. Extraer y validar la extension
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(Allowed_Extenionss).contains(extension)){
            throw new IllegalArgumentException("Solo se permiten archivos JPG, JPEG y PNG");
        }
        //Validar que el tipo de MIME sea una imagen
        if (!file.getContentType().startsWith("image/")){
            throw new IllegalArgumentException("El archivo debe ser valido");
        }
    }
}

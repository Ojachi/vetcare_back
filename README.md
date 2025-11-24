# vetcare
App para la gestion de una veterinaria

### Requisitos
- Java 21+
- Maven
- PostgreSQL
- Cuenta de Cloudinary (para gestión de imágenes)
- IDE (IntelliJ recomendado)

### Configuración de la base de datos
1. Crear la base de datos vacía en PostgreSQL:
   ```sql
   CREATE DATABASE vetcare;
   
2. Configurar variables de entorno o src/main/resources/application.properties:
    ```properties
    # Base de datos
    DB_URL=jdbc:postgresql://localhost:5432/vetcare
    DB_USER=postgres
    DB_PASS=tu_password
    
    # Cloudinary (obligatorio para imágenes)
    CLOUDINARY_CLOUD_NAME=your_cloud_name
    CLOUDINARY_API_KEY=your_api_key
    CLOUDINARY_API_SECRET=your_api_secret
    
    # SendGrid (opcional - para emails)
    SENDGRID_API_KEY=tu_sendgrid_key
    
    # Hugging Face (opcional - para chat AI)
    HF_API_KEY=tu_huggingface_key
    ```

### Pasos para correr el backend
#### Clonar el repositorio
   ```git
      git clone https://github.com/Ojachi/vetcare_back.git
   ```
   ```bash
      cd vetcare_back
   ```
#### Instalar dependencias
   ```bash
      mvn clean install
   ```

#### Correr la aplicación con Maven
   ```bash
      mvn spring-boot:run
   ```

### 📸 Gestión de Imágenes con Cloudinary

El sistema usa **Cloudinary** para almacenar y gestionar imágenes de productos.

**Configuración:**
1. Crear cuenta gratuita en [Cloudinary](https://cloudinary.com/)
2. Obtener credenciales del Dashboard
3. Configurar variables de entorno (ver arriba)

**Para desarrolladores frontend:**
Ver [CLOUDINARY_INTEGRATION.md](CLOUDINARY_INTEGRATION.md) para ejemplos de cómo enviar imágenes desde el frontend.

**Características:**
- ✅ Optimización automática de imágenes
- ✅ CDN global para carga rápida
- ✅ Transformaciones on-the-fly (resize, crop, etc.)
- ✅ Límite de 10MB por imagen
- ✅ Eliminación automática al borrar productos

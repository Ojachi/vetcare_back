# vetcare
App para la gestion de una veterinaria

### Requisitos
- Java 21+
- Maven
- PostgreSQL
- IDE (IntelliJ recomendado)

### Configuración de la base de datos
1. Crear la base de datos vacía en PostgreSQL:
   ```sql
   CREATE DATABASE vetcare;
   
2. Configurar src/main/resources/application.properties con tus credenciales:
    ```properties
    spring.application.name=vetcare_back
    server.port=8080
   

   # Datos de conexión a PostgreSQL
    
    spring.datasource.url=jdbc:postgresql://localhost:5432/vetcare
    spring.datasource.username=${DB_USER:postgres}
    spring.datasource.password=${DB_PASS:####}
    spring.datasource.driver-class-name=org.postgresql.Driver
    
   # Configuración JPA/Hibernate
      
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=false

### Pasos para correr el backend
#### Clonar el repositorio
   ```git
      git clone https://github.com/Ojachi/vetcare_back.git
   ```
   ```bash
      cd vetcare_back
   ```
#### Correr la aplicación con Maven
   ```bash
      mvn spring-boot:run
   ```

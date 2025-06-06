Sistema de Gestión de Tienda de Abarrotes

Este proyecto es un sistema de gestión RESTful para una tienda de abarrotes, desarrollado con Spring Boot. Su objetivo es administrar productos y registrar ventas, adhiriéndose a las mejores prácticas de desarrollo.
🚀 Tecnologías Utilizadas

    Java 17+: Lenguaje de programación.

    Spring Boot 3.x: Framework principal para el desarrollo de APIs REST, ofreciendo auto-configuración y un servidor embebido.

    Spring Data JPA: Abstracción para el acceso a datos, simplificando la interacción con la base de datos.

    Hibernate: Implementación de JPA, responsable del mapeo objeto-relacional (ORM).

    Lombok: Librería que reduce el código repetitivo (boilerplate) mediante anotaciones (ej. @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor).

    MySQL: Base de datos relacional utilizada para el almacenamiento persistente de datos.

    Maven: Herramienta robusta para la gestión de dependencias y el ciclo de vida del proyecto.

    Jakarta Validation (Bean Validation): Para la validación de los DTOs de entrada en los controladores.

    HikariCP: Pool de conexiones por defecto de Spring Boot, conocido por su rendimiento.

📋 Requisitos Funcionales (cubiertos por este proyecto)

    Gestión de Productos:

        Crear un producto (nombre, SKU, precio unitario, cantidad en stock, nombre de la categoría como texto simple).

        Obtener todos los productos.

        Obtener un producto por su ID.

        Actualizar un producto (datos descriptivos).

        Eliminar un producto.

        Obtener productos filtrando por el nombre de la categoría.

    Gestión de Ventas:

        Registrar una venta (identificador del cliente, ID del producto, cantidad comprada).

            Al registrar una venta, se debe verificar y disminuir la cantidad en stock del producto correspondiente.

            Si no hay stock suficiente, la venta no debe proceder y se debe informar un error.

        Obtener todas las ventas.

        Obtener ventas por identificador del cliente.

    Relaciones:

        Una venta está asociada a un cliente (relación Many-to-One).

        Una venta tiene múltiples ítems de venta (relación One-to-Many con DetalleVenta).

        Cada ítem de venta está asociado a un producto (relación Many-to-One).

🛠️ Requisitos Técnicos Esenciales (cubiertos por este proyecto)

    Patrones de Diseño:

        DTO (Data Transfer Object): Utilizado para la transferencia de datos entre las capas de servicio y controlador.

        Repository: Para el acceso a la base de datos (Spring Data JPA).

        Service: Para encapsular la lógica de negocio.

    Interfaces:

        ServicioProducto y su implementación ServicioProductoImpl.

        ServicioVenta y su implementación ServicioVentaImpl.

    ResponseEntity:

        Utilizado en los controladores para devolver respuestas HTTP personalizadas, incluyendo códigos de estado apropiados (ej. 200 OK, 201 Created, 400 Bad Request, 404 Not Found).

    Conexión a Base de Datos:

        Configurada para MySQL con HikariCP como pool de conexiones.

        Hibernate (spring.jpa.hibernate.ddl-auto=update) gestiona la creación y actualización del esquema de la base de datos a partir de las entidades.

    Manejo de Logs:

        Logging básico proporcionado por Spring Boot por defecto.

    Entidades Relacionadas:

        Las entidades Producto, Venta, Cliente, DetalleVenta y Categoría están definidas con las relaciones y estrategias de generación de ID adecuadas (@GeneratedValue(strategy = GenerationType.IDENTITY)).

📂 Estructura de Módulos y Paquetes

src/main/java/com/prueba/demo
├── Controller/               # Controladores REST que exponen los endpoints
│   ├── ProductoController.java
│   └── VentaController.java
├── dto/                      # Data Transfer Objects (DTOs)
│   ├── ClienteDto.java
│   ├── DetalleVentaDto.java  # Para la respuesta de los detalles de venta
│   ├── ItemVentaDTO.java     # Para los ítems en la solicitud de venta
│   ├── ProductoDto.java
│   └── VentaDto.java         # DTO híbrido para solicitud (items) y respuesta (detalles)
├── entity/                   # Clases de entidades JPA (mapeo a tablas de DB)
│   ├── Categoria.java
│   ├── Cliente.java
│   ├── DetalleVenta.java
│   ├── Producto.java
│   └── Venta.java
├── repository/               # Interfaces Spring Data JPA para acceso a datos
│   ├── CategoriaRepository.java
│   ├── ClienteRepository.java
│   ├── DetalleVentaRepository.java
│   ├── ProductoRepository.java
│   └── VentaRepository.java
└── service/                  # Lógica de negocio
├── impl/
│   ├── ServicioProductoImpl.java
│   └── ServicioVentaImpl.java
├── ServicioProducto.java # Interfaz de servicio
└── ServicioVenta.java    # Interfaz de servicio

⚙️ Configuración de la Base de Datos (application.properties)

spring.application.name=SistemaGestionTiendaAbarrotes

# Configuración de la base de datos (DataSource) para MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/sistemagestiontiendaabarrotes?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=

# Driver JDBC para MySQL. Este es el driver estándar para MySQL 8 y posteriores.
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Configuración del pool de conexiones HikariCP
spring.datasource.hikari.maximum-pool-size=100
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=300000
spring.datasource.hikari.max-lifetime=6000000

🚀 Cómo Ejecutar el Proyecto

    Requisitos Previos:

        Java 17+ instalado.

        Maven instalado.

        Una instancia de MySQL en ejecución (localmente o remota).

        Crear la base de datos sistemagestiontiendaabarrotes en tu servidor MySQL.

        Asegúrate de que tus entidades (Producto, Venta, Cliente, Categoria, DetalleVenta) tengan @GeneratedValue(strategy = GenerationType.IDENTITY) en sus IDs para la autogeneración.

    Configurar application.properties:

        Abre src/main/resources/application.properties y ajusta spring.datasource.username y spring.datasource.password según tu configuración de MySQL.

        Si el nombre de tu base de datos es diferente, ajusta sistemagestiontiendaabarrotes en la URL.

    Configurar pom.xml para la codificación:

        Asegúrate de que tu pom.xml incluya la configuración de codificación para UTF-8, crucial para evitar errores de MalformedInputException:

        <properties>
            <java.version>17</java.version>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        </properties>
        <dependencies>
            <!-- ... tus dependencias existentes ... -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-validation</artifactId>
            </dependency>
        </dependencies>
        <build>
            <plugins>
                <!-- ... plugin de maven-compiler-plugin ... -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-resources-plugin</artifactId>
                    <version>3.3.1</version>
                    <configuration>
                        <encoding>UTF-8</encoding>
                    </configuration>
                </plugin>
                <!-- ... spring-boot-maven-plugin ... -->
            </plugins>
        </build>

    Construir y Ejecutar:

        Abre una terminal en la raíz del proyecto.

        Ejecuta el siguiente comando para limpiar, construir y empaquetar el proyecto:

        mvn clean install

        Luego, ejecuta la aplicación Spring Boot:

        mvn spring-boot:run

        La aplicación se iniciará en http://localhost:8080.

🌐 Endpoints de la API

Aquí se detallan todos los endpoints disponibles en la API:
1. Productos (/api/productos)

   POST /api/productos

        Descripción: Crea un nuevo producto.

        Body (JSON): ProductoCreacionDTO

        {
            "nombre": "Leche Entera 1L",
            "sku": "LECHE001",
            "precioUnitario": 2500.00,
            "cantidadEnStock": 50,
            "nombreCategoria": "Lácteos"
        }

        Respuesta: 201 Created con ProductoRespuestaDTO.

   GET /api/productos

        Descripción: Obtiene una lista de todos los productos.

        Respuesta: 200 OK con List<ProductoRespuestaDTO>.

   GET /api/productos/{id}

        Descripción: Obtiene un producto específico por su ID.

        Ejemplo: GET /api/productos/1

        Respuesta: 200 OK con ProductoRespuestaDTO o 404 Not Found si no existe.

   PUT /api/productos/{id}

        Descripción: Actualiza los datos descriptivos de un producto (nombre, descripción, precio, categoría).

        Ejemplo: PUT /api/productos/1

        Body (JSON): ProductoActualizacionDTO

        {
            "nombre": "Leche Deslactosada 1L",
            "precioUnitario": 2700.00
            // ... otros campos a actualizar
        }

        Respuesta: 200 OK con ProductoRespuestaDTO actualizado o 404 Not Found.

   DELETE /api/productos/{id}

        Descripción: Elimina un producto por su ID.

        Ejemplo: DELETE /api/productos/1

        Respuesta: 204 No Content si la eliminación fue exitosa o 404 Not Found.

   GET /api/productos/filtrar?categoria={nombreCategoria}

        Descripción: Obtiene productos filtrados por el nombre de la categoría.

        Ejemplo: GET /api/productos/filtrar?categoria=Lácteos

        Respuesta: 200 OK con List<ProductoRespuestaDTO>.

2. Ventas (/api/ventas)

   POST /api/ventas

        Descripción: Registra una nueva venta. Verifica y disminuye el stock. Si no hay stock, la venta no procede.

        Body (JSON): VentaDto (contiene cliente (o clienteId) y items (List<ItemVentaDTO>)).

        {
            "cliente": { "id": 1 }, // O directamente "clienteId": 1
            "items": [
                { "productoId": 1, "cantidad": 2 },
                { "productoId": 2, "cantidad": 1 }
            ]
        }

        Respuesta: 201 Created con VentaDto (incluirá el id de la venta, fechaVenta, totalGlobalVenta, cliente, items originales y los detalles de la venta guardados).

   GET /api/ventas

        Descripción: Obtiene una lista de todas las ventas registradas.

        Respuesta: 200 OK con List<VentaDto>.

   GET /api/ventas/cliente/{clienteId}

        Descripción: Obtiene todas las ventas realizadas por un cliente específico.

        Ejemplo: GET /api/ventas/cliente/1

        Respuesta: 200 OK con List<VentaDto> (donde cada VentaDto incluye sus detalles).

   GET /api/ventas/{id}

        Descripción: Obtiene una venta específica por su ID.

        Ejemplo: GET /api/ventas/1

        Respuesta: 200 OK con VentaDto o 404 Not Found.

🤝 Contribución

¡Las contribuciones son bienvenidas! Si encuentras un error o tienes una mejora, no dudes en abrir un issue o enviar un pull request.
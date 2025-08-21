# HTTP Server - AREP Workshop 1

A basic HTTP server implemented in Java that can serve static files (HTML, CSS, JavaScript, images) and handle simple REST services. This project demonstrates fundamental networking and HTTP protocol concepts through implementing a web server from scratch.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

To run this project you need to have installed:

- **Java 21** or higher
- **Apache Maven 3.6** or higher
- **Git** (to clone the repository)

To verify if you have Java installed:

```
java -version
```

To verify if you have Maven installed:

```
mvn -version
```

### Installing

Follow these steps to set up the development environment:

1. **Clone the repository**

   ```
   git clone https://github.com/SebastianCardona-P/AREP-TALLER1.git
   cd AREP-TALLER1/httpserver
   ```

2. **Compile the project**

   ```
   mvn clean compile
   ```

3. **Run the server**

   ```
   mvn clean compile exec:java
   ```

4. **Verify the server is running**

   Open your web browser and visit:

   ```
   http://localhost:35000
   ```

   You should see a page with example forms that demonstrate the server's capabilities.

## Architecture

### Project Structure

The prototype follows a standard Maven project structure with clear separation of concerns:

```
httpserver/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── mycompany/
    │   │           └── httpserver/
    │   │               ├── HttpServer.java          # Main HTTP server implementation
    │   │               ├── EchoServer/
    │   │               │   ├── EchoClient.java      # Echo client for testing
    │   │               │   └── EchoServer.java      # Simple echo server
    │   │               └── examplesHttp/
    │   │                   ├── URLParser.java       # URL parsing utilities
    │   │                   └── URLReader.java       # URL reading utilities
    │   └── resources/
    │       ├── index.html                           # Main HTML page
    │       ├── images/
    │       │   ├── favicon.ico                      # Website favicon
    │       │   ├── favicon.png                      # Alternative favicon
    │       │   ├── otroUsuario.jpg                  # User avatar image
    │       │   └── usuario.png                      # User avatar image
    │       ├── scripts/
    │       │   └── script.js                        # Client-side JavaScript
    │       └── styles/
    │           └── style.css                        # CSS styling
    └── test/
        └── java/                                    # Unit tests directory
```

### Server Architecture

The HTTP server is built using Java's Socket API and follows these principles:

1. **Single-threaded request handling**: Processes one request at a time
2. **File type detection**: Based on file extensions (.html, .css, .js, .png, .jpg, .ico)
3. **Content-Type mapping**: Automatic MIME type assignment
4. **Static file serving**: Direct file system access for resources
5. **REST endpoint**: Simple JSON response service

### Accessing Different Content Types

The server can serve various types of content directly. Here are examples:

#### Main Application

```
http://localhost:35000/
```

Serves the main HTML page with interactive forms.

#### CSS Stylesheets (Raw)

```
http://localhost:35000/styles/style.css
```

Returns pure CSS content with `Content-Type: text/css`

#### JavaScript Files (Raw)

```
http://localhost:35000/scripts/script.js
```

Returns pure JavaScript content with `Content-Type: text/javascript`

#### Images (Raw Binary)

```
http://localhost:35000/usuario.png
http://localhost:35000/favicon.ico
http://localhost:35000/otroUsuario.jpg
```

Returns binary image content with appropriate `Content-Type: image/*`

#### REST API Endpoint

```
http://localhost:35000/app/hello?name=YourName
```

Returns JSON response: `{"mensaje": "Hola YourName"}`

### Content Type Handling

The server automatically detects and serves content with proper MIME types:

| File Extension | Content-Type      | Description      |
| -------------- | ----------------- | ---------------- |
| `.html`        | `text/html`       | HTML documents   |
| `.css`         | `text/css`        | Stylesheet files |
| `.js`          | `text/javascript` | JavaScript files |
| `.png`         | `image/png`       | PNG images       |
| `.jpg/.jpeg`   | `image/jpg`       | JPEG images      |
| `.ico`         | `image/ico`       | Icon files       |

## Running the tests

To run the automated system tests:

```
mvn test
```

### Break down into end to end tests

The end-to-end tests verify that the server can:

- Serve HTML files correctly
- Deliver static resources (CSS, JavaScript, images)
- Handle basic REST services
- Respond with appropriate HTTP status codes

```
mvn test -Dtest=HttpServerIntegrationTest
```

## Deployment

To deploy the server on a production system:

1. **Build the executable JAR:**

   ```
   mvn package
   ```

2. **Configure the firewall** to allow connections on port 35000

**Note:** For production, consider changing the default port by editing the `PORT` constant in `HttpServer.java`

## Built With

- **Java 21** - Main programming language
- **Maven** - Dependency management and project build
- **Socket library** - For low-level network communication
- **Java NIO** - For efficient file handling and I/O

## Features

The HTTP server implements the following characteristics:

- ✅ Serve static HTML files
- ✅ Serve CSS files for styling
- ✅ Serve JavaScript files
- ✅ Serve images (PNG, JPG, ICO)
- ✅ Simple REST service (`/app/hello?name=value`)
- ✅ Handle 404 errors

## Contributing

If you want to contribute to the project:

1. Fork the repository
2. Create a branch for your feature (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Authors

- **Sebastian Cardona Parra** - [SebastianCardona-P](https://github.com/SebastianCardona-P)

## Acknowledgments

- Inspired by networking and HTTP protocol concepts
- Educational implementation for the Enterprise Architectures (AREP) course

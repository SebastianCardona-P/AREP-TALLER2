# Web Framework for REST Services and Static File Management - AREP Workshop 2

A comprehensive web framework built in Java that enables developers to create REST services using lambda functions and manage static files efficiently. This project enhances the basic HTTP server from Workshop 1 by converting it into a fully functional web framework for backend development.

## Project Overview

This web framework provides developers with modern tools to:

- **Define REST services using lambda functions** - Simple and expressive service definitions
- **Extract and manipulate query parameters** - Easy access to URL parameters within services
- **Specify static file locations** - Flexible configuration of static resource directories
- **Handle concurrent requests** - Multi-threaded server architecture
- **Support multiple content types** - HTML, CSS, JavaScript, images, and JSON responses

## Key Features

### 1. GET Method for REST Services

Implement REST services using intuitive lambda functions:

```java
get("/hello", (req, res) -> "Hello " + req.getValue("name"));
get("/pi", (req, res) -> String.valueOf(Math.PI));
```

### 2. Query Parameter Extraction

Access URL parameters easily within your services:

```java
get("/greet", (req, res) -> "Hello " + req.getValue("name") + ", you are " + req.getValue("age") + " years old");
```

### 3. Static File Management

Configure where your static files are located:

```java
staticfiles("/webroot");  // Files will be served from target/classes/webroot/
```

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
   git clone https://github.com/SebastianCardona-P/AREP-TALLER2.git
   cd AREP-TALLER2
   ```

2. **Compile the project**

   ```
   mvn clean compile
   ```

3. **Run the web application**

   ```
   java -cp target/classes com.mycompany.httpserver.WebApplication.WebApplication
   ```

   or

   ```
   mvn clean compile exec:java
   ```

4. **Verify the server is running**

   Open your web browser and visit:

   ```
   http://localhost:35000
   ```

   You should see a page with example forms that demonstrate the server's capabilities.

## Usage Examples

### Example Web Application

Here's how a developer would use this framework to create a web application:

```java
public class WebApplication {
    public static void main(String[] args) throws IOException, URISyntaxException {
        // Configure static files location
        staticfiles("/webroot");

        // Define REST services with lambda functions
        get("/hello", (req, resp) -> "hello " + req.getValue("name") +" you are " + req.getValue("age") + " years old");
        get("/pi", (req, resp) -> String.valueOf(Math.PI));

        // Start the server
        startServer(args);
    }
}
```

### Supported URLs

Once the server is running, you can test these endpoints:

**REST Services:**

- `http://localhost:35000/app/hello?name=Pedro&age=28` - Returns personalized greeting
- `http://localhost:35000/app/pi` - Returns the value of PI
- `http://localhost:35000/app/world` - Returns "hello world!"

**Static Files:**

- `http://localhost:35000/` - Main HTML page
- `http://localhost:35000/index.html` - Same as above
- `http://localhost:35000/styles/style.css` - CSS stylesheet
- `http://localhost:35000/scripts/script.js` - JavaScript file
- `http://localhost:35000/images/usuario.png` - Image files
- `http://localhost:35000/usuario.png` - Same as above
- `http://localhost:35000/images/otroUsuario.png` - Image files

You can add others static files or images with png, jpg, ico or jpeg extensions in the correct directory, and the server will automatically serve these static files

## Architecture

### Project Structure

The framework follows a standard Maven project structure with clear separation of concerns:

```
AREP-TALLER2/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mycompany/httpserver/
│   │   │       ├── HttpServer.java          # Core server
│   │   │       ├── HttpRequest.java         # Request handling
│   │   │       ├── HttpResponse.java        # Response handling
│   │   │       ├── Service.java             # Service
│   │   │       └── WebApplication/
│   │   │           └── WebApplication.java  # Example web
│   │   └── resources/                       # Static web files
│   │       ├── index.html
│   │       ├── styles/style.css
│   │       ├── scripts/script.js
│   │       └── images/
│   └── test/
│       └── java/                           # Comprehensive
├── pom.xml                                 # Maven
└── README.md                              # This file
```

### Core Components

#### 1. HttpServer

The main server class that handles:

- **Socket management** - Accepts and manages client connections
- **Request routing** - Directs requests to appropriate handlers
- **Static file serving** - Serves HTML, CSS, JS, and image files
- **Service registration** - Manages GET and POST service mappings

#### 2. HttpRequest

Handles HTTP request processing:

- **Query parameter extraction** - Parses URL parameters using `getValue()`
- **URI parsing** - Processes request paths and query strings
- **Parameter mapping** - Converts query strings to accessible key-value pairs

#### 3. Service Interface

Enables lambda function registration:

```java
public interface Service {
    public String executeService(HttpRequest req, HttpResponse res);
}
```

#### 4. WebApplication

Example implementation showing framework usage:

- **Service registration** - Demonstrates GET and POST service definitions
- **Static file configuration** - Shows how to set up static resource directories
- **Server startup** - Illustrates complete application setup

## Testing

The project includes a comprehensive test suite that validates all framework functionality:

### Test Categories

#### 1. Integration Tests (`HttpServerIntegrationTest`)

- End-to-end server functionality
- Static file serving (HTML, CSS, JS, images)
- REST service execution
- Concurrent client handling
- Error handling (404 responses)

#### 2. Framework Tests (`WebApplicationTest`)

- Service registration with lambda functions
- Query parameter extraction
- Static file configuration
- Complex business logic in services
- Multiple service coexistence

#### 3. Functionality Tests (`WebFrameworkFunctionalityTest`)

- Framework specification compliance
- Edge case parameter handling
- URL encoding support
- Service overwriting behavior
- GET/POST service independence

### Running Tests

Execute all tests:

```bash
mvn test
```

Run specific test class:

```bash
mvn test -Dtest=WebApplicationTest
```

View test results:

```bash
# Test reports are generated in target/surefire-reports/
```

### Test Coverage

The test suite includes **53 tests** covering:

- ✅ REST service registration and execution
- ✅ Query parameter extraction (`getValue()`)
- ✅ Static file serving for all content types
- ✅ Concurrent request handling
- ✅ Error handling and edge cases
- ✅ Framework API compliance
- ✅ Lambda function complex logic

## Performance and Scalability

### Current Limitations

- **Single-threaded processing** - Handles one request at a time
- **Memory-based file serving** - Loads entire files into memory
- **No connection pooling** - Creates new socket for each request

## Development Guidelines

### Adding New Services

To add a new REST service:

```java
// In your main application
HttpServer.get("/myservice", (req, res) -> {
    String param = req.getValue("param");
    // Your business logic here
    return "Response: " + param;
});
```

### Error Handling

Services should handle errors gracefully:

```java
HttpServer.get("/calculate", (req, res) -> {
    try {
        int a = Integer.parseInt(req.getValue("a"));
        int b = Integer.parseInt(req.getValue("b"));
        return String.valueOf(a + b);
    } catch (NumberFormatException e) {
        return "Error: Invalid numbers";
    }
});
```

### Static File Organization

Place static files in `src/main/java/resources/`:

```
resources/
├── index.html          # Main page
├── styles/            # CSS files
├── scripts/           # JavaScript files
└── images/            # Image assets
```

### Important

When you use the staticFiles method, if for example you type staticFiles("/webroot");
the system will automatically create the directory in `target/classes/webroot` and copy everything in `src/main/java/resources/` to the new location to have default files.

```
http://localhost:35000/usuario.png
http://localhost:35000/favicon.ico
http://localhost:35000/otroUsuario.jpg
```

Returns binary image content with appropriate `Content-Type: image/*`

}

### Content Type Handling

## Content Type Support

The server automatically detects and serves content with proper MIME types:

| File Extension | Content-Type      | Description      |
| -------------- | ----------------- | ---------------- |
| `.html`        | `text/html`       | HTML documents   |
| `.css`         | `text/css`        | Stylesheet files |
| `.js`          | `text/javascript` | JavaScript files |
| `.png`         | `image/png`       | PNG images       |
| `.jpg/.jpeg`   | `image/jpg`       | JPEG images      |
| `.ico`         | `image/ico`       | Icon files       |

## Deployment

To deploy the server on a production system:

1. **Build the project:**

   ```bash
   mvn clean package
   ```

2. **Run the application:**

   ```bash
   java -cp target/classes com.mycompany.httpserver.WebApplication.WebApplication
   ```

3. **Verify deployment:**

   ```bash
   curl http://localhost:35000/app/pi
   # Should return: 3.141592653589793
   ```

   s

4. **Configure the firewall** to allow connections on port 35000

**Note:** For production, consider changing the default port by editing the `PORT` constant in `HttpServer.java`

## Features

The HTTP server implements the following characteristics:

- ✅ Serve static HTML files
- ✅ Serve CSS files for styling
- ✅ Serve JavaScript files
- ✅ Serve images (PNG, JPG, ICO)
- ✅ Simple REST service (`/app/hello?name=value`)
- ✅ Handle 404 errors
- ✅ Serve get method
- ✅ Extract query params
- ✅ set static files directory

## Built With

- **[Java 21](https://openjdk.org/projects/jdk/21/)** - Programming language and runtime
- **[Maven](https://maven.apache.org/)** - Dependency management and build tool
- **[JUnit 5](https://junit.org/junit5/)** - Testing framework
- **Java Socket API** - Network communication
- **Java NIO** - File system operations

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Standards

- Follow Java naming conventions
- Write comprehensive tests for new features
- Update documentation for API changes
- Ensure all tests pass before submitting

## Authors

- **Sebastian Cardona** - _Initial work_ - [SebastianCardona-P](https://github.com/SebastianCardona-P)

## Acknowledgments

- AREP course at Escuela Colombiana de Ingeniería Julio Garavito
- HTTP/1.1 specification (RFC 2616)
- Modern web framework design pattern

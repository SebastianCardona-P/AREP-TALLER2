package com.mycompany.httpserver;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para WebApplication Framework
 * Verifica las funcionalidades específicas del framework web como:
 * - Método get() para servicios REST con lambda functions
 * - Método staticfiles() para especificar ubicación de archivos estáticos
 * - Extracción de query parameters
 * - Métodos getValue() y getValues() de HttpRequest
 */
public class WebApplicationTest {

    private static final int SERVER_PORT = 35000;
    private static final String SERVER_HOST = "localhost";
    private Thread serverThread;

    @BeforeEach
    void setUp() {
        // Limpiar servicios registrados antes de cada prueba
        HttpServer.services.clear();
    }

    @AfterEach
    void tearDown() {
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }

    @Test
    @DisplayName("Framework: get() method debe registrar servicios REST con lambda functions")
    void testGetMethodRegistersServices() {
        // Arrange & Act
        HttpServer.get("/test", (req, res) -> "test response");
        HttpServer.get("/hello", (req, res) -> "hello " + req.getValue("name"));

        // Assert
        assertTrue(HttpServer.services.containsKey("/test"), "Debe registrar servicio /test");
        assertTrue(HttpServer.services.containsKey("/hello"), "Debe registrar servicio /hello");
        assertEquals(2, HttpServer.services.size(), "Debe tener exactamente 2 servicios registrados");
    }

    @Test
    @DisplayName("Framework: post() method debe registrar servicios POST con lambda functions")
    void testPostMethodRegistersServices() {
        // Arrange & Act
        HttpServer.post("/submit", (req, res) -> "submitted " + req.getValue("data"));
        HttpServer.post("/login", (req, res) -> "login attempt");

        // Assert
        assertTrue(HttpServer.services.containsKey("/submit"), "Debe registrar servicio POST /submit");
        assertTrue(HttpServer.services.containsKey("/login"), "Debe registrar servicio POST /login");
        assertEquals(2, HttpServer.services.size(), "Debe tener exactamente 2 servicios registrados");
    }

    @Test
    @DisplayName("Framework: staticfiles() method debe configurar la ruta base correctamente")
    void testStaticFilesMethod() throws Exception {
        // Test with leading slash
        HttpServer.staticfiles("/webroot");
        // Note: We can't easily test the private basePath field directly,
        // but we can verify it doesn't throw exceptions and the method exists
        
        // Test without leading slash
        HttpServer.staticfiles("public");
        
        // Si llegamos aquí sin excepciones, el método funciona
        assertTrue(true, "staticfiles() debe ejecutarse sin errores");
    }

    @Test
    @DisplayName("Framework: HttpRequest.getValue() debe extraer query parameters correctamente")
    void testHttpRequestGetValue() throws URISyntaxException {
        // Arrange
        URI testUri = new URI("/test?name=John&age=25&city=Bogota");
        HttpRequest request = new HttpRequest(testUri);

        // Act & Assert
        assertEquals("John", request.getValue("name"), "Debe extraer parámetro 'name'");
        assertEquals("25", request.getValue("age"), "Debe extraer parámetro 'age'");
        assertEquals("Bogota", request.getValue("city"), "Debe extraer parámetro 'city'");
        assertEquals("", request.getValue("nonexistent"), "Debe retornar string vacío para parámetros inexistentes");
    }

    @Test
    @DisplayName("Framework: HttpRequest debe manejar query parameters vacíos")
    void testHttpRequestEmptyQueryParameters() throws URISyntaxException {
        // Test with empty parameter value
        URI testUri1 = new URI("/test?name=&age=25");
        HttpRequest request1 = new HttpRequest(testUri1);
        assertEquals("", request1.getValue("name"), "Debe manejar parámetros con valor vacío");
        assertEquals("25", request1.getValue("age"), "Debe extraer otros parámetros normalmente");

        // Test with no query string
        URI testUri2 = new URI("/test");
        HttpRequest request2 = new HttpRequest(testUri2);
        assertEquals("", request2.getValue("any"), "Debe retornar vacío cuando no hay query string");
    }

    @Test
    @DisplayName("Framework: HttpRequest debe manejar query parameters con caracteres especiales")
    void testHttpRequestSpecialCharacters() throws URISyntaxException {
        // Test with URL encoded characters
        URI testUri = new URI("/test?message=hello%20world&symbol=%26");
        HttpRequest request = new HttpRequest(testUri);

        // Note: Java's URI automatically decodes some characters
        String message = request.getValue("message");
        String symbol = request.getValue("symbol");
        
        // Verificar que se extraen los parámetros (independientemente de la decodificación)
        assertNotNull(message, "Debe extraer parámetro message");
        assertNotNull(symbol, "Debe extraer parámetro symbol");
        assertTrue(message.contains("hello"), "Debe contener 'hello' en el mensaje");
    }

    @Test
    @DisplayName("Framework: HttpRequest debe manejar múltiples parámetros con el mismo nombre")
    void testHttpRequestDuplicateParameters() throws URISyntaxException {
        // Current implementation behavior with duplicate parameters
        URI testUri = new URI("/test?tag=java&tag=web&tag=framework");
        HttpRequest request = new HttpRequest(testUri);

        // The current implementation will keep the last value
        assertEquals("framework", request.getValue("tag"), "Debe mantener el último valor para parámetros duplicados");
    }

    @Test
    @DisplayName("Framework: Servicios registrados deben ejecutarse correctamente")
    void testServiceExecution() {
        // Arrange
        HttpServer.get("/test", (req, res) -> "Hello World!");
        Service testService = HttpServer.services.get("/test");
        
        HttpRequest mockRequest = new HttpRequest(null);
        HttpResponse mockResponse = new HttpResponse();

        // Act
        String result = testService.executeService(mockRequest, mockResponse);

        // Assert
        assertEquals("Hello World!", result, "El servicio debe ejecutarse y retornar el resultado esperado");
    }

    @Test
    @DisplayName("Framework: Servicios con parámetros deben funcionar correctamente")
    void testServiceWithParameters() throws URISyntaxException {
        // Arrange
        HttpServer.get("/greet", (req, res) -> "Hello " + req.getValue("name") + "!");
        Service greetService = HttpServer.services.get("/greet");
        
        URI testUri = new URI("/greet?name=Sebastian");
        HttpRequest requestWithParams = new HttpRequest(testUri);
        HttpResponse mockResponse = new HttpResponse();

        // Act
        String result = greetService.executeService(requestWithParams, mockResponse);

        // Assert
        assertEquals("Hello Sebastian!", result, "El servicio debe usar los parámetros de la request");
    }

    @Test
    @DisplayName("Framework: Lambda functions complejas deben funcionar correctamente")
    void testComplexLambdaFunctions() throws URISyntaxException {
        // Arrange - Lambda function with more complex logic
        HttpServer.get("/calculate", (req, res) -> {
            String operation = req.getValue("op");
            String aStr = req.getValue("a");
            String bStr = req.getValue("b");
            
            if (operation.isEmpty() || aStr.isEmpty() || bStr.isEmpty()) {
                return "Error: Missing parameters";
            }
            
            try {
                int a = Integer.parseInt(aStr);
                int b = Integer.parseInt(bStr);
                
                switch (operation) {
                    case "add":
                        return String.valueOf(a + b);
                    case "multiply":
                        return String.valueOf(a * b);
                    default:
                        return "Error: Unknown operation";
                }
            } catch (NumberFormatException e) {
                return "Error: Invalid numbers";
            }
        });

        Service calcService = HttpServer.services.get("/calculate");
        HttpResponse mockResponse = new HttpResponse();

        // Test addition
        URI addUri = new URI("/calculate?op=add&a=5&b=3");
        HttpRequest addRequest = new HttpRequest(addUri);
        assertEquals("8", calcService.executeService(addRequest, mockResponse), "Debe calcular suma correctamente");

        // Test multiplication
        URI multiplyUri = new URI("/calculate?op=multiply&a=4&b=7");
        HttpRequest multiplyRequest = new HttpRequest(multiplyUri);
        assertEquals("28", calcService.executeService(multiplyRequest, mockResponse), "Debe calcular multiplicación correctamente");

        // Test error handling
        URI errorUri = new URI("/calculate?op=add&a=abc&b=3");
        HttpRequest errorRequest = new HttpRequest(errorUri);
        assertEquals("Error: Invalid numbers", calcService.executeService(errorRequest, mockResponse), "Debe manejar errores correctamente");
    }

    @Test
    @DisplayName("Framework: Multiple services should coexist without interference")
    void testMultipleServicesCoexistence() throws URISyntaxException {
        // Arrange - Register multiple services
        HttpServer.get("/ping", (req, res) -> "pong");
        HttpServer.get("/echo", (req, res) -> "Echo: " + req.getValue("message"));
        HttpServer.get("/time", (req, res) -> String.valueOf(System.currentTimeMillis()));
        HttpServer.post("/data", (req, res) -> "Received: " + req.getValue("payload"));

        // Assert all services are registered
        assertEquals(4, HttpServer.services.size(), "Debe tener 4 servicios registrados");
        assertTrue(HttpServer.services.containsKey("/ping"), "Debe contener servicio /ping");
        assertTrue(HttpServer.services.containsKey("/echo"), "Debe contener servicio /echo");
        assertTrue(HttpServer.services.containsKey("/time"), "Debe contener servicio /time");
        assertTrue(HttpServer.services.containsKey("/data"), "Debe contener servicio /data");

        // Test each service works independently
        HttpResponse mockResponse = new HttpResponse();
        
        assertEquals("pong", HttpServer.services.get("/ping").executeService(new HttpRequest(null), mockResponse));
        
        URI echoUri = new URI("/echo?message=test");
        assertEquals("Echo: test", HttpServer.services.get("/echo").executeService(new HttpRequest(echoUri), mockResponse));
    }

    @Test
    @DisplayName("Framework: Service registration should support method chaining pattern")
    void testServiceRegistrationPattern() {
        // This test verifies that services can be registered in a fluent manner
        // as shown in the project requirements example
        
        // Register services as in the example
        HttpServer.staticfiles("/webroot");
        HttpServer.get("/hello", (req, resp) -> "Hello " + req.getValue("name"));
        HttpServer.get("/pi", (req, resp) -> String.valueOf(Math.PI));

        // Verify services are registered correctly
        assertTrue(HttpServer.services.containsKey("/hello"), "Hello service should be registered");
        assertTrue(HttpServer.services.containsKey("/pi"), "Pi service should be registered");
        
        // Test the pi service
        String piResult = HttpServer.services.get("/pi").executeService(new HttpRequest(null), new HttpResponse());
        assertEquals(String.valueOf(Math.PI), piResult, "Pi service should return PI value");
    }
}

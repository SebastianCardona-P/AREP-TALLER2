package com.mycompany.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mycompany.httpserver.WebApplication.WebApplication;

/**
 * Pruebas de integración para HttpServer
 * Verifica el funcionamiento end-to-end del sistema según el README
 */
public class HttpServerIntegrationTest {

    private static Thread serverThread;
    private static final int SERVER_PORT = 35000;
    private static final String SERVER_HOST = "localhost";
    private static final int STARTUP_DELAY = 2000; // 2 segundos para que inicie el servidor

    @BeforeAll
    static void startServer() {
        serverThread = new Thread(() -> {
            try {
                WebApplication.main(new String[]{});
            } catch (Exception e) {
                // El servidor puede terminar abruptamente durante las pruebas
                System.out.println("Servidor terminado: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true); // Permitir que el programa termine
        serverThread.start();
        
        // Esperar a que el servidor inicie
        try {
            Thread.sleep(STARTUP_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterAll
    static void stopServer() {
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }

    @Test
    @DisplayName("End-to-end: Servidor debe servir página principal correctamente")
    void testServeHomePage() throws IOException {
        String response = makeHttpRequest("GET / HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        
        assertNotNull(response, "Debe recibir respuesta");
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Debe retornar 200 OK");
        assertTrue(response.contains("text/html"), "Debe ser contenido HTML");
        assertTrue(response.contains("Form Example For First AREP Lab"), "Debe contener el título de la página");
    }

    @Test
    @DisplayName("End-to-end: Servidor debe servir archivos CSS correctamente")
    void testServeCSSFiles() throws IOException {
        String response = makeHttpRequest("GET /styles/style.css HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        
        assertNotNull(response, "Debe recibir respuesta CSS");
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Debe retornar 200 OK para CSS");
        assertTrue(response.contains("text/css"), "Debe identificar content-type como CSS");
    }

    @Test
    @DisplayName("End-to-end: Servidor debe servir archivos JavaScript correctamente")
    void testServeJavaScriptFiles() throws IOException {
        String response = makeHttpRequest("GET /scripts/script.js HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        
        assertNotNull(response, "Debe recibir respuesta JavaScript");
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Debe retornar 200 OK para JS");
        assertTrue(response.contains("text/javascript"), "Debe identificar content-type como JavaScript");
    }

    @Test
    @DisplayName("End-to-end: Servidor debe servir imágenes PNG correctamente")
    void testServeImageFiles() throws IOException {
        String response = makeHttpRequest("GET /images/usuario.png HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        
        assertNotNull(response, "Debe recibir respuesta de imagen");
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Debe retornar 200 OK para imágenes");
        assertTrue(response.contains("image/png"), "Debe identificar content-type como PNG");
    }

    @Test
    @DisplayName("End-to-end: Servicio REST /app/hello debe funcionar correctamente")
    void testRESTServiceHello() throws IOException {
        String response = makeHttpRequest("GET /app/hello?name=Michael&age=18 HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        
        assertNotNull(response, "Debe recibir respuesta del servicio REST");
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Debe retornar 200 OK para REST");
        assertTrue(response.contains("application/json"), "Debe retornar JSON");
        assertTrue(response.contains("hello Michael you are 18 years old"), "Debe personalizar el saludo");
    }

    @Test
    @DisplayName("End-to-end: Servidor debe manejar errores 404 correctamente")
    void testNotFoundHandling() throws IOException {
        String response = makeHttpRequest("GET /archivo-que-no-existe.html HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        
        assertNotNull(response, "Debe recibir respuesta 404");
        assertTrue(response.contains("HTTP/1.1 404 Not Found"), "Debe retornar 404 Not Found");
        assertTrue(response.contains("404 Not Found"), "Debe contener mensaje de error");
    }

    @Test
    @DisplayName("End-to-end: Servidor debe soportar múltiples clientes concurrentes")
    void testConcurrentClients() throws InterruptedException {
        final int NUM_CLIENTS = 5;
        Thread[] clientThreads = new Thread[NUM_CLIENTS];
        final boolean[] results = new boolean[NUM_CLIENTS];
        
        for (int i = 0; i < NUM_CLIENTS; i++) {
            final int clientId = i;
            clientThreads[i] = new Thread(() -> {
                try {
                    String response = makeHttpRequest("GET /app/hello?name=Client" + clientId + " HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
                    results[clientId] = response.contains("HTTP/1.1 200 OK") && 
                                       response.contains("hello Client" + clientId);
                } catch (IOException e) {
                    results[clientId] = false;
                }
            });
        }
        
        // Iniciar todos los clientes
        for (Thread thread : clientThreads) {
            thread.start();
        }
        
        // Esperar a que terminen
        for (Thread thread : clientThreads) {
            thread.join(5000); // Timeout de 5 segundos
        }
        
        // Verificar que todos los clientes fueron atendidos correctamente
        for (int i = 0; i < NUM_CLIENTS; i++) {
            assertTrue(results[i], "Cliente " + i + " debe recibir respuesta correcta");
        }
    }

    @Test
    @DisplayName("End-to-end: Servidor debe manejar diferentes tipos de imagen")
    void testMultipleImageTypes() throws IOException {
        // Probar PNG
        String pngResponse = makeHttpRequest("GET /usuario.png HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        assertTrue(pngResponse.contains("image/png"), "Debe servir PNG correctamente");
        
        // Probar JPG
        String jpgResponse = makeHttpRequest("GET /otroUsuario.jpg HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        assertTrue(jpgResponse.contains("image/jpg"), "Debe servir JPG correctamente");
    }

    @Test
    @DisplayName("End-to-end: Servicio REST debe manejar parámetros vacíos")
    void testRESTServiceEmptyParameters() throws IOException {
        String response = makeHttpRequest("GET /app/hello?name= HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n");
        
        assertNotNull(response, "Debe manejar parámetros vacíos");
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Debe retornar 200 OK");
        assertTrue(response.contains("hello "), "Debe manejar nombre vacío");
    }

    /**
     * Método auxiliar para realizar peticiones HTTP
     */
    private String makeHttpRequest(String request) throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Enviar petición
            out.print(request);
            out.flush();
            
            // Leer respuesta
            StringBuilder response = new StringBuilder();
            String line;
            
            // Leer hasta que no haya más datos o se cierre la conexión
            try {
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\r\n");
                    // Si encontramos una línea vacía y luego contenido, leer el contenido
                    if (line.isEmpty() && in.ready()) {
                        char[] buffer = new char[1024];
                        int bytesRead = in.read(buffer);
                        if (bytesRead > 0) {
                            response.append(new String(buffer, 0, bytesRead));
                        }
                        break;
                    }
                }
            } catch (SocketTimeoutException | SocketException e) {
                // Conexión cerrada por el servidor, esto es normal
            }
            
            return response.toString();
        }
    }
}

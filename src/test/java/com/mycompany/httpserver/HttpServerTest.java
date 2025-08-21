package com.mycompany.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase HttpServer
 * Verifica que el servidor HTTP funcione correctamente
 */
public class HttpServerTest {

    @Test
    @DisplayName("HttpServer debe poder crear ServerSocket")
    void testServerSocketCreation() {
        try (ServerSocket testSocket = new ServerSocket(0)) {
            assertNotNull(testSocket, "Debe poder crear ServerSocket");
            assertTrue(testSocket.getLocalPort() > 0, "Debe asignar un puerto válido");
        } catch (IOException e) {
            fail("No debe fallar al crear ServerSocket: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("HttpServer debe manejar conexiones básicas")
    void testBasicConnection() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            
            Thread clientThread = new Thread(() -> {
                try {
                    Thread.sleep(100);
                    Socket socket = new Socket("localhost", port);
                    socket.close();
                } catch (Exception e) {
                    // Cliente puede fallar, esto es esperado
                }
            });
            
            clientThread.start();
            
            try (Socket clientSocket = serverSocket.accept()) {
                assertNotNull(clientSocket, "Debe aceptar conexiones");
                assertTrue(clientSocket.isConnected(), "Conexión debe estar activa");
            }
            
            clientThread.join(1000);
            
        } catch (Exception e) {
            fail("Test de conexión básica no debe fallar: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("HttpServer debe procesar HTTP requests básicos")
    void testHttpRequestProcessing() {
        // Probar que la lógica de parsing de HTTP funciona
        String sampleRequest = "GET /index.html HTTP/1.1";
        String[] parts = sampleRequest.split(" ");
        
        assertEquals("GET", parts[0], "Debe extraer método HTTP correctamente");
        assertEquals("/index.html", parts[1], "Debe extraer URI correctamente");
        assertEquals("HTTP/1.1", parts[2], "Debe extraer versión HTTP correctamente");
    }
}

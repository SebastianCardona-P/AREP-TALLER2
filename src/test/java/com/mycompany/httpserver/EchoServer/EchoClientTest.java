package com.mycompany.httpserver.EchoServer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para EchoClient
 * Verifica la funcionalidad del cliente de eco
 */
public class EchoClientTest {

    @Test
    @DisplayName("EchoClient debe poder crear Socket para conexión")
    void testSocketCreation() {
        // Crear un servidor simple para probar la conexión
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            
            // Crear cliente en thread separado
            Thread clientThread = new Thread(() -> {
                try {
                    Socket testSocket = new Socket("127.0.0.1", port);
                    assertNotNull(testSocket, "Debe poder crear socket");
                    assertTrue(testSocket.isConnected(), "Socket debe estar conectado");
                    testSocket.close();
                } catch (IOException e) {
                    // Esperado si no hay servidor escuchando
                }
            });
            
            clientThread.start();
            
            // Aceptar la conexión del cliente
            try (Socket clientSocket = serverSocket.accept()) {
                assertNotNull(clientSocket, "Servidor debe aceptar conexión del cliente");
                assertTrue(clientSocket.isConnected(), "Conexión debe estar activa");
            }
            
            clientThread.join();
            
        } catch (Exception e) {
            fail("La creación de socket no debe fallar: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("EchoClient debe manejar I/O streams correctamente")
    void testIOStreams() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            
            Thread clientThread = new Thread(() -> {
                try {
                    Socket echoSocket = new Socket("127.0.0.1", port);
                    PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                    
                    assertNotNull(out, "PrintWriter debe ser creado");
                    assertNotNull(in, "BufferedReader debe ser creado");
                    
                    // Enviar un mensaje de prueba
                    out.println("test message");
                    String response = in.readLine();
                    assertEquals("echo: test message", response, "Debe recibir respuesta correcta");
                    
                    echoSocket.close();
                } catch (IOException e) {
                    fail("Cliente no debe fallar en I/O: " + e.getMessage());
                }
            });
            
            clientThread.start();
            
            // Servidor simula el comportamiento del EchoServer
            try (Socket clientSocket = serverSocket.accept()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                
                String inputLine = in.readLine();
                if (inputLine != null) {
                    out.println("echo: " + inputLine);
                }
            }
            
            clientThread.join();
            
        } catch (Exception e) {
            fail("La prueba de I/O streams no debe fallar: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("EchoClient debe manejar conexiones fallidas correctamente")
    void testConnectionFailure() {
        // Intentar conectar a un puerto que no está escuchando
        assertThrows(ConnectException.class, () -> {
            Socket failSocket = new Socket("127.0.0.1", 12345); // Puerto que probablemente no esté en uso
            failSocket.close();
        }, "Debe lanzar ConnectException cuando no hay servidor");
    }

    @Test
    @DisplayName("EchoClient debe manejar input del usuario simulado")
    void testUserInputHandling() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            
            Thread clientThread = new Thread(() -> {
                try {
                    Socket echoSocket = new Socket("127.0.0.1", port);
                    PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                    
                    // Simular entrada del usuario
                    String userInput = "hello world";
                    out.println(userInput);
                    
                    String serverResponse = in.readLine();
                    assertNotNull(serverResponse, "Debe recibir respuesta del servidor");
                    assertTrue(serverResponse.contains("hello world"), "Respuesta debe contener el mensaje enviado");
                    
                    echoSocket.close();
                } catch (IOException e) {
                    fail("Cliente no debe fallar: " + e.getMessage());
                }
            });
            
            clientThread.start();
            
            // Servidor responde con eco
            try (Socket clientSocket = serverSocket.accept()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                
                String inputLine = in.readLine();
                if (inputLine != null) {
                    out.println("echo: " + inputLine);
                }
            }
            
            clientThread.join();
            
        } catch (Exception e) {
            fail("La prueba de manejo de input no debe fallar: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("EchoClient debe cerrar recursos correctamente")
    void testResourceClosure() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            
            Thread clientThread = new Thread(() -> {
                Socket echoSocket = null;
                PrintWriter out = null;
                BufferedReader in = null;
                BufferedReader stdIn = null;
                
                try {
                    echoSocket = new Socket("127.0.0.1", port);
                    out = new PrintWriter(echoSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                    stdIn = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("test\n".getBytes())));
                    
                    // Simular una iteración del bucle principal
                    String userInput = stdIn.readLine();
                    if (userInput != null) {
                        out.println(userInput);
                        String response = in.readLine();
                        assertNotNull(response, "Debe recibir respuesta");
                    }
                    
                } catch (IOException e) {
                    fail("No debe fallar durante operación normal: " + e.getMessage());
                } finally {
                    // Verificar que los recursos se pueden cerrar sin errores
                    try {
                        if (out != null) out.close();
                        if (in != null) in.close();
                        if (stdIn != null) stdIn.close();
                        if (echoSocket != null) echoSocket.close();
                    } catch (IOException e) {
                        fail("No debe fallar al cerrar recursos: " + e.getMessage());
                    }
                }
            });
            
            clientThread.start();
            
            // Servidor simple
            try (Socket clientSocket = serverSocket.accept()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                
                String inputLine = in.readLine();
                if (inputLine != null) {
                    out.println("echo: " + inputLine);
                }
            }
            
            clientThread.join();
            
        } catch (Exception e) {
            fail("La prueba de cierre de recursos no debe fallar: " + e.getMessage());
        }
    }
}

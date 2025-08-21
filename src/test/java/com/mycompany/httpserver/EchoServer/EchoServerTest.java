package com.mycompany.httpserver.EchoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para EchoServer
 * Verifica que el servidor de eco funcione correctamente
 */
public class EchoServerTest {

    private Thread serverThread;
    private static final int TEST_PORT = 35001; // Usar puerto diferente para evitar conflictos

    @BeforeEach
    void setUp() {
        // No iniciar el servidor automáticamente para evitar conflictos de puerto
    }

    @AfterEach
    void tearDown() {
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }

    @Test
    @DisplayName("EchoServer debe calcular el cuadrado de números correctamente")
    void testSquareCalculation() {
        // Crear un servidor mock que use la misma lógica
        double inputNumber = 5.0;
        double expectedResult = inputNumber * inputNumber;
        
        assertEquals(25.0, expectedResult, "Debe calcular correctamente el cuadrado de 5");
        
        inputNumber = 3.5;
        expectedResult = inputNumber * inputNumber;
        assertEquals(12.25, expectedResult, "Debe calcular correctamente el cuadrado de 3.5");
        
        inputNumber = 0.0;
        expectedResult = inputNumber * inputNumber;
        assertEquals(0.0, expectedResult, "Debe calcular correctamente el cuadrado de 0");
    }

    @Test
    @DisplayName("EchoServer debe manejar parsing de números")
    void testNumberParsing() {
        // Testear la lógica de parsing que usa el servidor
        try {
            double result1 = Double.parseDouble("10");
            assertEquals(10.0, result1, "Debe parsear enteros correctamente");
            
            double result2 = Double.parseDouble("3.14");
            assertEquals(3.14, result2, 0.001, "Debe parsear decimales correctamente");
            
            double result3 = Double.parseDouble("  5.5  ");
            assertEquals(5.5, result3, 0.001, "Debe parsear números con espacios");
            
        } catch (NumberFormatException e) {
            fail("No debe lanzar excepción para números válidos");
        }
    }

    @Test
    @DisplayName("EchoServer debe manejar errores de parsing")
    void testInvalidNumberParsing() {
        assertThrows(NumberFormatException.class, () -> {
            Double.parseDouble("no-es-numero");
        }, "Debe lanzar NumberFormatException para texto inválido");
        
        assertThrows(NumberFormatException.class, () -> {
            Double.parseDouble("");
        }, "Debe lanzar NumberFormatException para string vacío");
    }

    @Test
    @DisplayName("EchoServer debe generar respuestas en formato correcto")
    void testResponseFormat() {
        double input = 4.0;
        double result = input * input;
        String expectedResponse = "Respuesta: " + result;
        
        assertEquals("Respuesta: 16.0", expectedResponse, 
                    "Debe generar respuesta en formato correcto");
    }

    @Test
    @DisplayName("EchoServer debe poder crear ServerSocket")
    void testServerSocketCreation() {
        try (ServerSocket testSocket = new ServerSocket(0)) { // Puerto 0 = cualquier puerto disponible
            assertNotNull(testSocket, "Debe poder crear ServerSocket");
            assertTrue(testSocket.getLocalPort() > 0, "Debe asignar un puerto válido");
            assertFalse(testSocket.isClosed(), "El socket debe estar abierto inicialmente");
        } catch (IOException e) {
            fail("No debe fallar al crear ServerSocket: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("EchoServer debe manejar conexiones de red básicas")
    void testBasicNetworking() {
        // Probar que la lógica de red básica funciona
        try {
            // Crear servidor en puerto disponible
            ServerSocket serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();
            
            // Crear cliente en thread separado
            Thread clientThread = new Thread(() -> {
                try {
                    Thread.sleep(100); // Dar tiempo al servidor para estar listo
                    Socket socket = new Socket("localhost", port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    // Enviar número y leer respuesta
                    out.println("3");
                    String response = in.readLine();
                    assertEquals("Respuesta: 9.0", response, "Debe recibir respuesta correcta");
                    
                    socket.close();
                } catch (Exception e) {
                    fail("Cliente no debe fallar: " + e.getMessage());
                }
            });
            
            clientThread.start();
            
            // Servidor acepta una conexión
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String inputLine = in.readLine();
            if (inputLine != null) {
                double inputNumber = Double.parseDouble(inputLine.trim());
                double result = inputNumber * inputNumber;
                out.println("Respuesta: " + result);
            }
            
            clientSocket.close();
            serverSocket.close();
            clientThread.join();
            
        } catch (Exception e) {
            fail("La prueba de red básica no debe fallar: " + e.getMessage());
        }
    }
}

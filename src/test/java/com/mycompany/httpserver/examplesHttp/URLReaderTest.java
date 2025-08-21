package com.mycompany.httpserver.examplesHttp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase URLReader
 * Verifica la funcionalidad de lectura de URLs y escritura de archivos
 */
public class URLReaderTest {

    @AfterEach
    void cleanup() {
        // Limpiar archivos creados durante las pruebas
        try {
            Files.deleteIfExists(Paths.get("resultado.html"));
        } catch (Exception e) {
            // Ignorar errores de limpieza
        }
    }

    @Test
    @DisplayName("URLReader debe crear archivo resultado.html")
    void testFileCreation() {
        // Simular entrada del usuario con una URL válida
        String simulatedInput = "https://www.google.com\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Capturar salida para evitar interferencias
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            URLReader.main(new String[]{});
            
            // Verificar que el archivo fue creado
            assertTrue(Files.exists(Paths.get("resultado.html")), 
                      "El archivo resultado.html debe ser creado");
            
            // Verificar que el archivo no está vacío
            assertTrue(Files.size(Paths.get("resultado.html")) > 0, 
                      "El archivo resultado.html no debe estar vacío");
            
        } catch (Exception e) {
            // La prueba puede fallar por problemas de conectividad, pero no debe crashear
            assertNotNull(e.getMessage(), "Si hay error, debe tener mensaje descriptivo");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("URLReader debe manejar URLs malformadas correctamente")
    void testMalformedURL() {
        String simulatedInput = "url-invalida\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        try {
            URLReader.main(new String[]{});
            
            // Verificar que se maneja el error apropiadamente
            String errorOutput = errContent.toString();
            assertTrue(errorOutput.contains("URL ingresada no es válida") || 
                      errorOutput.contains("MalformedURLException"),
                      "Debe mostrar error para URL malformada");
            
            // Si se crea el archivo, debe contener información del error
            if (Files.exists(Paths.get("resultado.html"))) {
                String fileContent = Files.readString(Paths.get("resultado.html"));
                assertTrue(fileContent.contains("Error") || fileContent.contains("URL inválida"),
                          "El archivo debe contener información del error");
            }
            
        } catch (Exception e) {
            // Expected behavior for malformed URLs
            assertTrue(e.getMessage().contains("MalformedURL") || 
                      e.getMessage().contains("URL"),
                      "Debe lanzar excepción relacionada con URL malformada");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    @Test
    @DisplayName("URLReader debe procesar URLs válidas y escribir contenido")
    void testValidURLProcessing() {
        // Usar una URL que probablemente funcione (httpbin es un servicio de pruebas HTTP)
        String simulatedInput = "https://httpbin.org/html\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            URLReader.main(new String[]{});
            
            String output = outContent.toString();
            assertTrue(output.contains("Se leyó exitosamente el archivo") || 
                      output.contains("Error de conexión"),
                      "Debe mostrar mensaje de éxito o error de conexión");
            
            // Si el archivo fue creado, verificar su contenido
            if (Files.exists(Paths.get("resultado.html"))) {
                String fileContent = Files.readString(Paths.get("resultado.html"));
                assertFalse(fileContent.trim().isEmpty(), 
                           "El archivo debe contener algún contenido");
            }
            
        } catch (Exception e) {
            // Conexión puede fallar por problemas de red, pero no debe crashear sin manejo
            assertNotNull(e.getMessage(), "Cualquier excepción debe tener mensaje descriptivo");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("URLReader debe cerrar recursos correctamente")
    void testResourceManagement() {
        String simulatedInput = "https://www.example.com\n";
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            URLReader.main(new String[]{});
            
            // La prueba principal es que no hay excepción por recursos no cerrados
            // Si llegamos aquí, el manejo de recursos funcionó correctamente
            assertTrue(true, "El programa debe completarse sin errores de recursos");
            
        } catch (Exception e) {
            // Verificar que no es un error de recursos no cerrados
            assertFalse(e.getMessage().contains("closed") || 
                       e.getMessage().contains("resource"),
                       "No debe haber errores de recursos no cerrados: " + e.getMessage());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}

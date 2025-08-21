package com.mycompany.httpserver.examplesHttp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;

/**
 * Pruebas unitarias para la clase URLParser
 * Verifica el correcto parsing de URLs
 */
public class URLParserTest {

    @Test
    @DisplayName("URLParser debe ejecutarse sin errores con una URL válida")
    void testURLParserMainMethod() {
        // Capturar la salida del sistema
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Ejecutar el método main
            URLParser.main(new String[]{});
            
            String output = outContent.toString();
            
            // Verificar que la salida contiene los componentes esperados de la URL
            assertTrue(output.contains("Protocol: http"), "Debe mostrar el protocolo HTTP");
            assertTrue(output.contains("Authority: michaelJordan.com:80"), "Debe mostrar la autoridad correcta");
            assertTrue(output.contains("Host: michaelJordan.com"), "Debe mostrar el host correcto");
            assertTrue(output.contains("Port: 80"), "Debe mostrar el puerto correcto");
            assertTrue(output.contains("Path: /mejoresJugadas.html"), "Debe mostrar el path correcto");
            assertTrue(output.contains("Query: anio=2016"), "Debe mostrar el query correcto");
            assertTrue(output.contains("File: /mejoresJugadas.html?anio=2016"), "Debe mostrar el file correcto");
            assertTrue(output.contains("Ref: 123"), "Debe mostrar la referencia correcta");
            
        } catch (Exception e) {
            fail("URLParser no debe lanzar excepciones con URLs válidas: " + e.getMessage());
        } finally {
            // Restaurar la salida original
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("URLParser debe demostrar el correcto parsing de componentes URL")
    void testURLParsingComponents() {
        // Este test verifica que la lógica de parsing funciona correctamente
        // al verificar la salida del programa
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            URLParser.main(new String[]{});
            String output = outContent.toString();
            
            // Contar el número de líneas de salida (debe ser 8: Protocol, Authority, Host, Port, Path, Query, File, Ref)
            String[] lines = output.trim().split("\n");
            assertEquals(8, lines.length, "Debe imprimir exactamente 8 componentes de la URL");
            
            // Verificar que cada línea tiene el formato esperado "Component: value"
            for (String line : lines) {
                assertTrue(line.contains(":"), "Cada línea debe contener ':' separando el componente del valor");
            }
            
        } catch (Exception e) {
            fail("No debe ocurrir ninguna excepción durante el parsing: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("URLParser debe manejar correctamente URLs con todos los componentes")
    void testCompleteURLParsing() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            URLParser.main(new String[]{});
            String output = outContent.toString();
            
            // Verificar que no hay valores nulos o vacíos en componentes importantes
            assertFalse(output.contains("null"), "No debe haber valores nulos en la salida");
            assertTrue(output.contains("michaelJordan.com"), "Debe contener el host especificado");
            assertTrue(output.contains("80"), "Debe contener el puerto especificado");
            assertTrue(output.contains("anio=2016"), "Debe contener los parámetros de query");
            
        } catch (Exception e) {
            fail("El parsing de URL completa no debe fallar: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }
}

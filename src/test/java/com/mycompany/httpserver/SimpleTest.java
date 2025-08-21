package com.mycompany.httpserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba simple para verificar que JUnit funciona
 */
public class SimpleTest {

    @Test
    void testBasicFunctionality() {
        assertEquals(2, 1 + 1, "1 + 1 debe ser 2");
        assertTrue(true, "Verdadero debe ser verdadero");
        assertNotNull("test", "String no debe ser nulo");
    }
    
    @Test
    void testHttpServerExists() {
        assertNotNull(HttpServer.class, "La clase HttpServer debe existir");
    }
}

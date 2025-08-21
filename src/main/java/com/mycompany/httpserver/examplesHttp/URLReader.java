package com.mycompany.httpserver.examplesHttp;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Sebastian Cardona
 */
public class URLReader {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingresa la URL a consultar");
        String site = scanner.nextLine();

        PrintWriter fileWriter = null;

        try {
            //crear archivo
            fileWriter = new PrintWriter("resultado.html");

            // Crea el objeto que representa una URL
            URL siteURL = new URL(site);
            // Crea el objeto que URLConnection
            URLConnection urlConnection = siteURL.openConnection();

            // Obtiene los campos del encabezado y los almacena en un estructura Map
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            // Obtiene una vista del mapa como conjunto de pares <K,V>
            // para poder navegarlo
            Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
            // Recorre la lista de campos e imprime los valores

            for (Map.Entry<String, List<String>> entry : entrySet) {
                String headerName = entry.getKey();
                //Si el nombre es nulo, significa que es la linea de estado
                if (headerName != null) {
                    fileWriter.print(headerName + ":");
                }
                List<String> headerValues = entry.getValue();
                for (String value : headerValues) {
                    fileWriter.print(value);
                }
                fileWriter.println("");
            }

            try (BufferedReader reader
                    = new BufferedReader(new InputStreamReader(siteURL.openStream()))) {
                String inputLine = null;
                while ((inputLine = reader.readLine()) != null) {
                    fileWriter.println(inputLine);
                }
            } catch (IOException x) {
                fileWriter.println(x.getMessage());
            }
            
            System.out.println("Se leyó exitosamente el archivo");

        } catch (MalformedURLException e) {
            System.err.println("Error: La URL ingresada no es válida - " + e.getMessage());
            if (fileWriter != null) {
                fileWriter.println("Error: URL inválida - " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error de conexión o escritura: " + e.getMessage());
            if (fileWriter != null) {
                fileWriter.println("Error de conexión: " + e.getMessage());
            }
        } finally {
            // Cerrar el archivo y el scanner
            if (fileWriter != null) {
                fileWriter.close();
            }
            scanner.close();
        }

    }
}

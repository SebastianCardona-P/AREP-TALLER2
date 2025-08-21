/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.httpserver.examplesHttp;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author curso
 */
public class URLParser {
    public static void main(String[] args) throws MalformedURLException {
        URL myUrl = new URL("http://michaelJordan.com:80/mejoresJugadas.html?anio=2016#123");
        
        System.out.println("Protocol: " + myUrl.getProtocol());
        System.out.println("Authority: " + myUrl.getAuthority());
        System.out.println("Host: " + myUrl.getHost());
        System.out.println("Port: " + myUrl.getPort());
        System.out.println("Path: " + myUrl.getPath());
        System.out.println("Query: " + myUrl.getQuery());
        System.out.println("File: " + myUrl.getFile());
        System.out.println("Ref: " + myUrl.getRef());
    }
}

package com.mycompany.httpserver.WebApplication;

/**
 *
 * @author sebastian.cardona-p
 */
import static com.mycompany.httpserver.HttpServer.get;
import static com.mycompany.httpserver.HttpServer.staticfiles;
import static com.mycompany.httpserver.HttpServer.startServer;
import java.io.IOException;
import java.net.URISyntaxException;

public class WebApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        staticfiles("/webroot/public");
        get("/onlyHello", (req, res) -> "hello world!");
        get("/hello", (req, resp) -> "hello " + req.getValue("name") +" you are " + req.getValue("age") + " years old");
        get("/pi", (req, resp) -> {
            return String.valueOf(Math.PI);
        });

        startServer(args);
    }

}

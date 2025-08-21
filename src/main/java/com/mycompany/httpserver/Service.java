package com.mycompany.httpserver;

/**
 *
 * @author sebastian.cardona-p
 */
public interface Service {
    public String executeService(HttpRequest req, HttpResponse res);
}

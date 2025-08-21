/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.httpserver;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sebastian.cardona-p
 */
public class HttpRequest {

    URI reuestUri = null;
    HttpRequest(URI requestUri) {
        reuestUri = requestUri;
    }
    public String getValue(String paramName) {
        String query = reuestUri.getQuery();
        if (query == null) {
            return "";
        }
        String[] queryParams = query.split("&");
        Map<String, String> queryMap = new HashMap<>();
        for (String param : queryParams) {
            String[] nameValue = param.split("=");
            if (nameValue.length == 2) {
                queryMap.put(nameValue[0], nameValue[1]);
            } else if (nameValue.length == 1) {
                queryMap.put(nameValue[0], "");
            }
        }

        return queryMap.get(paramName) != null ? queryMap.get(paramName) : "";
    }
}

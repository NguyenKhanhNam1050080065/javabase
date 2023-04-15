package com.cycastic.javabase.network;

import com.cycastic.javabase.dispatcher.AsyncEngine;
import com.cycastic.javabase.dispatcher.Command;
import com.cycastic.javabase.dispatcher.ExecutionLoop;
import com.cycastic.javabase.dispatcher.SafeFlag;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

class RequestPackage {
    private final int connectionTimeout;
    private final String url;
    private final Map<String, String> headers;
    private final boolean sslOnly;
    private final String method;
    private final String customData;
    private final HTTPResponseListener listener;

    private void emit(HTTPResponseListener l, int result, int response_code, Map<String, String> headers, String raw_response){
        if (l == null) return;
        l.request_completed(result, response_code, headers, raw_response);
    }
    public RequestPackage(int connectionTimeout, String url, Map<String, String> headers, boolean sslOnly, String method, String customData, HTTPResponseListener listener){
        this.connectionTimeout = connectionTimeout;
        this.url = url;
        this.headers = headers;
        this.sslOnly = sslOnly;
        this.method = method;
        this.customData = customData;
        this.listener = listener;
    }
    public void activate(){
        try {
            URL realUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection)realUrl.openConnection();

            if (Objects.equals(method, "PATCH")){
                con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                con.setRequestMethod("POST");
            } else con.setRequestMethod(method);

            con.setConnectTimeout(connectionTimeout);
            con.setReadTimeout(connectionTimeout);

            for (Map.Entry<String, String> E : headers.entrySet()){
                con.setRequestProperty(E.getKey(), E.getValue());
            }

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(customData);
            out.flush();
            out.close();

            int response_status = con.getResponseCode();
            Map<String, String> response_headers = new HashMap<>();
            con.getHeaderFields().entrySet().stream()
                    .filter(entry -> entry.getKey() != null)
                    .forEach(entry ->{
                        String response_key = entry.getKey();
                        StringBuilder response_value = new StringBuilder();
                        List<String> headers_values = entry.getValue();
                        Iterator<String> it = headers_values.iterator();
                        if (it.hasNext()){
                            response_value.append(it.next());
                            while (it.hasNext()){
                                response_value.append(", ").append(it.next());
                            }
                        }
                        response_headers.put(response_key, response_value.toString());
                    });

            Reader streamReader = null;
            if (response_status >= 300){
                // Failed
                streamReader = new InputStreamReader(con.getErrorStream());
            } else {
                // Succeeded
                streamReader = new InputStreamReader(con.getInputStream());
            }
            BufferedReader in = new BufferedReader(streamReader);
            String input_line;
            StringBuilder response_data = new StringBuilder();
            while ((input_line = in.readLine()) != null){
                response_data.append(input_line);
            }
            con.disconnect();
            emit(listener, 1, response_status, response_headers, response_data.toString());
        } catch (IOException e){
            emit(listener, 0, -1, new HashMap<>(), "");
        }
    }
}

public class HTTPRequest {
    public int connectionTimeout = 5000;
    public final SafeFlag oneshot;
    private final AsyncEngine mainLoop;

    public HTTPRequest(){
        this(AsyncEngine.MODE_HOT);
    }
    public HTTPRequest(int heatMode){
        this.mainLoop = new AsyncEngine(heatMode);
        this.oneshot = new SafeFlag(false);
    }
    public void terminate() { mainLoop.terminate(); }
    public void request(String url, Map<String, String> headers, boolean sslOnly, String method, String customData, HTTPResponseListener listener) {
        RequestPackage pck = new RequestPackage(connectionTimeout, url, headers, sslOnly, method, customData, listener);
        mainLoop.dispatch(() -> {
            pck.activate();
            if (oneshot.get()) terminate();
        });
    }
}

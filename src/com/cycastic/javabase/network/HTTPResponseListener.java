package com.cycastic.javabase.network;

import java.util.Map;

public abstract class HTTPResponseListener {
    public abstract void request_completed(int result, int response_code, Map<String, String> headers, String rawResponse);
}
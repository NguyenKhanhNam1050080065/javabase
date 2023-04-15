package com.cycastic.javabase.auth;

import java.util.Map;

public abstract class FirebaseAuthListener {
    public abstract void onConnectionFailed();
    public abstract void onRequestFailed(int response_code, Map<String, String> headers, String raw_response);
    public abstract void onAuthChanged(FirebaseAuthToken token);
}

package com.cycastic.javabase.auth;

import com.cycastic.javabase.network.HTTPRequest;
import com.cycastic.javabase.network.HTTPResponseListener;
import com.cycastic.javabase.misc.FirebaseConfig;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JSONfy {
    public static String stringify(Map<String, Object> params){
        StringBuilder builder = new StringBuilder();
        List<String> lines = new ArrayList<>();
        builder.append("{");
        params.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .forEach(entry -> {
                    String key = "\"" + entry.getKey() + "\"";
                    Object value = entry.getValue();
                    String value_str = "";
                    if (value.getClass() == String.class){
                        value_str = "\"" + value + "\"";
                    } else value_str = value.toString();
                    lines.add(key + ": " + value_str);
                });
        for (int i = 0, s = lines.size(); i < s; i++){
            builder.append(lines.get(i));
            if (i != s - 1) {
                builder.append(", ");
            }
        }
        builder.append("}");
        return builder.toString();
    }
}


public class FirebaseAuth {
    static final String API_VERSION = "v1";
    static final String BASE_URL = String.format("https://identitytoolkit.googleapis.com/%s/", API_VERSION);
    static final String SIGNUP_REQUEST_URL = "accounts:signUp?key=%s";
    static final String LOGIN_REQUEST_URL = "accounts:signInWithPassword?key=%s";

    private final HTTPRequest httpRequest;
    private final Map<String, String> headers;
    private final FirebaseAuthTokenWrapper authWrapper;

    private String apiKey = "";

    private class AuthResponseHandler extends HTTPResponseListener {
        private final FirebaseAuthListener activeListener;
        public AuthResponseHandler(FirebaseAuthListener listener){
            activeListener = listener;
        }
        private Map<String, String> cleanseKey(Map<String, Object> authResult){
            Map<String, String> cleansed = new HashMap<>();
            authResult.entrySet().stream()
                    .filter(entry -> entry.getKey() != null)
                    .forEach(entry -> {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        cleansed.put(key.replace("_", "").toLowerCase(), value);
                    });
            return cleansed;
        }
        @Override
        public void request_completed(int result, int response_code, Map<String, String> headers, String rawResponse) {
            if (result == 0){
                if (activeListener != null) activeListener.onConnectionFailed();
                return;
            }
            if (response_code >= 300 || response_code < 0 || rawResponse.isEmpty()){
                if (activeListener != null) activeListener.onRequestFailed(response_code, headers, rawResponse);
                return;
            }
            JSONObject body = new JSONObject(rawResponse);
            Map<String, Object> bodyAsMap = body.toMap();
            FirebaseAuthToken authToken = new FirebaseAuthToken(cleanseKey(bodyAsMap));
            authWrapper.setAuthToken(authToken);
            if (activeListener != null) activeListener.onAuthChanged(authToken);
        }
    }

    private String createLoginBody(String email,String password){
        Map<String, Object> login_request = new HashMap<>();
        login_request.put("email", email);
        login_request.put("password", password);
        login_request.put("returnSecureToken", true);
        return JSONfy.stringify(login_request);
    }
    public FirebaseAuth(){
        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        httpRequest = new HTTPRequest();
        authWrapper = new FirebaseAuthTokenWrapper();
    }
    public void terminate() { httpRequest.terminate(); }
    public FirebaseAuthTokenWrapper getAuthWrapper() { return authWrapper; }
    public void enrollConfig(FirebaseConfig config){
        if (config != null)
            apiKey = config.apiKey;
    }

    public void loginWithEmailAndPassword(String email, String password, FirebaseAuthListener listener){
        if (apiKey.isEmpty()) throw new FirebaseAuthException("No API key found");
        String body = createLoginBody(email, password);
        httpRequest.request(BASE_URL + LOGIN_REQUEST_URL.formatted(apiKey), headers,
                true, "POST", body, new AuthResponseHandler(listener));
    }
    public void signupWithEmailAndPassword(String email, String password, FirebaseAuthListener listener){
        if (apiKey.isEmpty()) throw new FirebaseAuthException("No API key found");
        String body = createLoginBody(email, password);
        httpRequest.request(BASE_URL + SIGNUP_REQUEST_URL.formatted(apiKey), headers,
                true, "POST", body, new AuthResponseHandler(listener));
    }
    public void loginWithEmailAndPassword(String email, String password) { loginWithEmailAndPassword(email, password, null); }
    public void signupWithEmailAndPassword(String email, String password) { signupWithEmailAndPassword(email, password, null); }
    public void logout() { authWrapper.setAuthToken(null); }
}

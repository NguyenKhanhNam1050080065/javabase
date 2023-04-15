package com.cycastic.javabase.firestore;

import com.cycastic.javabase.auth.FirebaseAuthTokenWrapper;
import com.cycastic.javabase.dispatcher.AsyncEngine;
import com.cycastic.javabase.misc.FirebaseConfig;
import com.cycastic.javabase.network.HTTPRequest;
import com.cycastic.javabase.network.HTTPResponseListener;

import java.util.HashMap;
import java.util.Map;

public class Firestore {
    static final String API_VERSION = "v1";
    static final String BASE_URL = String.format("https://firestore.googleapis.com/%s/projects/", API_VERSION);
    static final String BASE_SUB_URL = "/databases/%s/documents/";

    private FirebaseAuthTokenWrapper authWrapper;
    private String projectId;
    private final HTTPRequest httpRequest;
    private final boolean dispatched;

    public Firestore(boolean dispatched){
        this.dispatched = dispatched;
        httpRequest = new HTTPRequest(dispatched ? AsyncEngine.MODE_COLD : AsyncEngine.MODE_HOT);
    }
    public Firestore(){
        this(false);
    }
    public void enrollToken(FirebaseAuthTokenWrapper authToken){
        this.authWrapper = authToken;
    }
    public void enrollConfig(FirebaseConfig config){
        if (config != null)
            projectId = config.projectId;
    }
    public void terminate() { httpRequest.terminate(); }
    private void queryInternal(FirestoreQuery request, FirestoreTaskReceiver receiver){
        if (projectId.isEmpty()) throw new FirestoreException("projectId not found");
        Map<String, String> headers = new HashMap<>();
        if (authWrapper != null && authWrapper.getAuthToken() != null) {
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");
            headers.put("Authorization", "Bearer %s".formatted(authWrapper.getAuthToken().getIdToken()));
        }
        String url = "";
        url = BASE_URL + projectId + BASE_SUB_URL.formatted(request.getDatabaseName());
        url += request.getSubUrl();
        httpRequest.request(url, headers, true, request.getHttpMethod(), request.toHttpBody(), new HTTPResponseListener() {
            @Override
            public void request_completed(int result, int responseCode, Map<String, String> headers, String rawResponse) {
                if (receiver == null) return;
                if (result == 0) {
                    receiver.connectionFailed();
                } else if (responseCode >= 300) {
                    receiver.requestFailed(responseCode, headers, rawResponse);
                } else {
                    receiver.queryCompleted(new FirestoreTaskResult(responseCode, headers, rawResponse, request));
                }
            }
        });
    }
    public void query(FirestoreQuery request){
        query(request, null);
    }
    public void query(FirestoreQuery request, FirestoreTaskReceiver receiver){
        if (dispatched) new Thread(() -> queryInternal(request, receiver)).start();
        else queryInternal(request, receiver);
    }
}

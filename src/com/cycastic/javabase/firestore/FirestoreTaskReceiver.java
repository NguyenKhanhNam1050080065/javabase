package com.cycastic.javabase.firestore;

import java.util.Map;

public abstract class FirestoreTaskReceiver {
    public abstract void connectionFailed();
    public abstract void requestFailed(int response_code, Map<String, String> headers, String rawResponse);
    public abstract void queryCompleted(FirestoreTaskResult queryResult);
}

package com.cycastic.javabase.firestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.LinkedHashMap;

public class FirestoreTaskResult {
    private final int responseCode;
    private final Map<String, String> headers;
    private final String content;
    private final FirestoreQuery baseQuery;
    private final Map<String, FirestoreDocument> documents;

    public int getResponseCode() { return responseCode; }
    public Map<String, String> getResponseHeaders() { return headers; }
    public String getResponseContent() { return content; }
    public FirestoreQuery getBaseQuery() { return baseQuery; }
    public Map<String, FirestoreDocument> getDocuments() { return documents; }

    public FirestoreTaskResult(int responseCode, Map<String, String> headers, String content, FirestoreQuery baseQuery){
        this.responseCode = responseCode;
        this.headers = headers;
        this.content = content;
        this.baseQuery = baseQuery;
        this.documents = new LinkedHashMap<>();

        if (responseCode == -1) return;

        if (responseCode >= 300) return;
        JSONArray task_result;
        try {
            task_result = new JSONArray(content);
            if (task_result.isEmpty()) return;
        } catch (JSONException ignored){
            try {
                JSONObject stuff = new JSONObject(content);
                if (stuff.isEmpty()) return;
                FirestoreDocument doc = new FirestoreDocument().parseDocument(stuff.toMap());
                if (!doc.getDocumentName().isEmpty())
                    documents.put(doc.getDocumentName(), doc);
            } catch (JSONException ignored1) {
                throw new FirestoreException("Failed to convert rawResponse into meaningful JSON");
            }
            return;
        }

        for (int i = 0, s = task_result.length(); i < s; i++){
            Object stuff = task_result.get(i);
            if (stuff == null) continue;
            FirestoreDocument doc = new FirestoreDocument().parse((JSONObject) stuff);
            if (doc.getDocumentName().isEmpty()) continue;
            documents.put(doc.getDocumentName(), doc);
        }
    }
}

package com.cycastic.javabase.auth;

import java.util.Map;

public class FirebaseAuthToken {
    private final Map<String, String> key;
    public FirebaseAuthToken(Map<String, String> cleanKey){
        key = cleanKey;
    }
    public String getIdToken(){
        return key.get("idtoken");
    }
    public String getLocalId(){
        return key.get("localid");
    }
}

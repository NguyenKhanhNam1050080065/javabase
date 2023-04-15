package com.cycastic.javabase.misc;

import org.json.JSONObject;

import java.util.Map;

public class FirebaseConfig {
    public String apiKey = "";
    public String authDomain = "";
    public String databaseUrl = "";
    public String projectId = "";
    public String storageBucket = "";
    public String messageSenderId = "";
    public String appId = "";
    public String measurementId = "";

    private int counter = 0;

    public FirebaseConfig(){}
    public FirebaseConfig(String cfg) { deserialize(cfg); }

    private String read_field(JSONObject json, String field){
        if (json.isNull(field)) return "";
        counter += 1;
        return json.getString(field);
    }
    private String read_field(Map<String, String> map, String field){
        if (!map.containsKey(field)) return "";
        counter += 1;
        return map.get(field);
    }
    public int fields_loaded() { return counter; }
    public void deserialize(String json){
        counter = 0;
        JSONObject body = new JSONObject(json);
        if (body.isEmpty()) return;
        apiKey = read_field(body, "apiKey");
        authDomain = read_field(body, "authDomain");
        databaseUrl = read_field(body, "databaseURL");
        projectId = read_field(body, "projectId");
        storageBucket = read_field(body, "storageBucket");
        messageSenderId = read_field(body, "messagingSenderId");
        appId = read_field(body, "appId");
        measurementId = read_field(body, "measurementId");
    }
    public void deserialize(Map<String, String> body){
        counter = 0;
        apiKey = read_field(body, "apiKey");
        authDomain = read_field(body, "authDomain");
        databaseUrl = read_field(body, "databaseURL");
        projectId = read_field(body, "projectId");
        storageBucket = read_field(body, "storageBucket");
        messageSenderId = read_field(body, "messagingSenderId");
        appId = read_field(body, "appId");
        measurementId = read_field(body, "measurementId");
    }
}

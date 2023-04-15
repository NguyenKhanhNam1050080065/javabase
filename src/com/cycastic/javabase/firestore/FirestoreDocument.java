package com.cycastic.javabase.firestore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class FirestoreDocument {
    String documentName = "";
    String documentNameShort = "";
    String readTime = "";
    String createTime = "";
    String updateTime = "";
    Map<String, Object> fields = new HashMap<>();

    private static String getDocName(String base_name){
        for (int i = base_name.length() - 1; i >= 0; i--){
            if (base_name.charAt(i) == '/'){
                return base_name.substring(i + 1);
            }
        }
        return "";
    }

    public String getDocumentPath() {
        return documentName;
    }
    public String getDocumentName() {
        return documentNameShort;
    }
    public String getReadTime() {
        return readTime;
    }
    public String getCreateTime() {
        return createTime;
    }
    public String getUpdateTime() {
        return updateTime;
    }
    public Map<String, Object> getFields(){
        return fields;
    }

    public FirestoreDocument(){}
    Object parseSingleField(Map<String, Object> raw_data){
        Object re = null;
        if (raw_data.containsKey("stringValue")){
            re = raw_data.get("stringValue").toString();
        } else if (raw_data.containsKey("integerValue")) {
            re = Long.valueOf(raw_data.get("integerValue").toString());
        } else if (raw_data.containsKey("doubleValue")){
            re = Double.valueOf(raw_data.get("doubleValue").toString());
        } else if (raw_data.containsKey("booleanValue")){
            re = Boolean.valueOf(raw_data.get("booleanValue").toString());
        } else if (raw_data.containsKey("arrayValue")){
            Map<String, Object> arr_check = (Map<String, Object>)raw_data.get("arrayValue");
            if (arr_check.isEmpty()) re = new Vector<>();
            else {
                List<Object> arr = (List<Object>)(arr_check).get("values");
                re = parseArrayValue(arr);
            }
        } else if (raw_data.containsKey("mapValue")) {
            Map<String, Object> map_check = (Map<String, Object>)raw_data.get("mapValue");
            if (map_check.isEmpty()) re = new HashMap<String, Object>();
            else {
                Map<String, Object> arr = (Map<String, Object>)(map_check).get("fields");
                re = parseMapValue(arr);
            }
        }
        return re;
    }
    List<Object> parseArrayValue(List<Object> array){
        List<Object> re = new Vector<>();
        for (Object o : array) {
            Object cleansed = parseSingleField((Map<String, Object>) o);
            re.add(cleansed);
        }
        return re;
    }
    Map<String, Object> parseMapValue(Map<String, Object> raw_data){
        Map<String, Object> re = new HashMap<>();
        for (Map.Entry<String, Object> E : raw_data.entrySet()){
            Map<String, Object> field = ((Map<String, Object>)E.getValue());
            String field_name = E.getKey();
            Object value = parseSingleField(field);
            re.put(field_name, value);
        }
        return re;
    }
    public FirestoreDocument parse(JSONObject origin){
        Map<String, Object> base_map = origin.toMap();
//        if (base_map.containsKey("readTime"))
//            read_time = base_map.get("readTime").toString();
        if (!base_map.containsKey("document")) return this;
        return parseDocument((Map<String, Object>)base_map.get("document"));
    }
    public FirestoreDocument parseDocument(Map<String, Object> doc){
        documentName = doc.get("name").toString();
        documentNameShort = getDocName(documentName);
        createTime = doc.get("createTime").toString();
        updateTime = doc.get("updateTime").toString();
        if (!doc.containsKey("fields")) return this;
        fields = parseMapValue((Map<String, Object>)doc.get("fields"));
        return this;
    }
}

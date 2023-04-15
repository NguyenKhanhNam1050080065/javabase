package com.cycastic.javabase.firestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreQuery {
    private final QueryType type;
    private final String httpMethod;
    private boolean updated = false;
    private String lastSerialization = "";
    private String collectionName = "";
    private String documentId = "";
    private String databaseName = "(default)";
    public enum Direction {
        DIRECTION_UNSPECIFIED,
        ASCENDING,
        DESCENDING;

        public static String toString(Direction dir){
            switch (dir){
                case DIRECTION_UNSPECIFIED -> {
                    return "DIRECTION_UNSPECIFIED";
                }
                case ASCENDING -> {
                    return "ASCENDING";
                }
                case DESCENDING -> {
                    return "DESCENDING";
                }
            }
            return "";
        }
    }
    public enum Operator {
        OPERATOR_UNSPECIFIED,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        EQUAL,
        NOT_EQUAL,
        ARRAY_CONTAINS,
        ARRAY_CONTAINS_ANY,
        IN,
        NOT_IN,
        IS_NAN,
        IS_NULL,
        IS_NOT_NAN,
        IS_NOT_NULL,
        AND,
        OR;
        public static String toString(Operator op){
            switch (op){
                case OPERATOR_UNSPECIFIED -> { return "OPERATOR_UNSPECIFIED"; }
                case LESS_THAN -> { return "LESS_THAN"; }
                case LESS_THAN_OR_EQUAL -> { return "LESS_THAN_OR_EQUAL"; }
                case GREATER_THAN -> { return "GREATER_THAN"; }
                case GREATER_THAN_OR_EQUAL -> { return "GREATER_THAN_OR_EQUAL"; }
                case EQUAL -> { return "EQUAL"; }
                case NOT_EQUAL -> { return "NOT_EQUAL"; }
                case ARRAY_CONTAINS -> { return "ARRAY_CONTAINS"; }
                case ARRAY_CONTAINS_ANY -> { return "ARRAY_CONTAINS_ANY"; }
                case IN -> { return "IN"; }
                case NOT_IN -> { return "NOT_IN"; }
                case IS_NAN -> { return "IS_NAN"; }
                case IS_NULL -> { return "IS_NULL"; }
                case IS_NOT_NAN -> { return "IS_NOT_NAN"; }
                case IS_NOT_NULL -> { return "IS_NOT_NULL"; }
                case AND -> { return "AND"; }
                case OR -> { return "OR"; }
                default ->  { return ""; }
            }
        }
        public static Operator fromString(String op){
            switch (op){
                case "OPERATOR_UNSPECIFIED" -> { return OPERATOR_UNSPECIFIED; }
                case "LESS_THAN" -> { return LESS_THAN; }
                case "LESS_THAN_OR_EQUAL" -> { return LESS_THAN_OR_EQUAL; }
                case "GREATER_THAN" -> { return GREATER_THAN; }
                case "GREATER_THAN_OR_EQUAL" -> { return GREATER_THAN_OR_EQUAL; }
//                case "EQUAL" -> { return EQUAL; }
                case "NOT_EQUAL" -> { return NOT_EQUAL; }
                case "ARRAY_CONTAINS" -> { return ARRAY_CONTAINS; }
                case "ARRAY_CONTAINS_ANY" -> { return ARRAY_CONTAINS_ANY; }
                case "IN" -> { return IN; }
                case "NOT_IN" -> { return NOT_IN; }
                case "IS_NAN" -> { return IS_NAN; }
                case "IS_NULL" -> { return IS_NULL; }
                case "IS_NOT_NAN" -> { return IS_NOT_NAN; }
                case "IS_NOT_NULL" -> { return IS_NOT_NULL; }
                case "AND" -> { return AND; }
                case "OR" -> { return OR; }
                default ->  { return EQUAL; }
            }
        }
    }
    public enum QueryType {
        STRUCTURED_QUERY,
        CREATE_DOCUMENT,
        PATCH_DOCUMENT,
        DELETE_DOCUMENT,
    }
    public QueryType getQueryType(){
        return type;
    }
    public String getCollectionName(){
        return collectionName;
    }
    public String getDocumentId(){
        return documentId;
    }
    public String getDatabaseName() { return databaseName; }
    public FirestoreQuery(QueryType t){
        type = t;
        if (type == QueryType.PATCH_DOCUMENT) httpMethod = "PATCH";
        else if (type == QueryType.DELETE_DOCUMENT) httpMethod = "DELETE";
        else httpMethod = "POST";
    }
    public FirestoreQuery(){
        this(QueryType.STRUCTURED_QUERY);
    }
    static Map<String, Object> orderObject(String field, Direction dir){
        Map<String, Object> _f = new HashMap<>();
        Map<String, Object> _ff = new HashMap<>();
        _ff.put("fieldPath", field);
        _f.put("field", _ff);
        _f.put("direction", Direction.toString(dir));
        return _f;
    }
    public static String getTypeIndicator(Object value){
        String typeIndicator = "";
        if (value.getClass() == String.class) {
            typeIndicator = "stringValue";
        } else if (value.getClass() == Integer.class || value.getClass() == Long.class) {
            typeIndicator = "integerValue";
        } else if (value.getClass() == Double.class || value.getClass() == Float.class) {
            typeIndicator = "doubleValue";
        } else if (value.getClass() == Boolean.class){
            typeIndicator = "booleanValue";
        } else {
            // Special orders
            try {
                List<Object> l = (List<Object>)value;
                typeIndicator = "arrayValue";
            } catch (ClassCastException ignored) {}
            if (!typeIndicator.isEmpty()) return typeIndicator;
            try {
                Map<String, Object> m = (Map<String, Object>)value;
                typeIndicator = "mapValue";
            } catch (ClassCastException ignored) {}
            if (!typeIndicator.isEmpty()) return typeIndicator;
        }
        if (typeIndicator.isEmpty()) throw new FirestoreException("Unsupported type");
        return typeIndicator;
    }

    private Integer __limit = 0;
    private Boolean __doc_existed = true;
    private Operator __composite_operation = Operator.AND;
    private final List<Map<String, Object>> __order_by = new ArrayList<>();
    private final List<Map<String, String>> __from = new ArrayList<>();
    private final List<Map<String, String>> __select = new ArrayList<>();
    private final List<Map<String, Object>> __composite_filters = new ArrayList<>();
    private final List<String> __update_mask = new ArrayList<>();
    private final List<String> __mask = new ArrayList<>();
    private Map<String, Object> __create = new HashMap<>();

    private String buildStructuredQuery(){
        if (updated) return lastSerialization;
        Map<String, Object> _fake_json = new HashMap<>();
        JSONObject _json = new JSONObject("{}");

        if (__limit > 0) _fake_json.put("limit", __limit);
        if (!__order_by.isEmpty()) _fake_json.put("orderBy", __order_by);
        if (!__from.isEmpty()) _fake_json.put("from", __from);
        if (!__composite_filters.isEmpty()){
            Map<String, Object> real_cf = new HashMap<>();
            Map<String, Object> real_filter = new HashMap<>();
            real_cf.put("op", Operator.toString(__composite_operation));
            real_cf.put("filters", __composite_filters);
            real_filter.put("compositeFilter", real_cf);
            _fake_json.put("where", real_filter);
        }
        if (!__select.isEmpty()) {
            Map<String, List<Map<String, String>>> real_select = new HashMap<>();
            real_select.put("fields", __select);
            _fake_json.put("select", real_select);
        }
        _json.put("structuredQuery", _fake_json);
        lastSerialization = _json.toString(4);
        updated = true;
        return lastSerialization;
    }

    public FirestoreQuery select(String field_path){
        if (type != QueryType.STRUCTURED_QUERY) return this;
        Map<String, String> _field_path = new HashMap<>();
        updated = false;
        _field_path.put("fieldPath", field_path);
        __select.add(_field_path);
        return this;
    }
    public FirestoreQuery from(String collection){
        if (type != QueryType.STRUCTURED_QUERY) return this;
        Map<String, String> _from = new HashMap<>();
        updated = false;
        _from.put("collectionId", collection);
        __from.add(_from);
        return this;
    }
    public FirestoreQuery orderBy(String field, Direction dir){
        if (type != QueryType.STRUCTURED_QUERY) return this;
        updated = false;
        __order_by.add(orderObject(field, dir));
        return this;
    }
    public FirestoreQuery limit(int by){
        if (type != QueryType.STRUCTURED_QUERY || by < 1) return this;
        updated = false;
        __limit = by;
        return this;
    }
    public FirestoreQuery relate(Operator op){
        updated = false;
        __composite_operation = op;
        return this;
    }
    public FirestoreQuery where(String field, Operator op, Object value){
        if (type != QueryType.STRUCTURED_QUERY) return this;
        updated = false;
        String type_indicator = getTypeIndicator(value);

        Map<String, Object> _field_filter = new HashMap<>();
        Map<String, Object> _real_field_filter = new HashMap<>();
        Map<String, Object> _field_path = new HashMap<>();
        Map<String, Object> _value = new HashMap<>();
        _value.put(type_indicator, value);
        _field_path.put("fieldPath", field);
        _real_field_filter.put("field", _field_path);
        _real_field_filter.put("op", Operator.toString(op));
        _real_field_filter.put("value", _value);
        _field_filter.put("fieldFilter", _real_field_filter);

        __composite_filters.add(_field_filter);
        return this;
    }
    private Map<String, Object> serializeValue(Object target){
        final String type = getTypeIndicator(target);
        final Map<String, Object> _value = new HashMap<>();
        if (type.equals("arrayValue")){
            Map<String, Object> _another_value = new HashMap<>();
            List<Object> serializedArray = new ArrayList<>();
            for (Object childObject : (List<Object>)target){
                serializedArray.add(serializeValue(childObject));
            }
            _another_value.put("values", serializedArray);
            _value.put("arrayValue", _another_value);
        } else if (type.equals("mapValue")){
            Map<String, Object> _another_value = new HashMap<>();
            _another_value.put("fields", target);
            _value.put("mapValue", _another_value);
        } else {
            _value.put(type, target);
        }
        return  _value;
    }
    public FirestoreQuery create(Map<String, Object> document){
        if (type != QueryType.CREATE_DOCUMENT && type != QueryType.PATCH_DOCUMENT) return this;
        updated = false;
        Map<String, Object> _create = new HashMap<>();
        for (Map.Entry<String, Object> E : document.entrySet()){
            // BREAKING CHANGES
            Map<String, Object> _value = serializeValue(E.getValue());
            _create.put(E.getKey(), _value);
        }
        __create = _create;
        return this;
    }
    public FirestoreQuery update(Map<String, Object> document) { return create(document); }
    public FirestoreQuery mask(String field_mask) {
        if (type != QueryType.PATCH_DOCUMENT) return this;
        updated = false;
        __mask.add(field_mask);
        return this;
    }
    public FirestoreQuery updateMask(String field_mask) {
        if (type != QueryType.PATCH_DOCUMENT) return this;
        updated = false;
        __update_mask.add(field_mask);
        return this;
    }
    public FirestoreQuery documentExisted(boolean existence) {
        if (type != QueryType.PATCH_DOCUMENT) return this;
        updated = false;
        __doc_existed = existence;
        return this;
    }
    public FirestoreQuery onCollection(String c_name){
        updated = false;
        collectionName = c_name;
        return this;
    }
    public FirestoreQuery onDocument(String d_name){
        updated = false;
        documentId = d_name;
        return this;
    }
    public FirestoreQuery onDatabase(String db_name){
        databaseName = db_name;
        return this;
    }
    public String getLastSerialization() { return lastSerialization; }
    public String getHttpMethod() { return httpMethod; }
    public String getSubUrl() {
        switch (type){
            case STRUCTURED_QUERY -> { return ":runQuery"; }
            case CREATE_DOCUMENT -> {
                String re = getCollectionName();
                if (!getDocumentId().isEmpty()){
                    re += "?documentId=" + getDocumentId();
                }
                return re;
            }
            case PATCH_DOCUMENT -> {
                StringBuilder re = new StringBuilder(getCollectionName());
                re.append("/").append(getDocumentId()).append("?");
                re.append("currentDocument.exists=").append(__doc_existed.toString());

                for (String mask : __mask){
                    re.append("&mask.fieldPaths=").append(mask);
                }
                for (String update_mask : __update_mask){
                    re.append("&updateMask.fieldPaths=").append(update_mask);
                }

                return re.toString();
            }
            case DELETE_DOCUMENT -> {
                return getCollectionName() + "/" + getDocumentId() + "/?currentDocument.exists=true";
            }
            default -> { return ""; }
        }
    }
    public String toHttpBody(){
        switch (type){
            case STRUCTURED_QUERY -> {
                return buildStructuredQuery();
            }
            case CREATE_DOCUMENT, PATCH_DOCUMENT -> {
                lastSerialization = new JSONObject("{}").put("fields", __create).toString(4);
                updated = true;
                return lastSerialization;
            }
            default -> { return "{}"; }
        }
    }
}

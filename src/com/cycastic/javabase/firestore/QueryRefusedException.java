package com.cycastic.javabase.firestore;

public class QueryRefusedException extends FirestoreModelException {
    public QueryRefusedException(){
        super();
    }
    public QueryRefusedException(String msg){
        super(msg);
    }
    public QueryRefusedException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}
package com.cycastic.javabase.firestore;

public class NoConnectionException extends FirestoreModelException {
    public NoConnectionException(){
        super();
    }
    public NoConnectionException(String msg){
        super(msg);
    }
    public NoConnectionException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}
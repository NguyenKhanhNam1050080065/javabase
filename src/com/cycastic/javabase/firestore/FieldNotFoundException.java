package com.cycastic.javabase.firestore;

public class FieldNotFoundException extends FirestoreModelException {
    public FieldNotFoundException(){
        super();
    }
    public FieldNotFoundException(String msg){
        super(msg);
    }
    public FieldNotFoundException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}

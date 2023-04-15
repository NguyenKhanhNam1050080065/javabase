package com.cycastic.javabase.firestore;

public class FirestoreModelException extends FirestoreException {
    public FirestoreModelException(){
        super();
    }
    public FirestoreModelException(String msg){
        super(msg);
    }
    public FirestoreModelException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}
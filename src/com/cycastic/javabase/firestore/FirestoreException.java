package com.cycastic.javabase.firestore;

public class FirestoreException extends RuntimeException {
    public FirestoreException(){
        super();
    }
    public FirestoreException(String msg){
        super(msg);
    }
    public FirestoreException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}

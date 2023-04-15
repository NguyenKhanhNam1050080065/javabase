package com.cycastic.javabase.firestore;

public class FaultyConvertionException extends FirestoreModelException {
    public FaultyConvertionException(){
        super();
    }
    public FaultyConvertionException(String msg){
        super(msg);
    }
    public FaultyConvertionException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}
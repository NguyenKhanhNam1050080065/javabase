package com.cycastic.javabase.firestore;

public class IncorrectTypeException extends FirestoreModelException {
    public IncorrectTypeException(){
        super();
    }
    public IncorrectTypeException(String msg){
        super(msg);
    }
    public IncorrectTypeException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}

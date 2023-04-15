package com.cycastic.javabase.auth;

public class FirebaseAuthException extends RuntimeException {
    public FirebaseAuthException(){
        super();
    }
    public FirebaseAuthException(String msg){
        super(msg);
    }
    public FirebaseAuthException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}

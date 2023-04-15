package com.cycastic.javabase.network;

public class HTTPRequestException extends RuntimeException {
    public HTTPRequestException(){
        super();
    }
    public HTTPRequestException(String msg){
        super(msg);
    }
    public HTTPRequestException(String msg, Throwable thrown){
        super(msg, thrown);
    }
}

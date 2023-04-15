package com.cycastic.javabase.dispatcher;

public interface Command {
    void exec(Object ...params);
}

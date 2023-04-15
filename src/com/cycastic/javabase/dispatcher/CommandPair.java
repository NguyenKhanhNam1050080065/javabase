package com.cycastic.javabase.dispatcher;

public class CommandPair {
    public Command cmd;
    public Object[] params;
    public final SafeFlag isFinished;
    public CommandPair(Command cmd, Object[] params){
        this.cmd = cmd;
        this.params = params;
        this.isFinished = new SafeFlag(false);
    }
}

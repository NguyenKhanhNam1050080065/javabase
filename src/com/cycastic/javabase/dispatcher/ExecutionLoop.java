package com.cycastic.javabase.dispatcher;

class Terminator extends Thread {
    private final ExecutionLoop loop;
    public Terminator(ExecutionLoop loop) { this.loop = loop; start(); }

    @Override
    public void run(){
        loop.terminate();
    }
}

@Deprecated
public class ExecutionLoop extends Thread {
    private final CommandQueueCompat queue;
    private long serverId;
    private final SafeFlag exit;

    public ExecutionLoop(){
        queue = new CommandQueueCompat();
        exit = new SafeFlag(false);
        start();
    }
    @Override
    public void run(){
        serverId = Thread.currentThread().getId();
        while (!exit.get()){
            queue.waitAndRunOne();
        }
        queue.runAll();
    }
    public long getServerId() { return serverId; }
    public void terminate(){
        if (Thread.currentThread().getId() == serverId){
            new Terminator(this);
            return;
        }
        exit.set();
        while (isAlive()) { continue; }
    }
    public void push(Command cmd, Object... params){
        queue.push(cmd, params);
    }
    public void pushAndWait(Command cmd, Object... params){
        queue.pushAndWait(cmd, params);
    }
}

package com.cycastic.javabase.dispatcher;

public class Dispatchable {
    private final Runnable process;
    private final SafeFlag finished = new SafeFlag(false);
    public Dispatchable(Runnable process){
        this.process = process;
    }
    public Runnable getProcess() {
        return process;
    }
    public void setFinished() { finished.set(); }
    public boolean isFinished() { return finished.get(); }
    public void waitToFinish() { finished.waitToFinish(); }
    @Deprecated(since = "1.0")
    public void start() {}
}

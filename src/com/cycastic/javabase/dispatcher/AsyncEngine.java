package com.cycastic.javabase.dispatcher;

import java.util.concurrent.atomic.AtomicLong;

public class AsyncEngine extends Thread {
    public static final int MODE_HOT = 0;
    public static final int MODE_COLD = 1;
    public static final int MODE_ON_SPOT = 2;
    private final CommandQueue queue;
    private final SafeFlag exit = new SafeFlag(false);
    private final AtomicLong serverId = new AtomicLong(0L);
    private final boolean faultTolerant;
    private final int heatMode;

    public AsyncEngine(int heatMode, boolean daemonMode, boolean faultTolerant){
        queue = new CommandQueue();
        this.faultTolerant = faultTolerant;
        if (heatMode < MODE_HOT || heatMode > MODE_ON_SPOT) this.heatMode = MODE_HOT;
        else this.heatMode = heatMode;
        if (heatMode == MODE_HOT){
            setDaemon(daemonMode);
            start();
        }
    }
    public AsyncEngine() { this(MODE_HOT, false, true); }
    public AsyncEngine(int heatMode) { this(heatMode, false, true); }
    public AsyncEngine(boolean daemonMode) { this(MODE_HOT, daemonMode, true); }
    public AsyncEngine(int heatMode, boolean daemonMode) { this(heatMode, daemonMode, true); }
    @Override
    public void run(){
        serverId.set(Thread.currentThread().threadId());
        while (!exit.get()){
            if (!faultTolerant)
                queue.executeOne();
            else try {
                queue.executeOne();
            } catch (Exception ex){
                System.err.print("Exception caught during asynchronous execution: ");
                ex.printStackTrace(System.err);
                System.err.println("This exception will be ignored due to fault tolerant policy");
            }
        }
        queue.executeAll();
    }
    public long getServerId() { return serverId.get(); }
    public int queueSize() { return queue.queueSize(); }
    public void terminate(){
        if (heatMode != MODE_HOT || exit.get()) return;
        if (Thread.currentThread().threadId() == getServerId()){
            new Thread(this::terminate).start();
            return;
        }
        exit.set();
        while (isAlive()) { continue; }
    }
    public Dispatchable dispatch(final Runnable process){
        if (exit.get()) return null;
        switch (heatMode){
            case MODE_HOT -> {
                return queue.dispatch(process);
            }
            case MODE_COLD -> new Thread(process).start();
            case MODE_ON_SPOT -> process.run();
        }
        return new Dispatchable(process);
    }
    public void sync(final Runnable process){
        if (exit.get()) return;
        switch (heatMode){
            case MODE_HOT -> queue.sync(process);
            case MODE_COLD -> {
                final SafeFlag finished = new SafeFlag(false);
                new Thread(() -> {
                    process.run();
                    finished.set();
                }).start();
                finished.waitToFinish();
            }
            case MODE_ON_SPOT -> process.run();
        }
    }
    public void sync(){
        sync(() -> {});
    }
}

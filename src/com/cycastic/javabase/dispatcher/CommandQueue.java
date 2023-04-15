package com.cycastic.javabase.dispatcher;

import com.cycastic.javabase.collection.FIFOQueue;

public class CommandQueue {
    private final FIFOQueue<Dispatchable> processList;
    public CommandQueue(){
        processList = new FIFOQueue<>();
    }
    public int queueSize() { return processList.size(); }
    public boolean executeOne(){
        synchronized (this){
            if (processList.isEmpty()) return false;
            Dispatchable actualProcess = processList.dequeue();
            try {
                actualProcess.getProcess().run();
            } catch (Exception ex){
                actualProcess.setFinished();
                throw ex;
            }
            actualProcess.setFinished();
            return true;
        }
    }
    public void executeAll(){
        while (executeOne()) { continue; }
    }
    public Dispatchable dispatch(Runnable process){
        synchronized (this){
            Dispatchable re = new Dispatchable(process);
            processList.enqueue(re);
            return re;
        }
    }
    public void sync(Runnable process){
        dispatch(process).waitToFinish();
    }
}

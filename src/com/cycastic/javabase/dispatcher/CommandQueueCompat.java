package com.cycastic.javabase.dispatcher;

import com.cycastic.javabase.collection.ReferencesList;

public class CommandQueueCompat {
    private final ReferencesList<CommandPair> queue;

    public CommandQueueCompat() {
        queue = new ReferencesList<>();
    }
    public int queueSize() {
        synchronized (queue) { return queue.size(); }
    }
    public boolean waitAndRunOne(){
        synchronized (queue){
            ReferencesList.Element<CommandPair> pair = queue.first();
            if (pair == null) {
                return false;
            }
            CommandPair actual_pair = pair.getValue();
            actual_pair.cmd.exec(actual_pair.params);
            actual_pair.isFinished.set();
            queue.erase(pair);
            return true;
        }
    }
    public void runAll(){
        while (waitAndRunOne()) { continue; }
    }
    public CommandPair push(Command cmd, Object... params){
        synchronized (queue){
            CommandPair pair = new CommandPair(cmd, params);
            queue.add(pair);
            return pair;
        }
    }
    public void pushAndWait(Command cmd, Object... params){
        CommandPair pair = push(cmd, params);
        while (!pair.isFinished.get()) { continue; }
    }
}

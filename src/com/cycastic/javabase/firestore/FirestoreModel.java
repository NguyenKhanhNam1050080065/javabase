package com.cycastic.javabase.firestore;

import com.cycastic.javabase.dispatcher.SafeFlag;

public class FirestoreModel {
    protected final Firestore host;
    protected final boolean lazyEvaluation;
    protected String documentName = "";
    protected FirestoreModelException exception;
    protected final SafeFlag ranOnce = new SafeFlag(false);
    private final SafeFlag evaluationFlag = new SafeFlag(true);
    public FirestoreModel(Firestore host, boolean lazyEvaluation){
        this.host = host;
        this.lazyEvaluation = lazyEvaluation;
        if (!lazyEvaluation) evaluate();
    }
    public FirestoreModel(Firestore host){
        this(host, true);
    }
    public String getDocumentName() {
        return documentName;
    }
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    public void evaluate(){
        synchronized (ranOnce) {
            ranOnce.set();
        }
        synchronized (ranOnce){
            if (isEvaluating()) return;
            evaluationFlag.clear();
            evaluatePrivate();
            evaluationFlag.waitToFinish();
            if (exception != null) throw exception;
        }
    }
    public boolean isEvaluating() { return !evaluationFlag.get(); }
    protected void setEvaluationFlag(){
        evaluationFlag.set();
    }
    protected void evaluatePrivate(){
        exception = new FirestoreModelException("Not implemented");
        setEvaluationFlag();
    }
}

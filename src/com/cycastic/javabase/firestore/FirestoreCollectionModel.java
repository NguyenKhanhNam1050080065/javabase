package com.cycastic.javabase.firestore;

import java.util.Map;
import java.util.LinkedHashMap;

public class FirestoreCollectionModel<T extends FirestoreModel> extends FirestoreModel{
    protected FirestoreQuery baseQuery;
    protected final Map<String, T> cachedValue = new LinkedHashMap<>();

    public FirestoreCollectionModel(Firestore host, boolean lazyEvaluation, FirestoreQuery baseQuery) {
        super(host, lazyEvaluation);
        this.baseQuery = baseQuery;
    }
    public FirestoreCollectionModel(Firestore host, boolean lazyEvaluation) {
        super(host, lazyEvaluation);
        this.baseQuery = null;
    }
    public FirestoreCollectionModel(Firestore host) {
        super(host);
        this.baseQuery = null;
    }
    public FirestoreCollectionModel<T> setBaseQuery(FirestoreQuery baseQuery) {
        this.baseQuery = baseQuery;
        return this;
    }
    public FirestoreQuery getBaseQuery() { return baseQuery; }
    public Map<String, T> getDocuments(){
        synchronized (ranOnce){
            if (!ranOnce.get() && lazyEvaluation) evaluate();
        }
        while (isEvaluating()) { continue; }
        return cachedValue;
    }
    private void evaluationFinalize(){
        cachedValue.clear();
        setEvaluationFlag();
    }
    @Override
    protected void evaluatePrivate() {
        if (baseQuery == null){
            exception = new FirestoreModelException("No Query");
            evaluationFinalize();
            return;
        }
        if (baseQuery.getQueryType() == FirestoreQuery.QueryType.STRUCTURED_QUERY)
            host.query(baseQuery, new FirestoreTaskReceiver() {
                @Override
                public void connectionFailed() {
                    exception = new NoConnectionException();
                    evaluationFinalize();
                }
                @Override
                public void requestFailed(int response_code, Map<String, String> headers, String rawResponse) {
                    exception = new QueryRefusedException("Response code: %d. Raw response: %s".formatted(response_code, rawResponse));
                    evaluationFinalize();
                }
                @Override
                public void queryCompleted(FirestoreTaskResult queryResult) {
                    taskResultCleanse(queryResult);
                    setEvaluationFlag();
                }
            });
        else if (baseQuery.getQueryType() == FirestoreQuery.QueryType.CREATE_DOCUMENT ||
                 baseQuery.getQueryType() == FirestoreQuery.QueryType.PATCH_DOCUMENT){
            Map<String, Object> serialized = serialize();
            baseQuery.update(serialized);
            host.query(baseQuery, new FirestoreTaskReceiver() {
                @Override
                public void connectionFailed() {
                    exception = new NoConnectionException();
                    evaluationFinalize();
                }
                @Override
                public void requestFailed(int response_code, Map<String, String> headers, String rawResponse) {
                    exception = new QueryRefusedException("Response code: %d. Raw response: %s".formatted(response_code, rawResponse));
                    evaluationFinalize();
                }
                @Override
                public void queryCompleted(FirestoreTaskResult queryResult) {
                    setEvaluationFlag();
                }
            });
        } else {
            exception = new FirestoreModelException("Not implemented");
            evaluationFinalize();
        }
    }
    protected void taskResultCleanse(FirestoreTaskResult queryResult){

    }
    protected Map<String, Object> serialize(){
        return null;
    }
}

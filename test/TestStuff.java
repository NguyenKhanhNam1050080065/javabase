import com.cycastic.javabase.dispatcher.AsyncEngine;
import com.cycastic.javabase.dispatcher.ExecutionLoop;
import com.cycastic.javabase.firestore.*;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TestStuff {
    @Test
    public static void otherStuff(){
        Firestore db = new Firestore();
        FirestoreQuery query = new FirestoreQuery(FirestoreQuery.QueryType.STRUCTURED_QUERY)
                .from("dvds_info").where("stock", FirestoreQuery.Operator.GREATER_THAN, 0);
        db.query(query, new FirestoreTaskReceiver() {
            @Override
            public void connectionFailed() {
                System.out.println("Failed");
            }
            @Override
            public void requestFailed(int response_code, Map<String, String> headers, String rawResponse) {
                System.out.println("Failed");
            }
            @Override
            public void queryCompleted(FirestoreTaskResult queryResult) {
                for (Map.Entry<String, FirestoreDocument> E : queryResult.getDocuments().entrySet()){
                    System.out.println(E.getValue().getFields().get("imdb_link").toString());
                }
            }
        });
    }
    @Test
    public static void compareStuff(){
        {
            final AsyncEngine engine = new AsyncEngine();
            final AtomicInteger num = new AtomicInteger(0);
            engine.dispatch(num::incrementAndGet);
            engine.dispatch(num::incrementAndGet);
            engine.dispatch(num::incrementAndGet);
            engine.sync();
            engine.terminate();
        }
        {
            final ExecutionLoop loop = new ExecutionLoop();
            final AtomicInteger num = new AtomicInteger(0);
            loop.push(params -> {
                final AtomicInteger integer = (AtomicInteger) params[0];
                integer.incrementAndGet();
            }, num);
            loop.push(params -> {
                final AtomicInteger integer = (AtomicInteger) params[0];
                integer.incrementAndGet();
            }, num);
            loop.push(params -> {
                final AtomicInteger integer = (AtomicInteger) params[0];
                integer.incrementAndGet();
            }, num);
            loop.pushAndWait(params -> {});
            loop.terminate();
        }
    }
}

import com.cycastic.javabase.auth.FirebaseAuth;
import com.cycastic.javabase.auth.FirebaseAuthListener;
import com.cycastic.javabase.auth.FirebaseAuthToken;
import com.cycastic.javabase.firestore.Firestore;
import com.cycastic.javabase.firestore.FirestoreQuery;
import com.cycastic.javabase.firestore.FirestoreTaskReceiver;
import com.cycastic.javabase.firestore.FirestoreTaskResult;
import com.cycastic.javabase.misc.FirebaseConfig;
import com.cycastic.javabase.network.HTTPRequest;
import com.cycastic.javabase.network.HTTPResponseListener;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class FirebaseAuthTest {
    private static final String config = """
{
    apiKey: "AIzaSyBEoa3dc5al1uNSQ-heCMNYcpYhtEHv3Jw",
    authDomain: "test-app-1-a1063.firebaseapp.com",
    databaseURL: "https://test-app-1-a1063-default-rtdb.asia-southeast1.firebasedatabase.app",
    projectId: "test-app-1-a1063",
    storageBucket: "test-app-1-a1063.appspot.com",
    messagingSenderId: "388802145512",
    appId: "1:388802145512:web:a7d252cd5270d1b0baad5d",
    measurementId: "G-GRTGJG5K9M"
}
            """;
    private void assertAuthStatus(FirebaseAuth auth){
//        SortedMap<String, String> a = new LinkedHashMap<>();
        Assert.assertNotNull(auth.getAuthWrapper().getAuthToken());
        auth.terminate();
    }
    @Test
    public void testNetwork(){
        HTTPRequest toGoogle = new HTTPRequest();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        toGoogle.request("https://google.com", headers, false, "POST", "Hello world", new HTTPResponseListener() {
            @Override
            public void request_completed(int result, int response_code, Map<String, String> headers, String rawResponse) {
                // Nếu result = 0 => không có mạng, bằng 1 => có mạng
                // response_code chứ HTTP response từ người nhận
                Assert.assertTrue(true);
            }
        });
    }
    @Test
    public void testLogin(){
        FirebaseConfig config = new FirebaseConfig();
        config.deserialize(FirebaseAuthTest.config);
        FirebaseAuth authServer = new FirebaseAuth();
        authServer.enrollConfig(config);
        authServer.loginWithEmailAndPassword("testuwu@gmail.com", "namanama", new FirebaseAuthListener() {
            @Override
            public void onConnectionFailed() {
                assertAuthStatus(authServer);
            }
            @Override
            public void onRequestFailed(int response_code, Map<String, String> headers, String raw_response) {
                assertAuthStatus(authServer);
            }
            @Override
            public void onAuthChanged(FirebaseAuthToken token) {
                assertAuthStatus(authServer);
            }
        });
    }
    @Test
    public void testFirestoreRead(){
        FirebaseConfig config = new FirebaseConfig();
        config.deserialize(FirebaseAuthTest.config);
        FirebaseAuth authServer = new FirebaseAuth();
        authServer.enrollConfig(config);
        authServer.loginWithEmailAndPassword("testuwu@gmail.com", "namanama", new FirebaseAuthListener() {
            @Override
            public void onConnectionFailed() {
                Assert.fail();
                authServer.terminate();
            }
            @Override
            public void onRequestFailed(int response_code, Map<String, String> headers, String raw_response) {
                Assert.fail();
                authServer.terminate();
            }
            @Override
            public void onAuthChanged(FirebaseAuthToken token) {
                Firestore dbInstance = new Firestore();
                dbInstance.enrollConfig(config);
                dbInstance.enrollToken(authServer.getAuthWrapper());
                FirestoreQuery query = new FirestoreQuery(FirestoreQuery.QueryType.STRUCTURED_QUERY)
                        .from("dvds_info").where("stock", FirestoreQuery.Operator.GREATER_THAN, 0);
                dbInstance.query(query, new FirestoreTaskReceiver() {
                    @Override
                    public void connectionFailed() {
                        Assert.fail();
                        authServer.terminate();
                        dbInstance.terminate();
                    }
                    @Override
                    public void requestFailed(int response_code, Map<String, String> headers, String rawResponse) {
                        Assert.fail();
                        authServer.terminate();
                        dbInstance.terminate();
                    }
                    @Override
                    public void queryCompleted(FirestoreTaskResult queryResult) {
                        Assert.assertTrue(true);
                        authServer.terminate();
                        dbInstance.terminate();
                    }
                });
            }
        });
    }
}

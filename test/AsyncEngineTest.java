import com.cycastic.javabase.dispatcher.AsyncEngine;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AsyncEngineTest {
    public static int FIBONACCI_CEIL = 30;
//    public static void doNothing() {}
    public static long fibonacci(int n){
        if (n <= 1L) return n;
        return (long)n + fibonacci(n - 1) + fibonacci(n - 2);
    }
    @Test
    public static void testFault(){
        new AsyncEngine().dispatch(() -> {
            int a = 1;
            int b = 0;
            int c = a / b;
        });
    }
    @Test
    public static void testGeneralDispatched() {
        final AsyncEngine engine = new AsyncEngine(true);
        final AtomicInteger num  = new AtomicInteger(1);
        engine.dispatch(num::incrementAndGet);
        engine.dispatch(num::incrementAndGet);
        engine.dispatch(num::incrementAndGet);
        engine.dispatch(() -> Assert.assertEquals(num.get(), 4));
    }
    @Test
    public static void testGeneralSynchronized() {
        final AsyncEngine engine = new AsyncEngine(true);
        final AtomicInteger num  = new AtomicInteger(0);
        engine.sync(num::incrementAndGet);
        engine.sync(num::incrementAndGet);
        engine.sync(num::incrementAndGet);
        engine.sync(num::incrementAndGet);
        engine.sync(() -> Assert.assertEquals(num.get(), 4));
    }
    @Test
    public static void testWaitDispatched(){
        final AsyncEngine engine = new AsyncEngine(true);
        engine.dispatch(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        });
//        engine.dispatch(() -> Assert.assertFalse(false));
        Assert.assertEquals(engine.queueSize(), 1);
    }
    @Test
    public static void testWaitSynchronized(){
        final AsyncEngine engine = new AsyncEngine(true);
        engine.sync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        });
        engine.sync(() -> Assert.assertFalse(false));
    }
    @Test
    public static void testHotDispatched(){
        final AsyncEngine engine = new AsyncEngine(AsyncEngine.MODE_HOT, true);
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> Assert.assertFalse(false));
    }
    @Test
    public static void testColdDispatched(){
        final AsyncEngine engine = new AsyncEngine(AsyncEngine.MODE_COLD, true);
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> Assert.assertFalse(false));
    }
    @Test
    public static void testOnSpotDispatched(){
        final AsyncEngine engine = new AsyncEngine(AsyncEngine.MODE_ON_SPOT, true);
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> Assert.assertFalse(false));
    }
    @Test
    public static void testHotSynchronized(){
        final AsyncEngine engine = new AsyncEngine(AsyncEngine.MODE_HOT, true);
        engine.sync(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.sync(() -> Assert.assertFalse(false));
    }
    @Test
    public static void testColdSynchronized(){
        final AsyncEngine engine = new AsyncEngine(AsyncEngine.MODE_COLD, true);
        engine.sync(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.sync(() -> Assert.assertFalse(false));
    }
    @Test
    public static void testOnSpotSynchronized(){
        final AsyncEngine engine = new AsyncEngine(AsyncEngine.MODE_ON_SPOT, true);
        engine.sync(() -> fibonacci(FIBONACCI_CEIL));
        engine.dispatch(() -> fibonacci(FIBONACCI_CEIL));
        engine.sync(() -> Assert.assertFalse(false));
    }
}

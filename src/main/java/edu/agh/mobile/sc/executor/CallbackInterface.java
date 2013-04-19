package edu.agh.mobile.sc.executor;

//import android.webkit.JavascriptInterface;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class CallbackInterface {

    private final AtomicReference<CountDownLatch> latch = new AtomicReference<CountDownLatch>(new CountDownLatch(1));
    private final AtomicReference<String> result = new AtomicReference<String>();

    public void restart() {
        result.set(null);
        latch.set(new CountDownLatch(1));
    }

//    @JavascriptInterface
    public void returns(String result) {
        this.result.set(result);
        latch.get().countDown();
    }

    public String getResult(long time, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        boolean timeoutOccurred = !latch.get().await(time, timeUnit);
        if (timeoutOccurred) {
            throw new TimeoutException("Did not get result in " + time + " " + timeUnit);
        }
        return result.get();

    }
}
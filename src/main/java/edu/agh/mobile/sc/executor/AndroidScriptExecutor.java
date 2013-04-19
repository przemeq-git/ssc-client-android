package edu.agh.mobile.sc.executor;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import edu.agh.mobile.sc.Constants;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Przemyslaw Dadel
 */
public class AndroidScriptExecutor {

    public static final String CALLBACK = "callback";
    private static final String scripTemplate = "javascript: var args = JSON.parse('%s'); \n" +
            "%s; \n" +
            "var startTime = Date.now(); \n" +
            "var functionResult = main(args); \n" +
            "var elapsed = Date.now() - startTime; \n" +
            "var result = new Object(); \n" +
            "result.elapsed = elapsed; \n" +
            "result.result = functionResult; \n" +
            "callback.returns(JSON.stringify(result));";
    private final String source = "<html><head></head>><body></body></html>";
    private final AtomicReference<WebView> webViewRef = new AtomicReference<WebView>();
    private final AtomicReference<CallbackInterface> callbackInterfaceRef = new AtomicReference<CallbackInterface>();
    private final CountDownLatch initialized = new CountDownLatch(1);
    private final LoggingWebChromeClient webChromeClient = new LoggingWebChromeClient();
    private final Handler handler;

    public AndroidScriptExecutor(Context applicationContext) {
        handler = new Handler(applicationContext.getMainLooper());
    }

    public void start(final Context applicationContext) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final WebView webView = new WebView(applicationContext);
                    webView.getSettings().setJavaScriptEnabled(true);

                    final CallbackInterface callbackInterface = new CallbackInterface();
                    webView.addJavascriptInterface(callbackInterface, CALLBACK);
                    callbackInterfaceRef.set(callbackInterface);

                    webView.setWebChromeClient(webChromeClient);

                    webView.loadData(source, "text/html", "utf-8");
                    webViewRef.set(webView);
                } catch (Throwable t) {
                    Log.e(Constants.SC_LOG_TAG, "Error on Ex start", t);
                } finally {
                    initialized.countDown();
                }

            }
        });
    }

    public Result execute(final String script, final String args, long time, TimeUnit timeUnit) throws ScriptExecutionException, TimeoutException {
        long startTime = System.currentTimeMillis();
        try {
            Log.d(Constants.SC_LOG_TAG, "Waiting for JS");
            initialized.await();
            Log.d(Constants.SC_LOG_TAG, "JS Executor initialized");
            callbackInterfaceRef.get().restart();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //TODO make sure page is loaded
                    webViewRef.get().loadUrl(String.format(scripTemplate, args, script));
                }
            });
            final String jsResult = callbackInterfaceRef.get().getResult(time, timeUnit);
            final long executorTime = System.currentTimeMillis() - startTime;
            return new Result(jsResult, executorTime);
        } catch (TimeoutException ex) {
            final List<String> errors = webChromeClient.takeErrors();
            if (errors.isEmpty()) {
                throw ex;
            }
            throw new ScriptExecutionException(ex, errors);
        } catch (InterruptedException e) {
            throw new ScriptExecutionException(e);
        }
    }

}

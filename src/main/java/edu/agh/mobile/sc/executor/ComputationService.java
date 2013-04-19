package edu.agh.mobile.sc.executor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.communication.ArgResolver;
import edu.agh.mobile.sc.communication.MessageBuilder;
import edu.agh.mobile.sc.communication.ResponseServer;
import edu.agh.mobile.sc.providers.BatteryDataProvider;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Przemyslaw Dadel
 */

public class ComputationService extends Service implements Executor {

    private AndroidScriptExecutor engine;

    @Override
    public void onCreate() {
        Log.d(Constants.SC_LOG_TAG, "Computation service is being created");
        engine = new AndroidScriptExecutor(this);
        engine.start(this);
        super.onCreate();
        Log.d(Constants.SC_LOG_TAG, "Computation service was created");
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.SC_LOG_TAG, "Computation service is destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ExecutorBinder(this);
    }

    @Override
    public void execute(String script, ArgResolver argResolver, ResponseServer responseServer, MessageBuilder messageBuilder, long timeout) {

        try {
            final String actualArgument = argResolver.getArgument();
            if (Log.isLoggable(Constants.SC_LOG_TAG, Log.DEBUG)) {
                Log.d(Constants.SC_LOG_TAG, String.format("Will execute a script in a service %s, %s", script, actualArgument));
            }
            final Result execute = engine.execute(script, actualArgument, timeout, TimeUnit.MILLISECONDS);
            messageBuilder.with("result", execute.getResult());
            messageBuilder.with("executorTime", execute.getExecutorTime());
            if (Log.isLoggable(Constants.SC_LOG_TAG, Log.DEBUG)) {
                Log.d(Constants.SC_LOG_TAG, execute.toString());
            }
        } catch (Exception e) {
            Log.e(Constants.SC_LOG_TAG, "Script error", e);
            messageBuilder.with("error", e.getMessage());
        } finally {
            final Map<String, Object> batteryData = new BatteryDataProvider().getData(this);
            messageBuilder.withMap("batteryEnd", batteryData);
            messageBuilder.with("end", System.currentTimeMillis());
            Log.i(Constants.SC_LOG_TAG, "Sending response");
            responseServer.publish(messageBuilder.build());
        }

    }
}

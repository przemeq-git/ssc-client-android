package edu.agh.mobile.sc.commands;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.communication.*;
import edu.agh.mobile.sc.executor.ComputationService;
import edu.agh.mobile.sc.executor.Executor;
import edu.agh.mobile.sc.executor.ExecutorServiceConnection;
import edu.agh.mobile.sc.providers.BatteryDataProvider;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class ComputationCommand extends AbstractExtrasCommand {

    private final ExecutorServiceConnection serviceConnection = new ExecutorServiceConnection();
    private final SocialComputingServer server;

    public ComputationCommand(SocialComputingServer server) {
        this.server = server;
    }

    @Override
    public boolean accepts(Bundle extras) {
        return hasExtrasValue(extras, "compute");
    }

    @Override
    public void execute(Context context, Bundle extras) {
        final long startTime = System.currentTimeMillis();
        final Map<String, Object> batteryData = new BatteryDataProvider().getData(context);

        final Intent intent = new Intent(context, ComputationService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        final MessageBuilder builder = MessageBuilder.create(context, "ctoken");
        builder.with("id", getExtrasValue(extras, "id", String.class));
        builder.with("send", Long.valueOf(getExtrasValue(extras, "send", String.class)));
        builder.with("batteryStart", new JSONObject(batteryData));
        builder.with("start", startTime);

        final Executor executor;
        try {
            executor = serviceConnection.getExecutor();
            if (executor != null) {
                final String script = getExtrasValue(extras, "src", String.class);
                final String args = getExtrasValue(extras, "arg", String.class);
                final String timeout = getExtrasValue(extras, "tout", String.class);
                final String serverURL = getExtrasValue(extras, "server", String.class);
                final ResponseServer responseServer = new SCCResponseServer(serverURL, server);
                final ArgResolver argResolver = new SCCArgResolver(args, server);
                executor.execute(script, argResolver, responseServer, builder, Long.valueOf(timeout));
            } else {
                Log.e(Constants.SC_LOG_TAG, "Executor is not available...");
            }
        } catch (InterruptedException e) {
            Log.e(Constants.SC_LOG_TAG, "Failed to get executor ", e);
        }

    }

}

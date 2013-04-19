package edu.agh.mobile.sc.commands;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.communication.MessageBuilder;
import edu.agh.mobile.sc.communication.SocialComputingServer;
import edu.agh.mobile.sc.providers.BatteryDataProvider;
import edu.agh.mobile.sc.providers.MemDataProvider;
import edu.agh.mobile.sc.providers.NetworkDataProvider;

/**
 * @author Przemyslaw Dadel
 */
public class StatusCommand extends AbstractExtrasCommand {

    private final SocialComputingServer server;

    public StatusCommand(SocialComputingServer server) {
        this.server = server;
    }

    @Override
    public boolean accepts(Bundle extras) {
        if (hasExtrasValue(extras, "status")) {
            return true;
        }
        return false;
    }

    @Override
    public void execute(Context context, Bundle extras) {
        if (hasExtrasValue(extras, "status")) {
            String responseHost = getExtrasValue(extras, "status", String.class);
            final String token;
            if (hasExtrasValue(extras, "token")) {
                token = getExtrasValue(extras, "token", String.class);
            } else {
                token = "token";
            }
            Log.d(Constants.SC_LOG_TAG, "Reporting device status");
            reportDeviceStatus(responseHost, token, context);
        }
    }

    private void reportDeviceStatus(String server, String token, Context context) {
        final MessageBuilder builder = MessageBuilder.create(context, token);
        try {

            final BatteryDataProvider batteryProvider = new BatteryDataProvider();
            final NetworkDataProvider networkProvider = new NetworkDataProvider();
            final MemDataProvider memoryDataProvider = new MemDataProvider();

            builder.withMap("battery", batteryProvider.getData(context));
            builder.withMap("network", networkProvider.getData(context));
            builder.withMap("memory", memoryDataProvider.getData(context));

            this.server.sendStatus(server, builder.build());
        } catch (Exception e) {
            Log.e(Constants.SC_LOG_TAG, "Exception", e);
            builder.with("error", e.getMessage());
            builder.with("cause", e.getCause().toString());
            this.server.sendStatus(server, builder.build());
        }
    }

}

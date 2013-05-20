package edu.agh.mobile.sc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import edu.agh.mobile.sc.commands.*;
import edu.agh.mobile.sc.communication.IdRegistration;
import edu.agh.mobile.sc.communication.SocialComputingServer;
import edu.agh.mobile.sc.executor.ComputationService;

/**
 * @author Przemyslaw Dadel
 */
public class GCMIntentService extends GCMBaseIntentService {

    private final Settings settings = new Settings();
    private final SocialComputingServer server = new SocialComputingServer();
    private final StatusCommand statusCommand = new StatusCommand(server);
    private final MessageCommand messageCommand = new MessageCommand();
    private final RegisterCommand registerCommand = new RegisterCommand();
    private final ComputationCommand computationCommand = new ComputationCommand(server);
    private final DynamicCommand dynamicCommand = new DynamicCommand();

    public GCMIntentService() {
        super(Constants.SENDER_ID);
    }

    @Override
    public void onCreate() {
        final ComponentName componentName = startService(new Intent(this, ComputationService.class));
        Log.d(Constants.SC_LOG_TAG, "GCM Started " + componentName);
        super.onCreate();
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        if (!settings.isMobileComputingActive(context)) {
            Log.d(Constants.SC_LOG_TAG, "Computing platform was deactivated");
            return;
        }

        Log.d(Constants.SC_LOG_TAG, "Received message: " + intent.toString());

        final Bundle extras = intent.getExtras();

        if (statusCommand.accepts(extras)) {
            statusCommand.execute(context, extras);
        }

        if (messageCommand.accepts(extras)) {
            messageCommand.execute(context, extras);
        }

        if (registerCommand.accepts(extras)) {
            registerCommand.execute(context, extras);
        }

        if (computationCommand.accepts(extras)) {
            computationCommand.execute(context, extras);
        }

        if (dynamicCommand.accepts(extras)) {
            dynamicCommand.execute(context, extras);
        }

    }

    @Override
    protected void onError(Context context, String errorId) {
        Log.i(Constants.SC_LOG_TAG, "Error occurred : " + errorId);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(Constants.SC_LOG_TAG, "Received registration id = " + registrationId);
        final IdRegistration idRegistration = new IdRegistration();
        idRegistration.registerId(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(Constants.SC_LOG_TAG, "Received un-registration id = " + registrationId);
        final IdRegistration idRegistration = new IdRegistration();
        idRegistration.unregisterId(context, registrationId);
    }

}

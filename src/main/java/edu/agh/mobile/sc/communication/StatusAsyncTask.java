package edu.agh.mobile.sc.communication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import edu.agh.mobile.sc.Constants;
import org.json.JSONObject;

/**
 * @author Przemyslaw Dadel
 */
public class StatusAsyncTask extends AsyncTask<JSONObject, Void, Void> {

    private final Context context;
    private final String address;

    public StatusAsyncTask(Context context, String address) {
        this.context = context;
        this.address = address;
    }

    @Override
    protected Void doInBackground(JSONObject... params) {
        final SocialComputingServer server = new SocialComputingServer();
        try {
            final JSONObject message = params[0];
            server.sendStatus(address, message);
        } catch (Exception e) {
            Log.e(Constants.SC_LOG_TAG, "Exception", e);
            final MessageBuilder messageBuilder = MessageBuilder.create(context, "error");
            messageBuilder.with("error", e.getMessage());
            final String cause = e.getCause() == null ? "None" : e.getCause().toString();
            messageBuilder.with("cause", cause);
            server.sendStatus(address, messageBuilder.build());
        }

        return null;
    }
}

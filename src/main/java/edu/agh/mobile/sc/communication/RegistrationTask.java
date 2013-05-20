package edu.agh.mobile.sc.communication;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author Przemyslaw Dadel
 */
public class RegistrationTask extends AsyncTask<String, Void, Void> {

    private final Context context;

    public RegistrationTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        final String id = params[0];
        final boolean register = Boolean.valueOf(params[1]);
        final IdRegistration registration = new IdRegistration();
        if (register) {
            registration.registerId(context, id);
        } else {
            registration.unregisterId(context, id);
        }
        return null;
    }
}

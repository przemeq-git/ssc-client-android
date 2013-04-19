package edu.agh.mobile.sc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.android.gcm.GCMRegistrar;
import edu.agh.mobile.sc.commands.DynamicCommand;

public class SocialComputerActivity extends Activity {

    private final IdRegistration idRegistration = new IdRegistration();
    private final Settings settings = new Settings();

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupActivationCheckBox();

        if (settings.isMobileComputingActive(this)) {
            GCMRegistrar.checkDevice(this);
            GCMRegistrar.checkManifest(this);
            final String regId = GCMRegistrar.getRegistrationId(this);
            if (regId.equals("")) {
                Log.d(Constants.SC_LOG_TAG, "Registering in GCM");
                GCMRegistrar.register(this, Constants.SENDER_ID);
            } else {
                Log.d(Constants.SC_LOG_TAG, "Already registered in GCM with id = " + regId);
                if (!settings.isRegistrationSend(this)) {
                    Log.d(Constants.SC_LOG_TAG, "Registering in SC with id = " + regId);
                    idRegistration.registerId(this, regId);
                } else {
                    Log.d(Constants.SC_LOG_TAG, "Already registered in SC with id = " + regId);
                }
            }
        } else {
            //not active
            if (!settings.isUnregistrationSend(this)) {
                idRegistration.unregisterId(this, settings.getRegistrationId(this));
            }
        }
        updateActivationStatusView();

    }

    private void updateActivationStatusView() {
        final TextView activationStatusView = (TextView) findViewById(R.id.sccActivationStatus);
        if (settings.isMobileComputingActive(this)) {
            activationStatusView.setText(String.format("SCC is enabled on your device."));
        } else {
            activationStatusView.setText(String.format("SCC is disabled on your device."));
        }
    }

    private void setupActivationCheckBox() {
        final CheckBox activation = (CheckBox) findViewById(R.id.activation);
        activation.setChecked(settings.isMobileComputingActive(SocialComputerActivity.this));

        activation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checkBoxValue) {
                Log.d(Constants.SC_LOG_TAG, "Changed activation status to " + checkBoxValue);
                final Context context = SocialComputerActivity.this;
                final boolean active = settings.isMobileComputingActive(context);

                if (checkBoxValue != active) {
                    settings.setMobileComputingActive(SocialComputerActivity.this, checkBoxValue);
                    if (checkBoxValue) {
                        //if activated - register
                        idRegistration.registerId(context, settings.getRegistrationId(context));
                    } else {
                        idRegistration.unregisterId(context, settings.getRegistrationId(context));
                    }
                }
                updateActivationStatusView();
            }
        });
    }

}


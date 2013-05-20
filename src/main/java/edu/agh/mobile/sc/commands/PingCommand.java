package edu.agh.mobile.sc.commands;

import android.content.Context;
import android.os.Bundle;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.Fields;
import edu.agh.mobile.sc.assumtions.PingDataProvider;
import edu.agh.mobile.sc.communication.SocialComputingServer;
import edu.agh.mobile.sc.providers.IdentifierProvider;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class PingCommand extends AbstractExtrasCommand {

    private final PingDataProvider dataProvider = new PingDataProvider();
    private final SocialComputingServer server;

    public PingCommand(SocialComputingServer server) {
        this.server = server;
    }

    @Override
    public boolean accepts(Bundle extras) {
        return hasExtrasValue(extras, "ping");
    }

    @Override
    public void execute(Context context, Bundle extras) {
        final String responseServer = getExtrasValue(extras, "ping", String.class);
        final String host = getExtrasValue(extras, "host", String.class);
        final Integer timeout = Integer.valueOf(getExtrasValue(extras, "timeout", String.class));
        final String token = getExtrasValue(extras, "token", String.class);
        final Map<String, Object> pingResult = dataProvider.getData(context, host, timeout);
        final Map<String, Object> result = responseMap(context, token);
        result.put("ping", pingResult);
        server.sendStatus(responseServer, new JSONObject(result));
    }

    private Map<String, Object> responseMap(Context context, String token) {
        final IdentifierProvider statusHelper = new IdentifierProvider(context);
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put(Fields.DEVICE_ID, statusHelper.getDeviceId());
        result.put("token", token);
        result.put("timestamp", System.currentTimeMillis());
        result.put("version", Constants.VERSION);
        return result;
    }


}

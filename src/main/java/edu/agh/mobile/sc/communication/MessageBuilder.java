package edu.agh.mobile.sc.communication;

import android.content.Context;
import edu.agh.mobile.sc.Constants;
import edu.agh.mobile.sc.Fields;
import edu.agh.mobile.sc.providers.IdentifierProvider;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class MessageBuilder {

    private final Map<String,Object> message;

    private MessageBuilder(Context context, String token) {
        message = responseMap(context, token);
    }

    public static MessageBuilder create(Context context, String token) {
        return new MessageBuilder(context,token);
    }

    public MessageBuilder withMap(String key, Map<String, Object> data) {
        message.put(key, new JSONObject(data));
        return this;
    }

    public MessageBuilder with(String key, Object data) {
        message.put(key, data);
        return this;
    }

    public JSONObject build(){
        return new JSONObject(message);
    }


    private Map<String, Object> responseMap(Context context, String token) {
        final IdentifierProvider statusHelper = new IdentifierProvider(context);
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put(Fields.DEVICE_ID, statusHelper.getDeviceId());
        result.put(Fields.TOKEN, token);
        result.put(Fields.TIMESTAMP, System.currentTimeMillis());
        result.put(Fields.VERSION, Constants.VERSION);
        return result;
    }

}

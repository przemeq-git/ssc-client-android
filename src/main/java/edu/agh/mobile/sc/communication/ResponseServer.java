package edu.agh.mobile.sc.communication;

import org.json.JSONObject;

/**
 * @author Przemyslaw Dadel
 */
public interface ResponseServer {

    void publish(JSONObject object);

}

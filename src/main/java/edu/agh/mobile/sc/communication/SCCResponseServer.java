package edu.agh.mobile.sc.communication;

import org.json.JSONObject;

/**
 * @author Przemyslaw Dadel
 */
public class SCCResponseServer implements ResponseServer {

    private final String publishURL;
    private final SocialComputingServer server;

    public SCCResponseServer(String publishURL, SocialComputingServer server) {
        this.publishURL = publishURL;
        this.server = server;
    }

    @Override
    public void publish(JSONObject object) {
        server.sendStatus(publishURL, object);
    }
}

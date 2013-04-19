package edu.agh.mobile.sc.communication;

import android.util.Log;
import edu.agh.mobile.sc.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Przemyslaw Dadel
 */
public class SocialComputingServer {

    private final HttpClient httpClient = new DefaultHttpClient();

    public boolean registerId(String endpoint, JSONObject registrationData) {
        return updateRegistration(endpoint + "/register", registrationData);
    }

    public boolean unregisterId(String endpoint, JSONObject registrationData) {
        return updateRegistration(endpoint + "/unregister", registrationData);

    }

    public boolean updateRegistration(String endpoint, JSONObject registrationData) {
        try {
            final StringEntity requestEntity = new StringEntity(registrationData.toString(), "UTF-8");
            Log.d(Constants.SC_LOG_TAG, "Sending registration update to " + endpoint);
            final StatusLine statusLine = sendJSONPost(endpoint, requestEntity);
            Log.d(Constants.SC_LOG_TAG, "Status code = " + statusLine.getStatusCode());
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
            return false;
        } catch (UnsupportedEncodingException e) {
            Log.e(Constants.SC_LOG_TAG, e.getMessage(), e);
        } catch (ClientProtocolException e) {
            Log.e(Constants.SC_LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(Constants.SC_LOG_TAG, e.getMessage(), e);
        }
        return false;
    }

    public void sendStatus(String server, JSONObject jsonObject) {
        try {
            final StringEntity requestEntity = new StringEntity(jsonObject.toString());
            Log.d(Constants.SC_LOG_TAG, "Sending status request");
            sendJSONPost(server, requestEntity);
        } catch (UnsupportedEncodingException e) {
            Log.e(Constants.SC_LOG_TAG, e.getMessage(), e);
        } catch (ClientProtocolException e) {
            Log.e(Constants.SC_LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(Constants.SC_LOG_TAG, e.getMessage(), e);
        }
    }

    public String get(String server) throws IOException {
        final HttpGet get = new HttpGet(server);
        final HttpResponse response = httpClient.execute(get);
        final int statusCode = response.getStatusLine().getStatusCode();
        final String responseString = EntityUtils.toString(response.getEntity());
        Log.d(Constants.SC_LOG_TAG, "Server response = " + responseString);
        if (statusCode != HttpStatus.SC_OK) {
            throw new IOException("Failed request with status code " + statusCode);
        }
        return responseString;
    }

    private StatusLine sendJSONPost(String server, StringEntity requestEntity) throws IOException {
        final HttpPost post = new HttpPost(server);
        post.setEntity(requestEntity);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);
        Log.d(Constants.SC_LOG_TAG, "Server response = " + EntityUtils.toString(response.getEntity()));
        return response.getStatusLine();
    }

}

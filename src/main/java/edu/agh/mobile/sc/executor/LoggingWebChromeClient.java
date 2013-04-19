package edu.agh.mobile.sc.executor;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import edu.agh.mobile.sc.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Przemyslaw Dadel
 */
public class LoggingWebChromeClient extends WebChromeClient {

    private final List<String> errors = new ArrayList<String>(2);

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        Log.v(Constants.SC_LOG_TAG, "invoked: onConsoleMessage() - " + sourceID + ":"
                + lineNumber + " - " + message);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

        StringBuilder msg = new StringBuilder(consoleMessage.messageLevel().name()).append('\t')
                .append(consoleMessage.message()).append('\t')
                .append(consoleMessage.sourceId()).append("\t Script line number: (")
                .append(consoleMessage.lineNumber()).append(")\n");

        Log.e(Constants.SC_LOG_TAG, consoleMessage.toString());
        Log.e(Constants.SC_LOG_TAG, msg.toString());
        errors.add(msg.toString());
        return true;
    }

    /**
     * Return and clean produced errors
     * @return
     */
    public List<String> takeErrors() {
        final List<String> result = new ArrayList<String>(errors);
        errors.clear();
        return result;
    }

}

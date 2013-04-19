package edu.agh.mobile.sc.executor;

import java.util.List;

/**
 * @author Przemyslaw Dadel
 */
public class ScriptExecutionException extends Exception {

    public ScriptExecutionException(Exception e) {
        super(e);
    }

    public ScriptExecutionException(Exception e, List<String> errors) {
        super(makeMessage(e, errors), e);
    }

    private static String makeMessage(Exception e, List<String> errors) {
        final StringBuilder sb = new StringBuilder(e.getMessage());
        sb.append("\n");
        for (String error : errors) {
            sb.append(error);
            sb.append("\n");
        }
        return sb.toString();
    }

}

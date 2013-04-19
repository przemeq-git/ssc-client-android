package edu.agh.mobile.sc.commands;

import android.os.Bundle;

/**
 * @author Przemyslaw Dadel
 */
public abstract class AbstractExtrasCommand implements Command {

    protected boolean hasExtrasValue(Bundle bundle, String command) {
        final Object commandObject = bundle.get(command);
        return commandObject != null;
    }

    protected <T> T getExtrasValue(Bundle bundle, String key, Class<T> clazz) {
        final Object value = bundle.get(key);
        return clazz.cast(value);
    }


}

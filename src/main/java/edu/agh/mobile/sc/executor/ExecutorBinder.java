package edu.agh.mobile.sc.executor;

import android.os.Binder;

/**
 * @author Przemyslaw Dadel
 */
public class ExecutorBinder extends Binder {

    private final Executor executor;

    public ExecutorBinder(Executor executor) {
        this.executor = executor;
    }

    public Executor getService() {
        return executor;
    }
}

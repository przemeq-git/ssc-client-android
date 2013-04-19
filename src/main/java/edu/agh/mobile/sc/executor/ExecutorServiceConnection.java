package edu.agh.mobile.sc.executor;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Przemyslaw Dadel
 */
public class ExecutorServiceConnection implements ServiceConnection {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition executorAvailable = lock.newCondition();
    private Executor executor;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        lock.lock();
        try {
            executor = ((ExecutorBinder) service).getService();
            executorAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        lock.lock();
        try {
            executor = null;
        } finally {
            lock.unlock();
        }
    }

    public Executor getExecutor() throws InterruptedException {
        lock.lock();
        try {
            while (executor == null) {
                executorAvailable.await();
            }
            return executor;
        } finally {
            lock.unlock();
        }
    }
}

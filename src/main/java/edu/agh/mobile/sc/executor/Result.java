package edu.agh.mobile.sc.executor;

/**
 * @author Przemyslaw Dadel
 */
public class Result {

    private final String result;
    private final long executorTime;

    public Result(String result, long executorTime) {
        this.result = result;
        this.executorTime = executorTime;
    }

    public String getResult() {
        return result;
    }

    public long getExecutorTime() {
        return executorTime;
    }

    @Override
    public String toString() {
        return "Result{" +
                "result=" + result +
                ", executorTime=" + executorTime +
                '}';
    }
}

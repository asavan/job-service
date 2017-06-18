package ru.asavan.job.framework;

/**
 * Created by asavan on 11.11.2016.
 */
public interface TimebleAndExecutable extends Timeble, BaseJob {
    void doLoggableJob();

    String getName();

    boolean isRunning();
}

package ru.asavan.job.framework;

public interface TimebleAndExecutable extends Timeble, BaseJob {
    void doLoggableJob();

    String getName();

    boolean isRunning();
}

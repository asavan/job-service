package ru.asavan.job.framework;

import java.util.Date;

/**
 * Created by asavan on 11.11.2016.
 */
public abstract class AbstractPrintableJob implements TimebleAndExecutable {
    private final LogableJob logableJob = new LogableJob();

    @Override
    public void doLoggableJob() {
        logableJob.doLoggableJob(this, this.getClass().getSimpleName());
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isRunning() {
        return getLastTimeStarted() != null &&
                (getLastTimeFinished() == null || getLastTimeFinished().before(getLastTimeStarted()));
    }

    abstract protected void doJobScheduled();

    @Override
    public Date getLastTimeStarted() {
        return logableJob.getLastTimeStarted();
    }

    @Override
    public Date getLastTimeFinished() {
        return logableJob.getLastTimeFinished();
    }

}

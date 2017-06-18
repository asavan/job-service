package ru.asavan.job.framework;

abstract class AbstractLooperJob<T> extends AbstractPrintableJob {
    @Override
    public void doJob() throws Exception {
        getLooper().doJob();
    }

    abstract Looper<T> getLooper();
}

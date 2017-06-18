package ru.asavan.job.framework;


/**
 *
 * Created by asavan on 11.11.2016.
 */
abstract class AbstractLooperJob<T> extends AbstractPrintableJob {
    @Override
    public void doJob() throws Exception {
       getLooper().doJob();
    }

    abstract Looper<T> getLooper();
}

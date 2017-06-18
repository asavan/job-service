package ru.asavan.job.framework;

/**
 *
 * Created by asavan on 11.11.2016.
 */
public abstract class SimpleLooperJob<T> extends AbstractLooperJob<T> implements Selector<T>, Updater<T> {
    @Override
    Looper<T> getLooper() {
        return new Looper<>(
                this,
                this);
    }
}

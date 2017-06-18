package ru.asavan.job.framework;

public abstract class SimpleLooperJob<T> extends AbstractLooperJob<T> implements Selector<T>, Updater<T> {
    @Override
    Looper<T> getLooper() {
        return new Looper<>(
                this,
                this);
    }
}

package ru.asavan.job.framework;

interface Updater<T> {
    // return true if updated;
    boolean update(T item) throws Exception;
}

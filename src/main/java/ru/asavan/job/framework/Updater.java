package ru.asavan.job.framework;

/**
 *
 * Created by asavan on 11.11.2016.
 */
interface Updater<T> {
    // return true if updated;
    boolean update(T item) throws Exception;
}

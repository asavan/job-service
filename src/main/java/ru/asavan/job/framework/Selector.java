package ru.asavan.job.framework;

import java.util.List;

/**
 *
 * Created by asavan on 11.11.2016.
 */
interface Selector<T> {
    List<T> selectList(int offset, int limit);
}

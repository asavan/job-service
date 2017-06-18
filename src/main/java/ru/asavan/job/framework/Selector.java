package ru.asavan.job.framework;

import java.util.List;

interface Selector<T> {
    List<T> selectList(int offset, int limit);
}

package ru.asavan.job.framework;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * Created by asavan on 11.11.2016.
 */
class Looper<T> implements BaseJob {
    private static final Logger log = LoggerFactory.getLogger(LogableJob.class);
    private static final int LIMIT  = 20;
    private static final int MAX_ERROR_COUNT = 1000;

    private final Selector<T> selector;
    private final Updater<T> updater;

    Looper(Selector<T> selector, Updater<T> updater) {
        this.selector = selector;
        this.updater= updater;
    }

    @Override
    public void doJob() {
        int offset = 0;
        while (offset < MAX_ERROR_COUNT) {
            List<T> list = selector.selectList(offset, LIMIT);
            if (list.isEmpty()) {
                break;
            }
            for (T item : list) {
                try {
                    boolean res = updater.update(item);
                    if (!res) {
                        ++offset;
                    }
                } catch (Exception e) {
                    ++offset;
                    log.error("Job update failed", e);
                }
            }
        }
    }
}

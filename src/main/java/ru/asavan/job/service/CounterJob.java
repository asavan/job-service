package ru.asavan.job.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.asavan.job.framework.BaseJob;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: a.savanovich
 * Date: 20.08.16
 * Time: 16:54
 */
@Service
public class CounterJob implements BaseJob {
    private static final long UPDATE_DELAY = 5 * 1000; // 5c

    private final AtomicInteger counter = new AtomicInteger();

    @Scheduled(fixedDelay = UPDATE_DELAY)
    public void scheduleRotate() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }

    @Override
    public void doJob() {
        scheduleRotate();
    }
}

package ru.asavan.job.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.asavan.job.framework.AbstractPrintableJob;

/**
 *
 * Created by asavan on 11.11.2016.
 */
@Service
public class LongTestJob extends AbstractPrintableJob {

    private static final long UPDATE_DELAY = 1000 * 60 * 5;

    @Override
    public void doJob() throws Exception {
        Thread.sleep(40000);
    }

    @Scheduled(fixedDelay = UPDATE_DELAY)
    protected void doJobScheduled() {
        doLoggableJob();
    }

}

package ru.asavan.job.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

final class LogableJob implements Timeble {
    private static final Logger log = LoggerFactory.getLogger(LogableJob.class);
    private final Object syncObj = new Object();
    private Date lastTimeStarted;
    private Date lastTimeFinished;


    void doLoggableJob(BaseJob job, String name) {
        log.debug(name + " planned");
        synchronized (syncObj) {
            lastTimeStarted = new Date();
            log.debug(name + " started");
            try {
                job.doJob();
            } catch (Exception ex) {
                log.error(name + " crashed", ex);
            }
            log.debug(name + " finished");
            lastTimeFinished = new Date();
        }
    }

    public Date getLastTimeStarted() {
        return lastTimeStarted;
    }

    public Date getLastTimeFinished() {
        return lastTimeFinished;
    }

}

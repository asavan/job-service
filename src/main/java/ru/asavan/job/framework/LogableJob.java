package ru.asavan.job.framework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Date;

final class LogableJob implements Timeble {
    private static final Logger log = LogManager.getLogger(LogableJob.class);
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

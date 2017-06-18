package ru.asavan.job.framework;

import org.apache.log4j.Logger;
import java.util.Date;

/**
 *
 * Created by asavan on 11.11.2016.
 */
final class LogableJob implements Timeble {
    private static final Logger log = Logger.getLogger(LogableJob.class);
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

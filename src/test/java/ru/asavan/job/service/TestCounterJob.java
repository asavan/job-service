package ru.asavan.job.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: a.savanovich
 * Date: 02.03.12
 * Time: 16:06
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/applicationContext-*.xml"})
public class TestCounterJob {

	@Autowired
	private CounterJob counterJob;

    @Test
    public void testCommentsUpdate() {
        counterJob.doJob();
    }
}

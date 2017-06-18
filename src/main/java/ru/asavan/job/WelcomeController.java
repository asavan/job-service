package ru.asavan.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.asavan.job.framework.TimebleAndExecutable;
import ru.asavan.job.service.CounterJob;
import ru.asavan.job.utils.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class WelcomeController {


	private static final String LIVE_CHECK = "/live-check";
	private static final String ROOT = "/";

	private final List<TimebleAndExecutable> jobsToShow;
	// to see if schedule annotation does not work
	private final CounterJob counterJob;

	@Autowired
	public WelcomeController(List<TimebleAndExecutable> jobsToShow, CounterJob counterJob) {
		this.jobsToShow = jobsToShow;
		this.counterJob = counterJob;
	}

	@RequestMapping(ROOT)
	public String welcome(Map<String, Object> model) {
		model.put("jobs", jobsToShow);
		return "welcome";
	}

	@RequestMapping("/run-job/{jobname}")
	public void runjob(@PathVariable String jobname, HttpServletResponse resp) throws ServletException, IOException {
		runJobByName(resp, jobname);
	}


	@RequestMapping(LIVE_CHECK)
	public void getLiveCheck(HttpServletResponse resp) throws IOException {
		WebUtils.setDefaultHeaders(resp);
		resp.getWriter().write("OK " + counterJob.getCounter());
		resp.getWriter().flush();
	}

	private void runJobByName(HttpServletResponse resp, String jobName) throws IOException {
		WebUtils.setDefaultHeaders(resp);
		resp.getWriter().write("RUN JOB <br>");
		boolean jobFounded = false;

		// should never happen
		if (jobsToShow == null) {
			resp.getWriter().write("jobs list is null <br>");
			resp.getWriter().flush();
			return;
		}

		try {
			for (TimebleAndExecutable job : jobsToShow) {
				if (jobName.equalsIgnoreCase(job.getClass().getSimpleName())) {
					jobFounded = true;
					resp.getWriter().write("Job started " + jobName + "<br>");
					resp.getWriter().flush();
					job.doLoggableJob();
				}
			}
			if (jobFounded) {
				resp.getWriter().write("Job finished " + jobName + "<br>");
			} else {
				resp.getWriter().write("Job not found " + jobName + "<br>");
			}
		} catch (Exception ex) {
			resp.getWriter().write("Job crached " + jobName + "<br>");
		}
		resp.getWriter().flush();
	}

}
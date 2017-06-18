package ru.asavan.job.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.asavan.job.framework.Timeble;
import ru.asavan.job.service.CounterJob;
import ru.asavan.job.utils.WebUtils;
import ru.asavan.job.framework.TimebleAndExecutable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class JobServlet extends HttpServlet {

    private static final String LIVE_CHECK = "/live-check";
    private static final String RUN_JOB = "/run-job/";
    private static final String ROOT = "/main/";

    @Autowired
    private List<TimebleAndExecutable> jobsToShow;
    // to see if schedule annotation does not work
    @Autowired
    private CounterJob counterJob;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (LIVE_CHECK.equals(req.getRequestURI())) {
            WebUtils.setDefaultHeaders(resp);
            resp.getWriter().write("OK " + counterJob.getCounter());
            resp.getWriter().flush();
            return;
        }
        if (ROOT.equals(req.getRequestURI())) {
            showMain(req, resp);
            return;
        }
        if ("/main2/".equals(req.getRequestURI())) {
            showMain2(req, resp);
            return;
        }
        String url = req.getRequestURI();
        if (url != null && url.startsWith(RUN_JOB)) {
            String jobName = url.substring(RUN_JOB.length());
            runJobByName(resp, jobName);
            return;
        }
        super.doGet(req, resp);
    }

    private void showMain(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<table>");
        for (Timeble job : jobsToShow) {
            out.println("<tr>");
            String name = job.getClass().getSimpleName();
            out.println("<td>");
            out.println("<a href=\"" + RUN_JOB + name + "\">" + name + "</a>");
            out.println("</td>");
            if (job.getLastTimeStarted() != null) {
                out.println("<td><span>" + job.getLastTimeStarted() + "</span></td>");
                out.println("<td>");
                if (job.getLastTimeFinished() == null || job.getLastTimeFinished().before(job.getLastTimeStarted())) {
                    out.println(" <span style=\"color:red\">" + "RUNNING" + "</span> ");
                } else {
                    out.println(" <span>" + job.getLastTimeFinished() + "</span> ");
                }
                out.println("</td>");
            }
            out.println("</tr>");
        }
        out.println("</table>");
    }

    private void showMain2(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("jobs", jobsToShow);
        System.out.print(request.getContextPath());
        RequestDispatcher rd = request.getRequestDispatcher("/job-grid.jsp");
        rd.forward(request, response);
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

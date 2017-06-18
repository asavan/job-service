package ru.asavan.job.main;

import org.apache.log4j.Logger;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.asavan.job.utils.ParametersUtils;
import ru.asavan.job.servlet.JobServlet;

import java.io.File;
import java.net.URI;

// import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * @author ameshkov
 */
public class JobStarter {

    private static Logger log = Logger.getLogger(JobStarter.class);
    private static ClassPathXmlApplicationContext applicationContext;
    private static Server server;


    private static ServletHolder defaultServletHolder(URI baseUri)
    {
        ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
        holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
        holderDefault.setInitParameter("dirAllowed", "true");
        return holderDefault;
    }

    public static void main(String[] args) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    onShutDown();
                }
            }));

            int port = Integer.valueOf(ParametersUtils.getParameter(args, "--port", "8091"));
            String serverLogs = ParametersUtils.getParameter(args, "--server-logs", "./logs/jetty-yyyy_mm_dd.request.log");

            applicationContext = new ClassPathXmlApplicationContext("spring/applicationContext-*.xml", "classpath*:/spring/applicationContext-dao.xml", "classpath*:/spring/applicationContext-geo.xml");

            log.info("Starting job service on port: " + port);
            server = new Server(port);

            // https://ru.stackoverflow.com/questions/494417/%D0%9A%D0%B0%D0%BA-%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%BD%D0%BE-%D0%BE%D1%82%D0%BE%D0%B1%D1%80%D0%B0%D0%B7%D0%B8%D1%82%D1%8C-jsp
//            JettyJasperInitializer sci = new JettyJasperInitializer();
//            ContainerInitializer initializer = new ContainerInitializer(sci, null);
//            List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
//            initializers.add(initializer);


            JobServlet servlet = applicationContext.getBean(JobServlet.class);
            ServletHolder servletHolder = new ServletHolder("default", servlet);
            HandlerCollection handlers = new HandlerCollection();

            WebAppContext webappcontext = new WebAppContext();
            // webappcontext.setDescriptor("web.xml");
            webappcontext.setContextPath("/");

//            webappcontext.setAttribute("org.eclipse.jetty.containerInitializers", initializers);

//            ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
//            webappcontext.setClassLoader(jspClassLoader);

            File warPath = new File("C:\\Develop\\planeta-git\\Server\\Services\\job-service\\src\\main\\resources");
            System.out.print(warPath.exists());
            Resource webroot = Resource.newResource(warPath.getAbsolutePath());
            if (!webroot.exists())
            {
                System.err.println("Resource does not exist: " + webroot);
                System.exit(-1);
            }

            if (!webroot.isDirectory())
            {
                System.err.println("Resource is not a directory: " + webroot);
                System.exit(-1);
            }
            // webappcontext.setWar(warPath.getAbsolutePath());
            System.out.print(warPath.getAbsolutePath());
            webappcontext.setBaseResource(webroot);
            // webappcontext.setResourceBase(warPath.getAbsolutePath());

            RequestLogHandler requestLogHandler = getRequestLogHandler(serverLogs);





//            ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//            servletContextHandler.setContextPath("/main2");
//            servletContextHandler.setResourceBase("file://C:\\Develop\\planeta-git\\planeta\\Server\\Services\\job-service\\src\\main\\webapp");
//            servletContextHandler.addServlet(servletHolder,"/");
            webappcontext.addServlet(jspServletHolder(), "*.jsp");
            webappcontext.addServlet(servletHolder, "/");
            handlers.setHandlers(new Handler[]{webappcontext, requestLogHandler});
            server.setHandler(handlers);


            server.start();

            log.info("Job server has been started successfully");

            server.join();
        } catch (Exception ex) {
            log.error("Unhandled exception in the main thread", ex);
        }
    }

    private static RequestLogHandler getRequestLogHandler(String serverLogs) {
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        NCSARequestLog requestLog = new NCSARequestLog(serverLogs);
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT+3");
        requestLogHandler.setRequestLog(requestLog);
        return requestLogHandler;
    }

    private static ServletHolder jspServletHolder()
    {
        ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
        holderJsp.setInitParameter("fork", "false");
        holderJsp.setInitParameter("xpoweredBy", "false");
        holderJsp.setInitParameter("compilerTargetVM", "1.8");
        holderJsp.setInitParameter("compilerSourceVM", "1.8");
        holderJsp.setInitParameter("keepgenerated", "true");
        return holderJsp;
    }

    private static void onShutDown() {
        log.info("Stopping server and application context");
        try {
            server.stop();
            log.info("Server has been stopped successfully");
            applicationContext.close();
            log.info("Application context has been closed successfully");
        } catch (Exception ex) {
            log.error("Error stopping server or closing application context", ex);
        }
    }
}

package ru.asavan.job.main;

import org.apache.jasper.servlet.JspServlet;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.asavan.job.servlet.JobServlet;
import ru.asavan.job.utils.ParametersUtils;

import java.net.URI;

// import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * @author ameshkov
 */
public class JobStarter {

    private static Logger log = Logger.getLogger(JobStarter.class);
    private static ClassPathXmlApplicationContext applicationContext;
    private static Server server;


    public static void main(String[] args) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(JobStarter::onShutDown));

            int port = Integer.valueOf(ParametersUtils.getParameter(args, "--port", "8091"));

            applicationContext = new ClassPathXmlApplicationContext("spring/applicationContext-*.xml");

            log.info("Starting job service on port: " + port);
            server = new Server(port);

            // https://ru.stackoverflow.com/questions/494417/%D0%9A%D0%B0%D0%BA-%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%BD%D0%BE-%D0%BE%D1%82%D0%BE%D0%B1%D1%80%D0%B0%D0%B7%D0%B8%D1%82%D1%8C-jsp

//            JettyJasperInitializer sci = new JettyJasperInitializer();
//            ContainerInitializer initializer = new ContainerInitializer(sci, null);
//            List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
//            initializers.add(initializer);


            JobServlet servlet = applicationContext.getBean(JobServlet.class);
            ServletHolder servletHolder = new ServletHolder("default", servlet);

            WebAppContext webappcontext = new WebAppContext();
            // webappcontext.setDescriptor("web.xml");
            webappcontext.setContextPath("/");

            // webappcontext.setAttribute("org.eclipse.jetty.containerInitializers", initializers);

//            ClassLoader jspClassLoader = new URLClassLoader(new URL[0], JobStarter.class.getClassLoader());
//            webappcontext.setClassLoader(jspClassLoader);

            URI baseUri = JobStarter.class.getResource("/").toURI();
            Resource webroot = Resource.newResource(baseUri);
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
            webappcontext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*taglibs.*\\.jar$");
            webappcontext.setBaseResource(webroot);

            log.info("add jsp");

            webappcontext.addServlet(jspServletHolder(), "*.jsp");
            log.info("add dafault");
            webappcontext.addServlet(servletHolder, "/");
            log.info("set handlers");

            server.setHandler(webappcontext);

            log.info("before start");


            server.start();

            log.info("Job server has been started successfully");

            server.join();
        } catch (Exception ex) {
            log.error("Unhandled exception in the main thread", ex);
        }
    }

    private static ServletHolder jspServletHolder()
    {
        ServletHolder holderJsp = new ServletHolder("jsp", JspServlet.class);
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

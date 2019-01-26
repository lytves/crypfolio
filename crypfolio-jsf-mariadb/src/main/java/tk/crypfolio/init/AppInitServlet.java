package tk.crypfolio.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/*
* this servlet is executed at the starts of the application, it defines in web.xml listener
* */
public class AppInitServlet implements ServletContextListener {

    private static final Logger LOGGER = LogManager.getLogger(AppInitServlet.class);

    /*
     * application startup method
     * */
    public void contextInitialized(ServletContextEvent sce) {

        LOGGER.info("CrypFolio application initializing.");
        LOGGER.info("\n" +
                "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██████░░░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██░░░░░░██████░░██░░██░░██████░░\n" +
                "░░██░░░░░░██░░░░░░██░░██░░██░░██░░\n" +
                "░░██░░░░░░██░░░░░░██░░██░░██░░██░░\n" +
                "░░██████░░██░░░░░░██████░░██████░░\n" +
                "░░░░░░░░░░░░░░░░░░░░░░██░░██░░░░░░\n" +
                "░░░░░░░░░░░░░░░░░░░░████░░██░░░░░░\n" +
                "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██████░░░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██░░░░░░░░░░░░██░░░░██░░░░░░░░░░\n" +
                "░░████░░██████░░██░░░░░░░░██████░░\n" +
                "░░██░░░░██░░██░░██░░░░██░░██░░██░░\n" +
                "░░██░░░░██████░░████░░██░░██████░░\n" +
                "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n");

    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
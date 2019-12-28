package server;

import active_lead.ReflectionContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ReflectiContextServletListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("IN LISTENER");
        System.out.println("HERE");
        ServletContext servletContext = servletContextEvent.getServletContext();
        ReflectionContext reflectionContext = new ReflectionContext("/media/diemass/DATA/Projects/Java/Education/JavaLab/Chatserver/src/main/resources/db.properties");
        servletContext.setAttribute(Attribute.componentsContext.get(), reflectionContext);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}

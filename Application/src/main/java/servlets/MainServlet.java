package servlets;

import controllers.GoodController;
import controllers.SignUpController;
import active_lead.ReflectionContext;
import server.Attribute;
import server.Controller;
import server.Mapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/")
public class MainServlet extends HttpServlet {

    private Set<Controller> controllers = new HashSet<>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        Object rawAttribute = servletContext.getAttribute(Attribute.componentsContext.get());
        ReflectionContext reflectionContext = (ReflectionContext) rawAttribute;
        controllers.add((GoodController) reflectionContext.getComponent(GoodController.class.getSimpleName()));
        controllers.add((SignUpController) reflectionContext.getComponent(SignUpController.class.getSimpleName()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String URI = request.getRequestURI();
        for (Controller controller: controllers) {
            System.out.println("URI=" + URI);
            System.out.println("value=" + controller.getClass().getAnnotation(Mapping.class).value());
            if(controller.getClass().getAnnotation(Mapping.class).value().equals(URI)) {
                System.out.println("TRUE");
                controller.handle(request, response);
                return;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String URI = request.getRequestURI();
        for (Controller controller: controllers) {
            if(controller.getClass().getAnnotation(Mapping.class).value().equals(URI)) {
                controller.handle(request, response);
                return;
            }
        }
    }
}

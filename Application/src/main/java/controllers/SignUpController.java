package controllers;

import models.User;
import server.Attribute;
import server.Controller;
import server.Mapping;
import services.SignUpService;
import servlets.Page;
import servlets.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Mapping(Path.Constants.SIGN_UP)
public class SignUpController implements Controller {

    private SignUpService service;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        switch (method) {
            case "GET": {
                request.getRequestDispatcher(Page.signUp.get()).forward(request, response);
                break;
            }
            case "POST": {
                String username = request.getParameter(Attribute.username.get());
                String password = request.getParameter(Attribute.password.get());
                service.signUp(new User(null, username, password, false));
                response.sendRedirect(Path.Constants.GOODS);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

}

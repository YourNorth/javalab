package controllers;

import server.Attribute;
import server.Controller;
import server.Mapping;
import services.GoodService;
import servlets.Page;
import servlets.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Mapping(Path.Constants.GOODS)
public class GoodController implements Controller {

    GoodService service;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        switch (method) {
            case "GET": {
                request.setAttribute(Attribute.goods.get(), service.getGoods());
                request.getRequestDispatcher(Page.goods.get()).forward(request, response);
                break;
            }
            case "POST": {
                throw new IllegalArgumentException();
//                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }

    }

}

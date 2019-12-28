package server;

import active_lead.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Controller extends Component {

    void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}

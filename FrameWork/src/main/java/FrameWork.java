import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.*;
import jakarta.servlet.*;

public class FrameWork extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req , HttpServletResponse res) throws IOException , ServletException {
        String url = req.getRequestURI();

        String[] uri = url.split("/");
        String output = uri[2];
        
        PrintWriter out = res.getWriter();
        out.println(output);
    }
}
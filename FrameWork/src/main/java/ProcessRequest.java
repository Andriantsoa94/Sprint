import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.MethodExecutor;
import util.UrlMethod;

public class ProcessRequest extends HttpServlet {

    private HashMap<UrlMethod, Method> urlMap;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        this.urlMap = (HashMap<UrlMethod, Method>) context.getAttribute("urlMap");
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String url = req.getRequestURI();
        String[] uri = url.split("/");
        String output = uri[uri.length - 1];

        if (output.endsWith(".html")) {
            String cheminPhysique = getServletContext().getRealPath("/" + output);
            File fichier = new File(cheminPhysique);

            if (fichier.exists()) {
                res.setContentType("text/html;charset=UTF-8");
                Files.copy(fichier.toPath(), res.getOutputStream());
                return;
            } else {
                res.sendError(404, "Fichier introuvable");
                return;
            }
        }

        res.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = res.getWriter();

        String httpMethod = req.getMethod();
        UrlMethod cle = new UrlMethod("/" + output, httpMethod);

        Method method = (urlMap != null) ? urlMap.get(cle) : null;

        if (method == null) {
            out.println("Aucune methode trouvee pour l'URL : " + url + " et la Methode : " + httpMethod);
            out.println("");
            out.println("URLs disponibles :");

            if (urlMap != null) {
                for (UrlMethod u : urlMap.keySet()) {
                    Method m = urlMap.get(u);
                    out.println(u.getMethod() + " " + u.getUrl() + "    " + m.getDeclaringClass().getName() + "." + m.getName() + "()");
                }
            }
            return;
        }

        out.println("URL     : " + url);
        out.println("Methode : " + method.getName() + "()");
        out.println("Http Methode : " + httpMethod);
        out.println("Classe  : " + method.getDeclaringClass().getName());

        try {
            out.println("Execution de :" + method);
            MethodExecutor.execute(method);
        } catch (Exception e) {
            out.println("Erreur lors de l'execution du methode :" + e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }
}

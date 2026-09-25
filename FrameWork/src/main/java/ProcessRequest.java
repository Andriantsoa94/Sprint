import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import annotation.API;
import util.MethodExecutor;
import util.ModelAndView;
import util.UrlMethod;

public class ProcessRequest extends HttpServlet {

    private HashMap<UrlMethod, Method> urlMap;
    private String prefix;
    private String surfix;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        this.urlMap = (HashMap<UrlMethod, Method>) context.getAttribute("urlMap");
        this.prefix = context.getInitParameter("view-prefix");
        this.surfix = context.getInitParameter("view-suffix");
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

        String httpMethod = req.getMethod();
        UrlMethod cle = new UrlMethod("/" + output, httpMethod);

        Method method = (urlMap != null) ? urlMap.get(cle) : null;

        if (method == null) {
            res.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = res.getWriter();
            out.println("Aucune methode trouvee pour l'URL : " + url + " et la Methode : " + httpMethod);
            out.println("");
            out.println("URLs disponibles :");

            if (urlMap != null) {
                for (UrlMethod u : urlMap.keySet()) {
                    Method m = urlMap.get(u);
                    out.println(u.getMethod() + " " + u.getUrl() + "    " + m.getDeclaringClass().getName() + "."
                            + m.getName() + "()");
                }
            }
            return;
        }

        try {
            Object obj = MethodExecutor.execute(method);

            if (method.isAnnotationPresent(API.class)) {
                res.setContentType("application/json;charset=UTF-8");
                PrintWriter out = res.getWriter();
                out.print(obj instanceof String ? obj : toJson(obj));
                return;
            }

            if (obj instanceof ModelAndView) {
                ModelAndView mv = (ModelAndView) obj;
                Map<String, Object> map = mv.getModel();

                for (Map.Entry<String, Object> mm : map.entrySet()) {
                    req.setAttribute(mm.getKey(), mm.getValue());
                }
                String path = this.prefix + mv.getView() + this.surfix;
                RequestDispatcher requestDispatcher = req.getRequestDispatcher(path);
                requestDispatcher.forward(req, res);
                return;
            }

            res.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = res.getWriter();
            out.println("URL     : " + url);
            out.println("Methode : " + method.getName() + "()");
            out.println("Execution de :" + method);
            out.println("Resultat : " + obj);

        } catch (Exception e) {
            res.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = res.getWriter();
            out.println("Erreur lors de l'execution du methode :" + e);
        }
    }

    private String toJson(Object value) throws IllegalAccessException {
        if (value == null) {
            return "null";
        }
        if (value instanceof String || value instanceof Character) {
            return quote(value.toString());
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value.getClass().isArray()) {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < Array.getLength(value); i++) {
                if (i > 0) {
                    json.append(',');
                }
                json.append(toJson(Array.get(value, i)));
            }
            return json.append(']').toString();
        }
        if (value instanceof Iterable<?>) {
            StringBuilder json = new StringBuilder("[");
            Iterator<?> iterator = ((Iterable<?>) value).iterator();
            while (iterator.hasNext()) {
                if (json.length() > 1) {
                    json.append(',');
                }
                json.append(toJson(iterator.next()));
            }
            return json.append(']').toString();
        }
        if (value instanceof Map<?, ?>) {
            StringBuilder json = new StringBuilder("{");
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (json.length() > 1) {
                    json.append(',');
                }
                json.append(quote(String.valueOf(entry.getKey()))).append(':');
                json.append(toJson(entry.getValue()));
            }
            return json.append('}').toString();
        }

        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Field field : value.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            field.setAccessible(true);
            if (!first) {
                json.append(',');
            }
            json.append(quote(field.getName())).append(':');
            json.append(toJson(field.get(value)));
            first = false;
        }
        return json.append('}').toString();
    }

    private String quote(String value) {
        StringBuilder escaped = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    escaped.append(character);
            }
        }
        return escaped.append('"').toString();
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

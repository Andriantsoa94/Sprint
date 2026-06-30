package listner;

import java.lang.reflect.Method;
import java.util.HashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import util.FinderAnnotation;
import util.UrlMethod;

@WebListener
public class AppInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String controleurPackage = context.getInitParameter("controleurPackage");

        if (controleurPackage == null || controleurPackage.isBlank()) {
            throw new IllegalStateException("Parametre d'initialisation 'controleurPackage' manquant dans web.xml");
        }
        try {
            HashMap<UrlMethod, Method> urlMap = FinderAnnotation.getControleurMaping(controleurPackage);
            context.setAttribute("urlMap", urlMap);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

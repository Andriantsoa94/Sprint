package listner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import util.ApplicationContext;
import util.FinderAnnotation;
import util.SessionMap;
import util.UrlMethod;

@WebListener
public class AppInitializer implements ServletContextListener, HttpSessionListener {
    private ApplicationContext applicationContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        Map<String, SessionMap> sessions = new ConcurrentHashMap<>();
        context.setAttribute("sessions", sessions);

        String auth = context.getInitParameter("auth");
        if (auth == null || auth.isBlank()) {
            auth = "isAuth";
        }
        context.setAttribute("authKey", auth);
        System.out.println("[MonFramework] Cle de session pour @Auth : " + auth);

        String packageController = context.getInitParameter("controleurPackage");
        if (packageController == null || packageController.isBlank()) {
            System.out.println("[MonFramework] Erreur : le context-param 'controleurPackage' est manquant dans le web.xml");
        } else {
            try {
                HashMap<UrlMethod, Method> urlMap = FinderAnnotation.getControleurMaping(packageController);
                context.setAttribute("urlMap", urlMap);
                System.out.println("[MonFramework] " + urlMap.size() + " route(s) enregistree(s) depuis le package " + packageController);
            } catch (Exception e) {
                System.out.println("[MonFramework] Erreur lors du scan des controleurs :");
                e.printStackTrace();
            }
        }

        try {
            applicationContext = new ApplicationContext();
            applicationContext.initialize();
            context.setAttribute("MY_FRAMEWORK_CONTEXT", applicationContext);
        } catch (Exception e) {
            System.out.println("[MonFramework] Erreur lors de l'initialisation de l'ApplicationContext :");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Map<String, SessionMap> sessions = (Map<String, SessionMap>)
                se.getSession().getServletContext().getAttribute("sessions");
        if (sessions != null) {
            sessions.remove(se.getSession().getId());
        }
    }
}
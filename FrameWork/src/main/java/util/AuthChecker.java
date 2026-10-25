package util;

import java.lang.reflect.Method;

import annotation.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AuthChecker {

    public static boolean estAutorise(Method method, HttpServletRequest req, String authKey) {
        boolean protege = method.isAnnotationPresent(Auth.class)
                || method.getDeclaringClass().isAnnotationPresent(Auth.class);
        if (!protege) {
            return true;
        }

        HttpSession httpSession = req.getSession(false);
        if (httpSession == null) {
            return false;
        }
        return httpSession.getAttribute(authKey) != null;
    }
}

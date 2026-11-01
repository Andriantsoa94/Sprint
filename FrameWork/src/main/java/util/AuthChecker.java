package util;

import java.lang.reflect.Method;
import java.util.Collection;

import annotation.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AuthChecker {

    public static Auth findAuth(Method method) {
        Auth auth = method.getAnnotation(Auth.class);
        if (auth == null) {
            auth = method.getDeclaringClass().getAnnotation(Auth.class);
        }
        return auth;
    }

    public static boolean estConnecte(HttpServletRequest req, String authKey) {
        HttpSession httpSession = req.getSession(false);
        if (httpSession == null) {
            return false;
        }
        return httpSession.getAttribute(authKey) != null;
    }

    public static boolean hasAnyRole(HttpServletRequest req, String roleKey, String[] allowed) {
        if (allowed == null || allowed.length == 0) {
            return true;
        }
        HttpSession httpSession = req.getSession(false);
        if (httpSession == null) {
            return false;
        }
        Object role = httpSession.getAttribute(roleKey);
        if (role == null) {
            return false;
        }
        for (String r : allowed) {
            if (role instanceof Collection<?> roles) {
                if (roles.contains(r)) {
                    return true;
                }
            } else if (r.equals(role.toString())) {
                return true;
            }
        }
        return false;
    }
}

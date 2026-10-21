package util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionMap {
    private Map<String , Object> session = new HashMap<>();
    private HttpSession httpSession;

    public SessionMap(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public Object getSession(String key) {
        return session.get(key);
    }

    public void put(String key , Object value) {
        session.put(key ,value);
    }

    public void delete() {
        session.clear();
    }

    public void remove(String key) {
        session.remove(key);
    }

    public Map<String, Object> getData () {
        return session;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void ecraserSession() {
        for(String nom : Collections.list(httpSession.getAttributeNames())) {
            httpSession.removeAttribute(nom);
        }
        for (Map.Entry<String, Object> haha  : session.entrySet()) {
            httpSession.setAttribute(haha.getKey(), haha.getValue());
        }
    }

    public static SessionMap chargerSession(HttpServletRequest req, Map<String, SessionMap> sessions) {
        HttpSession httpSession = req.getSession();
        String id = httpSession.getId();

        SessionMap session = sessions.get(id);
        if (session == null) {
            session = new SessionMap(httpSession);
            sessions.put(id, session);
        }

        return session;
    }
}

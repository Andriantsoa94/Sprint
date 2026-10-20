package util;

import java.util.HashMap;
import java.util.Map;

public class SessionMap {
    private Map<String , Object> session = new HashMap<>();

    public SessionMap () {}

    public SessionMap(Map<String, Object> session) {
        this.session = session;
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
}

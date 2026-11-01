package util;

import java.util.Map;

public class ControllerFactory {

    public static Object getControllerInstance(Class<?> classController, Map<Class<?>, Object> instances) throws Exception {
        Object instance = instances.get(classController);
        if (instance != null) {
            return instance;
        }
        synchronized (instances) {
            instance = instances.get(classController);
            if (instance == null) {
                instance = classController.getDeclaredConstructor().newInstance();
                instances.put(classController, instance);
                System.out.println("[MonFramework] Instance creee pour le controleur " + classController.getName());
            }
        }
        return instance;
    }
}

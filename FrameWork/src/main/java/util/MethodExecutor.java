package util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import jakarta.servlet.http.HttpServletRequest;

public class MethodExecutor {
    public static Object execute (Method method, HttpServletRequest request) throws Exception {
        if (method == null) {
            System.err.println("Methode ne peut pas etre null");
        }

        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length]; 

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String nameParameter = parameter.getName();
            String value = request.getParameter(nameParameter);

            args[i] = convert(value, parameter.getType(), nameParameter);
        }

        Class<?> classController = method.getDeclaringClass();
        Object constructor = classController.getDeclaredConstructor().newInstance();

        return method.invoke(constructor, args);
    }

    private static Object convert(String value, Class<?> type, String parameterName) {
        if (value == null) {
            if (type.isPrimitive()) {
                throw new IllegalArgumentException(
                    "Le paramètre obligatoire est absent : " + parameterName
                );
            }
            return null;
        }

        if (type == String.class) {
            return value;
        }

        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }

        if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }

        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }

        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }

        throw new IllegalArgumentException(
            "Type non supporté : " + type.getName()
        );
    }
}
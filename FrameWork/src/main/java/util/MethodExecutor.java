package util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jakarta.servlet.http.*;
import annotation.RequestParam;

public class MethodExecutor {
    public static Object execute (Method method, HttpServletRequest req) throws Exception {
        if (method == null) {
            System.err.println("Methode ne peut pas etre null");
        }
        Class<?> classController = method.getDeclaringClass();
        Object constructor = classController.getDeclaredConstructor().newInstance();

        Parameter[] param = method.getParameters();
        Object[] args = new Object[param.length];

        for (int i = 0; i < args.length ;i++) {
            Parameter parameters = param[i];
            RequestParam requestParam = parameters.getAnnotation(RequestParam.class);
            String paramName = requestParam != null
                    ? requestParam.value()
                    : parameters.getName();
            String value = req.getParameter(paramName);

            args[i] = convert(value, parameters.getType(), paramName);
        }

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
package util;

import java.lang.reflect.*;
import java.util.*;

import jakarta.servlet.http.*;
import annotation.RequestParam;

public class MethodExecutor {

    public static Object execute(Method method, HttpServletRequest req) throws Exception {
        if (method == null) {
            System.err.println("Methode ne peut pas etre null");
        }
        Class<?> classController = method.getDeclaringClass();
        Object instance = classController.getDeclaredConstructor().newInstance();

        Parameter[] param = method.getParameters();
        Object[] args = new Object[param.length];

        for (int i = 0; i < args.length; i++) {
            Parameter parameters = param[i];

            if (isSimpleType(parameters.getType())) {
                RequestParam requestParam = parameters.getAnnotation(RequestParam.class);
                String paramName = requestParam != null
                        ? requestParam.value()
                        : parameters.getName();
                String value = req.getParameter(paramName);
                args[i] = convert(value, parameters.getType(), paramName);
            } else {
                args[i] = bindObject(parameters.getType(), req, "");
            }
        }

        return method.invoke(instance, args);
    }

    private static boolean isSimpleType(Class<?> type) {
        if (type == String.class) return true;
        else if (type.isPrimitive()) return true;
        else if (Number.class.isAssignableFrom(type)) return true;
        else if (type == Boolean.class) return true;
        return false;
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

        if (type == String.class) return value;
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        if (type == double.class || type == Double.class) return Double.parseDouble(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);

        throw new IllegalArgumentException("Type non supporté : " + type.getName());
    }

    private static Object bindObject(Class<?> type, HttpServletRequest req, String prefix) throws Exception {
        Object bean = type.getDeclaredConstructor().newInstance();

        for (Field f : type.getDeclaredFields()) {
            f.setAccessible(true); 
            Class<?> fieldType = f.getType();
            String base = prefix.isEmpty() ? f.getName() : prefix + "." + f.getName();

            if (List.class.isAssignableFrom(fieldType)) {
                f.set(bean, bindList(f, req, base));
            } else if (isSimpleType(fieldType)) {
                String v = req.getParameter(base);
                f.set(bean, convertNullable(v, fieldType, base));
            } else {
                f.set(bean, bindObject(fieldType, req, base));
            }
        }
        return bean;
    }

    private static Object convertNullable(String value, Class<?> type, String name) {
        if (value == null) {
            if (type.isPrimitive()) {
                throw new IllegalArgumentException("Champ obligatoire absent : " + name);
            }
            return null;
        }
        return convert(value, type, name);
    }

    private static List<Object> bindList(Field field, HttpServletRequest req, String base) throws Exception {
        Type generic = field.getGenericType();
        Class<?> elementType = (Class<?>) ((ParameterizedType) generic).getActualTypeArguments()[0];

        Set<Integer> indices = new TreeSet<>();
        String start = base + "[";
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String p = names.nextElement();
            if (p.startsWith(start)) {
                int end = p.indexOf(']', start.length());
                indices.add(Integer.parseInt(p.substring(start.length(), end)));
            }
        }

        List<Object> list = new ArrayList<>();
        for (int idx : indices) {
            list.add(bindObject(elementType, req, base + "[" + idx + "]"));
        }
        return list;
    }
}
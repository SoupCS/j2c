package ru.youngsmoke.j2c.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.reflections.Reflections;

/**
 * @author itskekoff
 * @since 21:39 of 04.07.2024
 */
public class Reflection {
    public static <T> List<? extends T> getClasses(String prefix, Class<T> classType) {
        return new Reflections(prefix).getSubTypesOf(classType).stream()
                .filter(c -> c.getName().startsWith(prefix))
                .map(c -> {
                    try {
                        return c.getConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }
}

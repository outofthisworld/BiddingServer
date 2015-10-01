package Util;

import java.util.Objects;

/**
 * Created by daleappleby on 1/10/15.
 */
public final class Preconditions {

    public static void checkNotNull(String className, Object object) {
        Objects.requireNonNull(object, "Precondition checkNotNull failed " + object.getClass().getName() + " was null when called from " + className);
    }

    public static <T> T tryCast(Class<T> klazz, Object object) {
        try {
            return (T) object;
        } catch (ClassCastException e) {
            throw e;
        }
    }
}

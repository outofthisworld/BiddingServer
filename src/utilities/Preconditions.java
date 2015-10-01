package utilities;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.Objects;


/**
 * The type Preconditions.
 */
public final class Preconditions {
    /**
     * Check not null.
     *
     * @param className the class name
     * @param object    the object
     * @throws NullPointerException the null pointer exception
     */
    public static void checkNotNull(String className, Object object) throws NullPointerException {
        Objects.requireNonNull(object, "Precondition checkNotNull failed " + object.getClass().getName() + " was null when called from " + className);
    }

    /**
     * Try cast.
     *
     * @param <T>    the type parameter
     * @param klazz  the klazz
     * @param object the object
     * @return the t
     * @throws NullPointerException the null pointer exception
     * @throws NullPointerException the null pointer exception
     */
    public static <T> T tryCast(Class<T> klazz, Object object) throws NullPointerException, ClassCastException {
        if (klazz == null || object == null)
            throw new NullPointerException("Precondition check failed - supplied one of the supplied parameters was null when attempting to cast");
        try {
            return (T) object;
        } catch (ClassCastException e) {
            throw e;
        }
    }

    /**
     * Value between inclusive.
     *
     * @param <T>   the type parameter
     * @param low   the low
     * @param high  the high
     * @param value the value
     * @throws InvalidArgumentException the invalid argument exception
     */
    public static <T extends Number> void valueBetweenInclusive(T low, T high, T value) throws InvalidArgumentException {
        double dblVal = value.doubleValue();
        if (dblVal <= low.doubleValue() || dblVal >= high.doubleValue())
            throw new InvalidArgumentException(new String[]{"min:" + low.toString(), "high:" + high.toString(), "actual:" + value.toString()});
    }


    /**
     * Value between exclusive.
     *
     * @param <T>   the type parameter
     * @param low   the low
     * @param high  the high
     * @param value the value
     * @throws InvalidArgumentException the invalid argument exception
     */
    public static <T extends Number> void valueBetweenExclusive(T low, T high, T value) throws InvalidArgumentException {
        double dblVal = value.doubleValue();
        if (dblVal < low.doubleValue() || dblVal > high.doubleValue())
            throw new InvalidArgumentException(new String[]{"min:" + low.toString(), "high:" + high.toString(), "actual:" + value.toString()});
    }
}

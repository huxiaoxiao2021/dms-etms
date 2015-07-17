package com.jd.common.context;

import java.lang.reflect.Method;

/**
 * The Class ConvertableContext.
 * 
 * @param <T>
 *            the generic type
 * @param <V>
 *            the value type
 */
public final class ConvertableContext<T, V> {

    /** The type. */
    private final Class<T> type;

    /** The value type. */
    private final Class<V> valueType;

    /** The value method. */
    private final Method valueMethod;

    /** The of method. */
    private final Method ofMethod;

    /** The zero parameter. */
    private final Object[] zeroParameter = new Object[0];

    /**
     * Instantiates a new convertable context.
     * 
     * @param type
     *            the type
     * @param valueType
     *            the value type
     * @param valueMethod
     *            the value method
     * @param ofMethod
     *            the of method
     */
    ConvertableContext(final Class<T> type, final Class<V> valueType,
            final Method valueMethod, final Method ofMethod) {
        this.type = type;
        this.valueType = valueType;
        this.valueMethod = valueMethod;
        this.ofMethod = ofMethod;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Gets the value type.
     * 
     * @return the value type
     */
    public Class<V> getValueType() {
        return valueType;
    }

    /**
     * Value.
     * 
     * @param source
     *            the source
     * @return the v
     * @throws Exception
     *             the exception
     */
    public V value(final T source) throws Exception {
        return valueType.cast(valueMethod.invoke(source, zeroParameter));
    }

    /**
     * Of.
     * 
     * @param value
     *            the value
     * @return the t
     * @throws Exception
     *             the exception
     */
    public T of(final V value) throws Exception {
        return type.cast(ofMethod.invoke(null, value));
    }

    /**
     * Builds the.
     * 
     * @param <T>
     *            the generic type
     * @param <V>
     *            the value type
     * @param className
     *            the class name
     * @return the convertable context
     * @throws RuntimeException
     *             the runtime exception
     */
    @SuppressWarnings("unchecked")
    public static <T, V> ConvertableContext<T, V> build(final String className)
            throws RuntimeException {
        Class<T> convertableClazz;
        try {
            convertableClazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("Class not found", cnfe);
        }
        return build(convertableClazz);
    }

    /**
     * Builds the.
     * 
     * @param <T>
     *            the generic type
     * @param <V>
     *            the value type
     * @param type
     *            the type
     * @return the convertable context
     * @throws RuntimeException
     *             the runtime exception
     */
    public static <T, V> ConvertableContext<T, V> build(final Class<T> type)
            throws RuntimeException {

        if (!type.isAnnotationPresent(Convertable.class)) {
            throw new RuntimeException(
                    "Class should be annotated by Convertable.");
        }

        Convertable convertable = type.getAnnotation(Convertable.class);
        Method valueMethod;
        try {
            valueMethod = type.getMethod(convertable.valueMethod(),
                    new Class[0]);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to obtain method:"
                    + convertable.valueMethod()
                    + "(which should have non-argument).", exception);
        }

        @SuppressWarnings("unchecked")
        Class<V> valueType = (Class<V>) valueMethod.getReturnType();

        Method ofMethod;
        try {
            ofMethod = type.getMethod(convertable.ofMethod(),
                    new Class[] { valueType });
        } catch (Exception exception) {
            throw new RuntimeException("Failed to obtain method:"
                    + convertable.ofMethod() + "(which should have a(n) "
                    + valueType.getName() + " typed argument)", exception);
        }

        return new ConvertableContext<T, V>(type, valueType, valueMethod,
                ofMethod);
    }
}

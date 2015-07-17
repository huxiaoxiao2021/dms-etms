package com.jd.common.springmvc.converter;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.jd.common.context.Convertable;
import com.jd.common.context.ConvertableContext;

/**
 * A factory for creating StringToEnumConverter objects.
 */
final public class StringToEnumConverterFactory implements
        ConverterFactory<String, Enum<?>> {

    /** The conversion service. */
    private final ConversionService conversionService;

    /**
     * Instantiates a new string to enum converter factory.
     * 
     * @param conversionService
     *            the conversion service
     */
    public StringToEnumConverterFactory(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.core.convert.converter.ConverterFactory#getConverter
     * (java.lang.Class)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends Enum<?>> Converter<String, T> getConverter(
            Class<T> targetType) {
        return new StringToEnum(conversionService, targetType);
    }

    /**
     * The Class StringToEnum.
     * 
     * @param <T>
     *            the generic type
     */
    static class StringToEnum<T extends Enum<T>> implements
            Converter<String, T> {

        /** The conversion service. */
        private final ConversionService conversionService;

        /** The enum type. */
        private final Class<T> enumType;

        /**
         * Instantiates a new string to enum.
         * 
         * @param conversionService
         *            the conversion service
         * @param enumType
         *            the enum type
         */
        StringToEnum(ConversionService conversionService, Class<T> enumType) {
            this.conversionService = conversionService;
            this.enumType = enumType;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.springframework.core.convert.converter.Converter#convert(java
         * .lang.Object)
         */
        public T convert(String source) {
            if (enumType.isAnnotationPresent(Convertable.class)) {
                ConvertableContext<T, Object> convertableContext = ConvertableContext
                        .build(enumType);

                Object fromValue;
                if (source == null || source.length() == 0) {
                    // It's an empty enum identifier: reset the enum value to
                    // null.
                    fromValue = null;
                } else {
                    fromValue = conversionService.convert(source,
                            convertableContext.getValueType());
                }

                try {
                    return enumType.cast(convertableContext.of(fromValue));
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            } else if (source == null || source.length() == 0) {
                return null;
            }
            // fall back to normal conversion.
            return Enum.valueOf(this.enumType, source.trim());
        }
    }

}

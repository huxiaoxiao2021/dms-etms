package com.jd.common.springmvc.converter;

import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;

/**
 * The Class DefaultFormatterRegistrar.
 */
public class DefaultFormatterRegistrar implements FormatterRegistrar {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.format.FormatterRegistrar#registerFormatters(org.
     * springframework.format.FormatterRegistry)
     */
    @Override
    public void registerFormatters(FormatterRegistry registry) {
        if (registry instanceof ConversionService) {
            ConversionService service = (ConversionService) registry;
            registry.addConverterFactory(new StringToEnumConverterFactory(
                    service));
        }
        // register custom converter.
    }

}

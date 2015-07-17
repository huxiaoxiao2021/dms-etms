package com.jd.bluedragon.distribution.resolver;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

public class DataResolver implements WebBindingInitializer {

    private static final String DATE_FORMATTER = "yyyy-MM-dd";

    @Override
    public void initBinder(final WebDataBinder binder, final WebRequest request) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DataResolver.DATE_FORMATTER); // 可以設定任意的日期格
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}

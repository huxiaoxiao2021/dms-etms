package com.jd.bluedragon.distribution.resolver;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

/**
 * Property editor that trims Strings. Optionally allows transforming an empty
 * string into a null value. Needs to be explictly registered, e.g. for command
 * binding.
 * 
 * @see org.springframework.validation.DataBinder#registerCustomEditor
 * @see org.springframework.web.bind.BindInitializer#initBinder
 */
public class StringEditor extends PropertyEditorSupport {

    private String charsToDelete;

    private final boolean emptyAsNull;

    /**
     * Create a new StringTrimmerEditor instance.
     * 
     * @param emptyAsNull
     *            whether to transform an empty string to null
     */
    public StringEditor(final boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
    }

    /**
     * Create a new StringTrimmerEditor instance.
     * 
     * @param charsToDelete
     *            a set of characters to delete, in addition to trimming an
     *            input String. Useful for deleting unwanted line breaks. E.g.
     *            "\r\n\f" will delete all new lines and line feeds in a String.
     * @param emptyAsNull
     *            whether to transform an empty string to null
     */
    public StringEditor(final String charsToDelete, final boolean emptyAsNull) {
        this.charsToDelete = charsToDelete;
        this.emptyAsNull = emptyAsNull;
    }

    @Override
    public String getAsText() {
        final Object value = this.getValue();
        return value != null ? value.toString() : "";
    }

    @Override
    public void setAsText(final String text) {
        final String illegalCharacter = "";

        if (text == null) {
            this.setValue(null);
        } else {
            String value = text.trim();
            if (this.charsToDelete != null) {
                value = StringUtils.deleteAny(value, this.charsToDelete);
            }
            value = value.replaceAll(illegalCharacter, "_");

            if (this.emptyAsNull && "".equals(value)) {
                this.setValue(null);
            } else {
                this.setValue(value);
            }
        }
    }

}

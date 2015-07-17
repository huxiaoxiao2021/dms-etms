package com.jd.bluedragon.distribution.web;

/**
 * The Class JsonResult.
 */
public class JsonResult {

    /** The success. */
    private boolean success;

    /** The result. */
    private Object result;

    /**
     * Instantiates a new json result.
     */
    public JsonResult() {

    }

    /**
     * Instantiates a new json result.
     * 
     * @param success
     *            the success
     * @param result
     *            the result
     */
    public JsonResult(boolean success, Object result) {
        this.success = success;
        this.result = result;
    }

    /**
     * Checks if is success.
     * 
     * @return true, if is success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success.
     * 
     * @param success
     *            the new success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the result.
     * 
     * @return the result
     */
    public Object getResult() {
        return result;
    }

    /**
     * Sets the result.
     * 
     * @param result
     *            the new result
     */
    public void setResult(Object result) {
        this.result = result;
    }

}

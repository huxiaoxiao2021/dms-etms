package com.jd.bluedragon.core.message;

public class MessageException extends RuntimeException {
    
    private static final long serialVersionUID = -2661618190401954560L;
    
    public MessageException(String msg) {
        super(msg);
    }
    
    public MessageException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}

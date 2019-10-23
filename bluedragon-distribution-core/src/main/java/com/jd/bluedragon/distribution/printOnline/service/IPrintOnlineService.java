package com.jd.bluedragon.distribution.printOnline.service;

public interface IPrintOnlineService {

    /**
     * 逆向交接清单 线上签逻辑
     * @param sendCode
     * @return
     */
    boolean reversePrintOnline(String sendCode);
}

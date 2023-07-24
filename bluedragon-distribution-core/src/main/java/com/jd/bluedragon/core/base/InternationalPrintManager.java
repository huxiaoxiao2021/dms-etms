package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.print.domain.international.InternationalPrintReq;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/7/19 2:10 PM
 */
public interface InternationalPrintManager {

    /**
     * 生成pdf链接
     * 
     * @param request
     * @return
     */
    String generatePdfUrl(InternationalPrintReq request);
}

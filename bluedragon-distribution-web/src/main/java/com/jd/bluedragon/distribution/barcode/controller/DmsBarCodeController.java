package com.jd.bluedragon.distribution.barcode.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年07月10日 14时:28分
 */
@Controller
@RequestMapping("barcode")
public class DmsBarCodeController {
    private static final Log logger = LogFactory.getLog(DmsBarCodeController.class);

    /**
     * 返回主页面
     *
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/barcode/barcode";
    }
}

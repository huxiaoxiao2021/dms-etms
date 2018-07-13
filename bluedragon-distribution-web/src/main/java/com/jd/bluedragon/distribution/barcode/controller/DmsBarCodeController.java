package com.jd.bluedragon.distribution.barcode.controller;

import com.jd.bluedragon.distribution.barcode.domain.BarCode;
import com.jd.bluedragon.distribution.barcode.service.BarcodeService;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 69码查询
 * @date 2018年07月10日 14时:28分
 */
@Controller
@RequestMapping("barcode")
public class DmsBarCodeController extends DmsBaseController {
    private static final Log logger = LogFactory.getLog(DmsBarCodeController.class);

    @Autowired
    BarcodeService barcodeService;

    /**
     * 返回主页面
     *
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/barcode/barcode";
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param barCode
     * @return
     */
    @RequestMapping(value = "/listData")
    public @ResponseBody
    List<BarCode> listData(@RequestBody BarCode barCode) {
        return barcodeService.query(barCode);
    }

    @RequestMapping(value = "/toExport")
    public ModelAndView toExport(BarCode barCode, Model model) {
        try {
            List<List<Object>> resultList = barcodeService.export(barCode);
            model.addAttribute("filename", "69码商品查询.xls");
            model.addAttribute("sheetname", "69码商品列表");
            model.addAttribute("contents", resultList);

            return new ModelAndView(new DefaultExcelView(), model.asMap());
        } catch (Exception e) {
            logger.error("barcode--toExport:" + e.getMessage(), e);
            return null;
        }
    }
}

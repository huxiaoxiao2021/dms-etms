package com.jd.bluedragon.distribution.goodsPrint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.goodsPrint.service.GoodsPrintService;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.uim.annotation.Authorization;
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
 * @Description: 托寄物品名打印
 * @date 2018年11月16日 13时:58分
 */
@Controller
@RequestMapping("goodsPrint")
public class GoodsPrintController extends DmsBaseController {
    private static final Log logger = LogFactory.getLog(GoodsPrintController.class);

    @Autowired
    GoodsPrintService goodsPrintService;

    /**
     * 返回主页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/goodsPrint/goodsPrintList";
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param goodsPrint
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody
    JdResponse<List<GoodsPrintDto>> listData(@RequestBody GoodsPrintDto goodsPrint) {
        try {
            return goodsPrintService.query(goodsPrint);
        }catch (Exception e){
            JdResponse jdResponse=new JdResponse();
            jdResponse.setCode(500);
            jdResponse.setMessage("调用服务失败");
            logger.error("goodsPrint.listData调用服务失败",e);
            return jdResponse;
        }
    }

    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/toExport")
    public ModelAndView toExport(GoodsPrintDto goodsPrint, Model model) {
        try {
            List<List<Object>> resultList = goodsPrintService.export(goodsPrint);
            model.addAttribute("filename", "托寄物品名.xls");
            model.addAttribute("sheetname", "托寄物品名");
            model.addAttribute("contents", resultList);

            return new ModelAndView(new DefaultExcelView(), model.asMap());
        } catch (Exception e) {
            logger.error("GoodsPrint--toExport:" + e.getMessage(), e);
            return null;
        }
    }
}

package com.jd.bluedragon.distribution.abnormal.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpCondition;
import com.jd.bluedragon.distribution.abnormal.service.DmsAbnormalEclpService;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWaybillDiff;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWaybillDiffService;
import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.funcSwitchConfig.FuncSwitchConfigController;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author wuyoude
 * @ClassName: DmsAbnormalEclpController
 * @Description: ECLP外呼申请表--Controller实现
 * @date 2018年03月14日 16:31:20
 */
@Controller
@RequestMapping("/abnormal/abnormalWaybillDiff")
public class AbnormalWaybillDiffController {

    private static final Logger logger = LoggerFactory.getLogger(AbnormalWaybillDiffController.class);


    @Autowired
    private AbnormalWaybillDiffService abnormalWaybillDiffService;


    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/abnormal/abnormalWaybillDiff";
    }

    @RequestMapping(value = "/add/{waybillCodeC}/{waybillCodeE}/{type}")
    public String add(@PathVariable("waybillCodeC") String waybillCodeC,
                      @PathVariable("waybillCodeE") String waybillCodeE,
                      @PathVariable("type") String type) {
        abnormalWaybillDiffService.save(waybillCodeC,waybillCodeE,type);
        return "success";
    }



    @RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse uploadExcel(@RequestParam("importExcelFile") MultipartFile file) {
        JdResponse response = new JdResponse();
        try {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xls"))  {
                response.toFail("文件格式不正确!");
                return response;
            }
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(ExcelDataResolverFactory.EXCEL_2003);
            List<AbnormalWaybillDiff> dataList = dataResolver.resolver(file.getInputStream(), AbnormalWaybillDiff.class,
                    new PropertiesMetaDataFactory("/excel/abnomalWaybillDiff.properties"));
            //批量插入数据
            abnormalWaybillDiffService.importExcel(dataList);
        } catch (Exception e) {
            logger.error("导入数据异常",e);
            response.toFail("导入数据异常!");
        }
        return response;
    }
}

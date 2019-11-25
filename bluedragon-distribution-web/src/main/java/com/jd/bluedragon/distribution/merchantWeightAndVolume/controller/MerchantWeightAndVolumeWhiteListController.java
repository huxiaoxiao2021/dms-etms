package com.jd.bluedragon.distribution.merchantWeightAndVolume.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeCondition;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.domain.MerchantWeightAndVolumeDetail;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.service.MerchantWeightAndVolumeWhiteListService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * 商家称重量方白名单
 *
 * @author: hujiping
 * @date: 2019/11/5 11:01
 */
@Controller
@RequestMapping("/merchantWeightAndVolume/whiteList")
public class MerchantWeightAndVolumeWhiteListController extends DmsBaseController {

    private static final Log logger = LogFactory.getLog(MerchantWeightAndVolumeWhiteListController.class);

    /**
     * 文件后缀名
     * */
    private static final String SUFFIX_NAME = "xls";

    @Autowired
    private MerchantWeightAndVolumeWhiteListService merchantWeightAndVolumeWhiteListService;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = new Integer(-1);
        Integer orgId = new Integer(-1);
        LoginUser loginUser = getLoginUser();
        if(loginUser != null && loginUser.getSiteType() == 64){
            createSiteCode = loginUser.getSiteCode();
            orgId = loginUser.getOrgId();
        }
        model.addAttribute("orgId",orgId);
        model.addAttribute("createSiteCode",createSiteCode);
        return "merchantWeightAndVolume/merchantWeightAndVolumeWhiteList";
    }

    /**
     * 查询
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<MerchantWeightAndVolumeDetail> listData(@RequestBody MerchantWeightAndVolumeCondition condition){
        return merchantWeightAndVolumeWhiteListService.queryByCondition(condition);
    }

    /**
     * 删除
     * @param detail
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> delete(@RequestBody MerchantWeightAndVolumeDetail detail){
        return merchantWeightAndVolumeWhiteListService.delete(detail);
    }

    /**
     * 导入
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping(value = "/toImport", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse toImport(@RequestParam("importExcelFile") MultipartFile file) {
        logger.debug("uploadExcelFile begin...");
        JdResponse response = new JdResponse();
        try {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith(SUFFIX_NAME)) {
                return new JdResponse(JdResponse.CODE_FAIL,"文件格式不对!");
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            String importErpCode = erpUser.getUserCode();

            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(1);
            List<MerchantWeightAndVolumeDetail> dataList
                    = dataResolver.resolver(file.getInputStream(), MerchantWeightAndVolumeDetail.class, new PropertiesMetaDataFactory("/excel/merchantWeightAndVolume.properties"));
            String errorMessage = merchantWeightAndVolumeWhiteListService.checkExportData(dataList,importErpCode);
            if (!StringUtils.isEmpty(errorMessage)) {
                return new JdResponse(JdResponse.CODE_FAIL, errorMessage);
            }
        } catch (Exception e) {
            this.logger.error("导入异常!",e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
        return response;
    }

    /**
     * 导出
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R)
    @RequestMapping(value = "/toExport", method = RequestMethod.POST)
    public ModelAndView toExport(MerchantWeightAndVolumeCondition condition, Model model) {

        this.logger.info("商家称重量方白名单统计表");
        List<List<Object>> resultList;
        try{
            model.addAttribute("filename", "商家称重量方白名单.xls");
            model.addAttribute("sheetname", "商家称重量方白名单结果");
            resultList = merchantWeightAndVolumeWhiteListService.getExportData(condition);
        }catch (Exception e){
            this.logger.error("导出商家称重量方白名单统计表失败:" + e.getMessage(), e);
            List<Object> list = new ArrayList<>();
            list.add("导出商家称重量方白名单统计表失败!");
            resultList = new ArrayList<>();
            resultList.add(list);
        }
        model.addAttribute("contents", resultList);
        return new ModelAndView(new DefaultExcelView(), model.asMap());
    }

}

package com.jd.bluedragon.distribution.web.kuaiyun.weight;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightImportResponse;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByPackageService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.common.web.LoginContext;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 运单称重
 * 包裹导入excel
 * @author zfy 2021-03
 */
@Controller
@RequestMapping("/b2b/express/weightpackage")
public class WeighByPackageController {
    private static final Logger log = LoggerFactory.getLogger(WeighByPackageController.class);

    private final Double MAX_WEIGHT = 999999.99;
    private final Double MAX_VOLUME = 999.99;

    /*10：表示经调取运单接口WaybillQueryApi，已查到该运单，可直接入库*/
    /*20：表示经调取运单接口WaybillQueryApi，未查到该运单，需经处理*/
    private final Integer VALID_EXISTS_STATUS_CODE = 10;
    private final Integer VALID_NOT_EXISTS_STATUS_CODE = 20;

    private final Integer NO_NEED_WEIGHT = 201;
    private final Integer WAYBILL_STATE_FINISHED = 202;
    private final Integer KAWAYBILL_NEEDPACKAGE_WEIGHT=203;

    private final Integer EXCESS_CODE = 600;
    private static final String PACKAGE_WEIGHT_VOLUME_EXCESS_HIT = "您的包裹超规，请确认。超过'200kg/包裹'或'1方/包裹'为超规件";
    private static final String WAYBILL_WEIGHT_VOLUME_EXCESS_HIT = "您的运单包裹超规，请确认。超过'包裹数*200kg/包裹'或'包裹数*1方/包裹'";

    @Autowired
    private WeighByPackageService service;

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping(value = "/uploadExcelByPackage", method = RequestMethod.POST)
    public @ResponseBody JdResponse uploadExcelByPackage(@RequestParam("importbyPackageExcelFile") MultipartFile file){
        log.debug("uploadExcelByPackage begin...");
        String errorString = "";
        try {
            //提前获取一次
            com.jd.bluedragon.distribution.kuaiyun.weight.service.impl.ErpUserClient.ErpUser erpUser = com.jd.bluedragon.distribution.kuaiyun.weight.service.impl.ErpUserClient.getCurrUser();
            BaseStaffSiteOrgDto bssod = null;
            String userCode = "";
            //todo 暂时注销erp
            /*if(erpUser!=null){
                userCode = erpUser.getUserCode();
                bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
                if(bssod == null){
                    service.errorLogForOperator(null, LoginContext.getLoginContext(),true);
                    if(service.isOpenIntercept()) {
                        return new JdResponse(JdResponse.CODE_FAIL,"未获取到操作人机构信息，请在青龙基础资料维护员工信息");
                    }
                }
            }else {
                service.errorLogForOperator(null, LoginContext.getLoginContext(),true);
                if(service.isOpenIntercept()) {
                    return new JdResponse(JdResponse.CODE_FAIL,"未获取到操作人信息，请在青龙基础资料维护员工信息");
                }
            }*/
            //解析excel
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(file.getOriginalFilename());
            List<WaybillWeightVO> dataList = null;
            List<String> resultMessages = new ArrayList<String>();
            List<WaybillWeightVO> successList = new ArrayList<WaybillWeightVO>();
            List<WaybillWeightVO> errorList = new ArrayList<WaybillWeightVO>();
            List<WaybillWeightVO> warnList = new ArrayList<>();
            WaybillWeightImportResponse waybillWeightImportResponse =  new WaybillWeightImportResponse();
            waybillWeightImportResponse.setErrorList(errorList);
            waybillWeightImportResponse.setSuccessList(successList);
            waybillWeightImportResponse.setWarnList(warnList);
            dataList = dataResolver.resolver(file.getInputStream(), WaybillWeightVO.class, new PropertiesMetaDataFactory("/excel/packageWeight.properties"),true,resultMessages);
            log.info("WeighByWaybillController-uploadExcelByPackage转成WaybillWeightVO-List参数:{}", JsonUtils.toJSONString(dataList));
            if (dataList != null && dataList.size() > 0) {
                if (dataList.size() > 1000) {
                    errorString = "导入数据超出1000条";
                    return new JdResponse(JdResponse.CODE_FAIL,errorString);
                }
                //取出 成功的数据 继续校验重泡比 成功直接保存 失败的数据返回给前台
                for(int i=0;i<resultMessages.size();i++){
                    //for(String resultMessage :resultMessages){
                    WaybillWeightVO waybillWeightVO = dataList.get(i);
                    String resultMessage = resultMessages.get(i);
                    if(resultMessage.equals(JdResponse.CODE_SUCCESS.toString())){
                        // 按照前台JS逻辑编写此校验逻辑
                        if(service.checkOfImportPackage(waybillWeightVO,erpUser,bssod)){
                            //校验通过
                            successList.add(waybillWeightVO);
                            //判断是否有提示信息
                            if(InvokeResult.RESULT_INTERCEPT_CODE.equals(waybillWeightVO.getErrorCode())) {
                                warnList.add(waybillWeightVO);
                            }
                        }else{
                            errorList.add(waybillWeightVO);
                        }

                    }else{
                        waybillWeightVO.setErrorMessage(resultMessage);
                        errorList.add(waybillWeightVO);
                    }
                    //dataList.remove(0);
                }

                //拼装返回数据
                waybillWeightImportResponse.setSuccessCount(successList.size());
                waybillWeightImportResponse.setErrorCount(errorList.size());
                waybillWeightImportResponse.setWarnCount(warnList.size());
                waybillWeightImportResponse.setCount(errorList.size()+successList.size());

                JdResponse response = new JdResponse();
                //有拦截提示的数据
                if(warnList.size() > 0){
                    response.setData(waybillWeightImportResponse);
                }

                //存在失败的数据
                if(errorList.size()>0){
                    int key = 0 ;
                    for(WaybillWeightVO errorVo :errorList){
                        errorVo.setKey(key++);
                    }
                    response.setCode(JdResponse.CODE_PARTIAL_SUCCESS);
                    response.setMessage(JdResponse.MESSAGE_PARTIAL_SUCCESS);
                    response.setData(waybillWeightImportResponse);
                }

                return response;

            } else {
                errorString = "导入数据表格为空，请检查excel数据";
                return new JdResponse(JdResponse.CODE_FAIL,errorString);
            }

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                errorString = e.getMessage();
            } else {
                log.error("导入异常信息：", e);
                errorString = "导入出现异常";
            }
            return new JdResponse(JdResponse.CODE_FAIL,errorString);
        }
    }

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping(value = "/toExportPackage")
    public ModelAndView toExportPackage(String json, Model model) {
        try {
            List list = JsonHelper.fromJson(json,new ArrayList().getClass());
            model.addAttribute("filename", "waybillWeight.xls");
            model.addAttribute("sheetname", "快运称重失败导出结果");
            model.addAttribute("contents", service.getExportDataPackage(list));
            return new ModelAndView(new DefaultExcelView(), model.asMap());

        } catch (Exception e) {
            log.error("toExport:", e);
            return null;
        }
    }


    /**
     * 录入运单称重量方数据
     *
     * @param vo WaybillWeightVO
     * @return InvokeResult<Boolean> 插入结果
     */
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/insertWaybillWeightPackage")
    @ResponseBody
    public InvokeResult<Boolean> insertWaybillWeightPackage(WaybillWeightVO vo) {

        return service.insertWaybillWeight(vo,null,null);
    }
    /**校验是否是包裹号 且包裹号是否存在运单中*/
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/verifyWaybillReality")
    @ResponseBody
    public InvokeResult<Boolean> verifyPackageReality(String codeStr) {
        return service.verifyPackageReality(codeStr);
    }




}

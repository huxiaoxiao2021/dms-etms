package com.jd.bluedragon.distribution.web.kuaiyun.weight;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightImportResponse;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByWaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.common.web.LoginContext;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 运单称重
 * B2B的称重量方简化功能，支持按运单/包裹号维度录入总重量（KG）和总体积（立方米）
 *
 * @author luyue  2017-12
 */
@Controller
@RequestMapping("/b2b/express/weight")
public class WeighByWaybillController {
    private static final Log logger = LogFactory.getLog(WeighByWaybillController.class);

    private final Double MAX_WEIGHT = 999999.99;
    private final Double MAX_VOLUME = 999.99;

    /*10：表示经调取运单接口WaybillQueryApi，已查到该运单，可直接入库*/
    /*20：表示经调取运单接口WaybillQueryApi，未查到该运单，需经处理*/
    private final Integer VALID_EXISTS_STATUS_CODE = 10;
    private final Integer VALID_NOT_EXISTS_STATUS_CODE = 20;

    private final Integer NO_NEED_WEIGHT = 201;
    private final Integer WAYBILL_STATE_FINISHED = 202;

    private final Integer EXCESS_CODE = 600;
    private static final String PACKAGE_WEIGHT_VOLUME_EXCESS_HIT = "您的包裹超规，请确认。超过'200kg/包裹'或'1方/包裹'为超规件";
    private static final String WAYBILL_WEIGHT_VOLUME_EXCESS_HIT = "您的运单包裹超规，请确认。超过'包裹数*200kg/包裹'或'包裹数*1方/包裹'";

    @Autowired
    WeighByWaybillService service;

    @Autowired
    BaseMajorManager baseMajorManager;

    @Autowired
    WaybillQueryManager waybillQueryManager;

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/index")
    public String getIndexPage() {
        return "/b2bExpress/weight/weighByWaybill";
    }

    /**
     * 录入运单称重量方数据
     *
     * @param vo WaybillWeightVO
     * @return InvokeResult<Boolean> 插入结果
     */
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/insertWaybillWeight")
    @ResponseBody
    @BusinessLog(sourceSys = 1,bizType = 1901,operateType = 1901001)
    public InvokeResult<Boolean> insertWaybillWeight(WaybillWeightVO vo) {

        return insertWaybillWeight(vo,null,null);
    }

    private InvokeResult<Boolean> insertWaybillWeight(WaybillWeightVO vo,ErpUserClient.ErpUser erpUser, BaseStaffSiteOrgDto baseStaffSiteOrgDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();

        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setData(true);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);

        /*参数校验*/
        boolean isValid = this.validateParam(vo);
        if (!isValid) {
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            result.setData(false);
            return result;
        }

        /*插入记录*/
        try {
            try {
                if(StringUtils.isBlank(vo.getOperatorName())){
                    //入参自带操作人时不需要查操作人信息
                    if(erpUser==null || baseStaffSiteOrgDto==null){
                        erpUser = ErpUserClient.getCurrUser();
                        if (erpUser != null) {
                            vo.setOperatorId(erpUser.getUserId());
                            vo.setOperatorName(erpUser.getUserName());
                            baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
                            if (baseStaffSiteOrgDto != null) {
                                vo.setOperatorSiteCode(baseStaffSiteOrgDto.getSiteCode());
                                vo.setOperatorSiteName(baseStaffSiteOrgDto.getSiteName());
                            }else {
                                logger.error("运单称重：未获取到当前操作人机构信息"+JsonHelper.toJson(erpUser));
                                service.errorLogForOperator(vo, LoginContext.getLoginContext(),false);
                                if(service.isOpenIntercept()){
                                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                                    result.setMessage("未获取到当前操作人机构信息，请在青龙基础资料维护员工信息");
                                    result.setData(false);
                                    return result;
                                }
                            }
                        }else {
                            logger.error("运单称重：未获取到当前操作人信息"+JsonHelper.toJson(erpUser));
                            service.errorLogForOperator(vo, LoginContext.getLoginContext(),false);
                            if(service.isOpenIntercept()) {
                                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                                result.setMessage("未获取到当前操作人信息，请在青龙基础资料维护员工信息");
                                result.setData(false);
                                return result;
                            }
                        }
                    }else if(erpUser!=null && baseStaffSiteOrgDto!=null){
                        //供批量导入使用
                        vo.setOperatorId(erpUser.getUserId());
                        vo.setOperatorName(erpUser.getUserName());
                        vo.setOperatorSiteCode(baseStaffSiteOrgDto.getSiteCode());
                        vo.setOperatorSiteName(baseStaffSiteOrgDto.getSiteName());
                    }
                }

            } catch (Exception e) {
                logger.error("运单称重：获取操作用户Erp账号失败"+JsonHelper.toJson(erpUser),e);
                service.errorLogForOperator(vo, LoginContext.getLoginContext(),false);
                if(service.isOpenIntercept()) {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    result.setMessage("获取操作用户信息异常");
                    result.setData(false);
                    return result;
                }
            }

            service.insertWaybillWeightEntry(vo);
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion) {
            WeightByWaybillExceptionTypeEnum exceptionType = weighByWaybillExcpetion.exceptionType;
            if (exceptionType.shouldBeThrowToTop) {
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(exceptionType.toString());
                result.setData(false);
            } else {
                if (weighByWaybillExcpetion.exceptionType.equals(WeightByWaybillExceptionTypeEnum.MQServiceNotAvailableException)) {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    result.setMessage("toTask");
                } else {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    result.setMessage(exceptionType.toString());
                    result.setData(false);
                }
            }
        }
        return result;
    }

    /**
     * 验证运单存在性
     *
     * 是否需要称重逻辑校验  2018 07 27  update 刘铎
     *
     * @param codeStr 运单号/运单下包裹号
     * @return 能否从运单系统查到对应运单
     * @throws WeighByWaybillExcpetion
     */
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/verifyWaybillReality")
    @ResponseBody
    public InvokeResult<Boolean> verifyWaybillReality(@RequestParam(value = "codeStr") String codeStr) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setData(true);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);

        try {
            /*1 将单号或包裹号正则校验 通过后 如果是包裹号需要转成运单号*/
            String waybillCode = service.convertToWaybillCode(codeStr);
            /*2 对运单进行存在校验*/
            boolean isExist = service.validateWaybillCodeReality(waybillCode);

            result.setData(isExist);
            if (isExist) {
                result.setMessage("存在该运单相关信息，可以录入！");
            }
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion) {
            result.setData(false);

            WeightByWaybillExceptionTypeEnum exceptionType = weighByWaybillExcpetion.exceptionType;
            if (exceptionType.shouldBeThrowToTop) {
                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableException)) {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    logger.error("运单称重：" + exceptionType.exceptionMessage);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillNoNeedWeightException)){
                    //不称重
                    result.setCode(NO_NEED_WEIGHT);
                    logger.debug("运单称重：" +codeStr+ "  " + exceptionType.exceptionMessage);
                }else if(exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillFinishedException)){
                    //运单已经妥投，不允许录入
                    result.setCode(WAYBILL_STATE_FINISHED);
                    logger.debug("运单称重:" + codeStr + " " +exceptionType.exceptionMessage);
                }
                result.setData(false);
                result.setMessage(exceptionType.exceptionMessage);
            } else {
                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.UnknownCodeException)) {
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }

                if (exceptionType.equals(WeightByWaybillExceptionTypeEnum.WaybillNotFindException)) {
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage(exceptionType.exceptionMessage);
                }

            }
        }
        return result;
    }

    /**
     * 将包裹号或运单号统一转为运单号
     *
     * @param codeStr 包裹号或/运单号
     * @return InvokeResult<String> 运单号
     */
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/convertCodeToWaybillCode")
    @ResponseBody
    public InvokeResult<String> convertCodeToWaybillCode(@RequestParam(value = "codeStr") String codeStr) {

        InvokeResult<String> result = new InvokeResult<String>();

        try {
            String waybillCode = service.convertToWaybillCode(codeStr);
            result.setData(waybillCode);
        } catch (WeighByWaybillExcpetion weighByWaybillExcpetion) {
            if (weighByWaybillExcpetion.exceptionType.equals(WeightByWaybillExceptionTypeEnum.UnknownCodeException)) {
                result.setData(null);
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage("运单号/包裹号错误，不符合运单号/包裹号格式!");
            }
        }
        return result;
    }

    /**
     * 校验传入称重量方参数
     *
     * @param vo 传入重量体积参数对象
     * @return boolean 校验结果
     */
    private boolean validateParam(WaybillWeightVO vo) {
        Integer status = vo.getStatus();
        Double weight = vo.getWeight();
        Double volume = vo.getVolume();

        if (!(status.equals(VALID_EXISTS_STATUS_CODE) || status.equals(VALID_NOT_EXISTS_STATUS_CODE))) {
            return false;
        }

        if (weight.compareTo(this.MAX_WEIGHT) != -1 || weight <= 0.0) {
            return false;
        }

        if (volume.compareTo(this.MAX_VOLUME) != -1 || volume <= 0.0) {
            return false;
        }

        return true;
    }

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
    public @ResponseBody JdResponse uploadExcel(@RequestParam("importExcelFile") MultipartFile file) {
        logger.debug("uploadExcelFile begin...");
        String errorString = "";
        try {
            //提前获取一次
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            BaseStaffSiteOrgDto bssod = null;
            String userCode = "";
            if(erpUser!=null){
                userCode = erpUser.getUserCode();
                bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
                if(bssod == null){
                    service.errorLogForOperator(null, LoginContext.getLoginContext(),true);
                    if(service.isOpenIntercept()) {
                        return new JdResponse(JdResponse.CODE_FAIL,"未获取的操作人机构信息，请在青龙基础资料维护员工信息");
                    }
                }
            }else {
                service.errorLogForOperator(null, LoginContext.getLoginContext(),true);
                if(service.isOpenIntercept()) {
                    return new JdResponse(JdResponse.CODE_FAIL,"未获取的操作人信息，请在青龙基础资料维护员工信息");
                }
            }
            //解析excel
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(file.getOriginalFilename());
            List<WaybillWeightVO> dataList = null;
            List<String> resultMessages = new ArrayList<String>();
            List<WaybillWeightVO> successList = new ArrayList<WaybillWeightVO>();
            List<WaybillWeightVO> errorList = new ArrayList<WaybillWeightVO>(); //校验失败的数据
            WaybillWeightImportResponse waybillWeightImportResponse =  new WaybillWeightImportResponse();
            waybillWeightImportResponse.setErrorList(errorList);
            waybillWeightImportResponse.setSuccessList(successList);
            dataList = dataResolver.resolver(file.getInputStream(), WaybillWeightVO.class, new PropertiesMetaDataFactory("/excel/waybillWeight.properties"),true,resultMessages);
            if (dataList != null && dataList.size() > 0) {
                if (dataList.size() > 1000) {
                    errorString = "导入数据超出1000条";
                    return new JdResponse(JdResponse.CODE_FAIL,errorString);
                }
                //取出 成功的数据 继续校验重泡比 成功直接保存 失败的数据返回给前台
                for(String resultMessage :resultMessages){
                    WaybillWeightVO waybillWeightVO = dataList.get(0);
                    if(resultMessage.equals(JdResponse.CODE_SUCCESS.toString())){
                        // 按照前台JS逻辑编写此校验逻辑
                        if(checkOfImport(waybillWeightVO,erpUser,bssod)){
                            //校验通过
                            successList.add(waybillWeightVO);
                        }else{
                            errorList.add(waybillWeightVO);
                        }

                    }else{
                        waybillWeightVO.setErrorMessage(resultMessage);
                        errorList.add(waybillWeightVO);
                    }
                    dataList.remove(0);
                }

                //拼装返回数据
                waybillWeightImportResponse.setSuccessCount(successList.size());
                waybillWeightImportResponse.setErrorCount(errorList.size());
                waybillWeightImportResponse.setCount(errorList.size()+successList.size());

                //存在失败的数据
                if(errorList.size()>0){
                    int key = 0 ;
                    for(WaybillWeightVO errorVo :errorList){
                        errorVo.setKey(key++);
                    }
                    return new JdResponse(JdResponse.CODE_PARTIAL_SUCCESS,JdResponse.MESSAGE_PARTIAL_SUCCESS,waybillWeightImportResponse);
                }

            } else {
                errorString = "导入数据表格为空，请检查excel数据";
                return new JdResponse(JdResponse.CODE_FAIL,errorString);
            }

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                errorString = e.getMessage();
            } else {
                logger.error("导入异常信息：", e);
                errorString = "导入出现异常";
            }
            return new JdResponse(JdResponse.CODE_FAIL,errorString);
        }

        return new JdResponse();
    }



    private boolean checkOfImport(WaybillWeightVO waybillWeightVO,ErpUserClient.ErpUser erpUser, BaseStaffSiteOrgDto baseStaffSiteOrgDto){

        waybillWeightVO.setCanSubmit(0); //默认设置不可进行强制提交
        waybillWeightVO.setStatus(VALID_EXISTS_STATUS_CODE); //默认设置存在
        waybillWeightVO.setErrorCode(0);
        //必输项检查 这校验全在JS。。后台还得在来一遍
        String codeStr = waybillWeightVO.getCodeStr();
        Double weight = waybillWeightVO.getWeight();
        Double volume = waybillWeightVO.getVolume();
        if(StringUtils.isBlank(codeStr)){
            waybillWeightVO.setErrorMessage("运单号/包裹号必输");
            return false;
        }else if(weight == null || weight.equals(new Double(0))){
            waybillWeightVO.setErrorMessage("重量必输，并且大于0");
            return false;
        }else if(volume == null || volume.equals(new Double(0))){
            waybillWeightVO.setErrorMessage("体积必输，并且大于0");
            return false;
        }

        //转换
        InvokeResult<String> convertCodeToWaybillCodeResult =  convertCodeToWaybillCode(waybillWeightVO.getCodeStr());
        if(InvokeResult.RESULT_SUCCESS_CODE != convertCodeToWaybillCodeResult.getCode()){
            waybillWeightVO.setErrorMessage(convertCodeToWaybillCodeResult.getMessage());
            return false;
        }else{
            //转换成功 将运单号存入对象中
            waybillWeightVO.setCodeStr(convertCodeToWaybillCodeResult.getData());
        }

        //存在性校验
        InvokeResult<Boolean> verifyWaybillRealityResult = verifyWaybillReality(waybillWeightVO.getCodeStr());
        if(InvokeResult.RESULT_NULL_CODE == verifyWaybillRealityResult.getCode()){
            //不存在
            waybillWeightVO.setErrorMessage(verifyWaybillRealityResult.getMessage());
            waybillWeightVO.setStatus(VALID_NOT_EXISTS_STATUS_CODE);
            //可让前台强制提交
            waybillWeightVO.setCanSubmit(1);
            return false;
        }else if(InvokeResult.RESULT_SUCCESS_CODE != verifyWaybillRealityResult.getCode()){
            //失败
            waybillWeightVO.setErrorMessage(verifyWaybillRealityResult.getMessage());
            return false;
        }else{

            boolean isValid = this.validateParam(waybillWeightVO);
            if (!isValid) {
                //没通过
                waybillWeightVO.setErrorMessage(InvokeResult.PARAM_ERROR);
                waybillWeightVO.setErrorCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                return false;
            }

            //校验重泡比
            if(!BusinessHelper.checkWaybillWeightAndVolume(waybillWeightVO.getWeight(),waybillWeightVO.getVolume())){
                //没通过
                waybillWeightVO.setErrorMessage(Constants.CBM_DIV_KG_MESSAGE);
                waybillWeightVO.setErrorCode(Constants.CBM_DIV_KG_CODE);
                //可让前台强制提交
                waybillWeightVO.setCanSubmit(1);
                return false;
            }

            //存在
            //校验成功 执行插入
            InvokeResult<Boolean> insertWaybillWeightResult = insertWaybillWeight(waybillWeightVO,erpUser,baseStaffSiteOrgDto);
            if(InvokeResult.RESULT_SUCCESS_CODE != insertWaybillWeightResult.getCode()){
                //插入失败
                waybillWeightVO.setErrorMessage(insertWaybillWeightResult.getMessage());
                return false;
            }

        }

        return true;
    }

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping(value = "/toExport")
    public ModelAndView toExport(String json, Model model) {
        try {
            List list = JsonHelper.fromJson(json,new ArrayList().getClass());
            model.addAttribute("filename", "waybillWeight.xls");
            model.addAttribute("sheetname", "快运称重失败导出结果");
            model.addAttribute("contents", getExportData(list));

            return new ModelAndView(new DefaultExcelView(), model.asMap());

        } catch (Exception e) {
            logger.error("toExport:" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * 组装导出数据
     * @param list
     * @return
     */
    private List<List<Object>> getExportData(List<Map> list) {

        List<List<Object>> resList = new ArrayList<List<Object>>();

        List<Object> heads = new ArrayList<Object>();

        heads.add("运单号");
        heads.add("重量");
        heads.add("体积");

        resList.add(heads);

        for(Map waybillWeightVO :list){
            List<Object> body = new ArrayList<Object>();
            //运单号
            body.add(waybillWeightVO.get("codeStr"));
            //重量
            body.add(waybillWeightVO.get("weight"));
            //体积
            body.add(waybillWeightVO.get("volume"));
            resList.add(body);
        }

        return resList;
    }

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @ResponseBody
    @RequestMapping("/checkIsExcess")
    public InvokeResult checkIsExcess(@QueryParam("codeStr") String codeStr,
                                               @QueryParam("weight") String weight,@QueryParam("volume") String volume){
        InvokeResult result = new InvokeResult();

        if(StringUtils.isEmpty(codeStr) || StringUtils.isEmpty(weight)
                || StringUtils.isEmpty(volume)){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        try{
            if(WaybillUtil.isWaybillCode(codeStr)){
                int packNum = 0;
                BaseEntity<BigWaybillDto> entity = waybillQueryManager.getDataByChoice(codeStr, true, true, true, false);
                if(entity!= null && entity.getData() != null && entity.getData().getWaybill() != null){
                    packNum = entity.getData().getWaybill().getGoodNumber() == null?0:entity.getData().getWaybill().getGoodNumber();
                    if(Double.parseDouble(weight) > 200*packNum || Double.parseDouble(volume) > packNum){
                        result.setCode(EXCESS_CODE);
                        result.setMessage(WAYBILL_WEIGHT_VOLUME_EXCESS_HIT);
                    }
                }else{
                    result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                    result.setMessage("运单信息为空!");
                }
            }else{
                if(Double.parseDouble(weight) > 200 || Double.parseDouble(volume) > 1){
                    result.setCode(EXCESS_CODE);
                    result.setMessage(PACKAGE_WEIGHT_VOLUME_EXCESS_HIT);
                }
            }
        }catch (Exception e){
            this.logger.error("通过运单号:"+codeStr+"获取运单信息失败!");
        }

        return result;
    }



}

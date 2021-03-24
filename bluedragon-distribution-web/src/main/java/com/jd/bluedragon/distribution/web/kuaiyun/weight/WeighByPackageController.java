package com.jd.bluedragon.distribution.web.kuaiyun.weight;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightImportResponse;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByPackageService;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByWaybillService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.common.web.LoginContext;
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
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private WeighByWaybillService weighByWaybillService;
    @Autowired
    private SysConfigService sysConfigService;

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping(value = "/uploadExcelByPackage", method = RequestMethod.POST)
    public @ResponseBody JdResponse uploadExcelByPackage(@RequestParam("importbyPackageExcelFile") MultipartFile file){
        log.debug("uploadExcelByPackage begin...");
        String errorString = "";
        try {
            //提前获取一次
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            BaseStaffSiteOrgDto bssod = null;
            //todo 临时设置
            /*ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
            erpUser.setUserCode("wuyoude");
            erpUser.setUserName("吴有德");*/

            //todo 临时值
            String userCode = "";
            //todo 暂时注销erp
            if(erpUser!=null){
                userCode = erpUser.getUserCode();
                bssod = baseMajorManager.getBaseStaffByErpNoCache(userCode);
                if(bssod == null){
                    service.errorLogForOperator(null, LoginContext.getLoginContext(),true);
                    if(isOpenIntercept()) {
                        return new JdResponse(JdResponse.CODE_FAIL,"未获取到操作人机构信息，请在青龙基础资料维护员工信息");
                    }
                }
            }else {
                service.errorLogForOperator(null, LoginContext.getLoginContext(),true);
                if(isOpenIntercept()) {
                    return new JdResponse(JdResponse.CODE_FAIL,"未获取到操作人信息，请在青龙基础资料维护员工信息");
                }
            }
            //将excel上传至jss
            service.uploadExcelToJss(file,userCode);
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
                    WaybillWeightVO waybillWeightVO = dataList.get(i);
                    String resultMessage = resultMessages.get(i);
                    if(resultMessage.equals(JdResponse.CODE_SUCCESS.toString())){
                        // 按照前台JS逻辑编写此校验逻辑
                        if(checkOfImportPackage(waybillWeightVO,erpUser,bssod)){
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

        return insertWaybillWeight(vo,null,null);
    }
    /**校验是否是包裹号 且包裹号是否存在运单中*/
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/verifyWaybillReality")
    @ResponseBody
    public InvokeResult<Boolean> verifyPackageReality(String codeStr) {
        return service.verifyPackageReality(codeStr);
    }



    private boolean checkOfImportPackage(WaybillWeightVO waybillWeightVO,ErpUserClient.ErpUser erpUser, BaseStaffSiteOrgDto baseStaffSiteOrgDto)
    {
        //默认设置不可进行强制提交
        waybillWeightVO.setCanSubmit(0);
        //默认设置存在
        waybillWeightVO.setStatus(VALID_EXISTS_STATUS_CODE);
        waybillWeightVO.setErrorCode(0);
        //必输项检查 这校验全在JS。。后台还得在来一遍
        String codeStr = waybillWeightVO.getCodeStr();
        Double weight = waybillWeightVO.getWeight();
        //Double volume = waybillWeightVO.getVolume();
        //长
        Double length = waybillWeightVO.getPackageLength();
        //宽
        Double width = waybillWeightVO.getPackageWidth();
        //高
        Double high = waybillWeightVO.getPackageHigh();
        if(StringUtils.isBlank(codeStr)){
            waybillWeightVO.setErrorMessage("包裹号必输");
            return false;
        }else if(weight == null || weight.equals(new Double(0))){
            waybillWeightVO.setErrorMessage("重量必输,并且大于0");
            return false;
        }else if(length == null || length.equals(new Double(0))){
            waybillWeightVO.setErrorMessage("长必输,并且大于0");
            return false;
        }else if(width == null || width.equals(new Double(0))){
            waybillWeightVO.setErrorMessage("宽必输,并且大于0");
            return false;
        }else if(high == null || high.equals(new Double(0))){
            waybillWeightVO.setErrorMessage("高必输,并且大于0");
            return false;
        }
        //设置体积
        Double volume = MathUtils.mul(length,width,high,6);
        waybillWeightVO.setVolume(volume);
        //存在性校验
        InvokeResult<Boolean> verifyWaybillRealityResult = service.verifyPackageReality(waybillWeightVO.getCodeStr());
        if(InvokeResult.RESULT_NULL_CODE == verifyWaybillRealityResult.getCode()){
            //不存在
            waybillWeightVO.setErrorMessage(verifyWaybillRealityResult.getMessage());
            waybillWeightVO.setStatus(VALID_NOT_EXISTS_STATUS_CODE);
            //可让前台强制提交
            waybillWeightVO.setCanSubmit(0);
            return false;
        }else if(InvokeResult.RESULT_SUCCESS_CODE != verifyWaybillRealityResult.getCode()){
            //失败
            waybillWeightVO.setErrorMessage(verifyWaybillRealityResult.getMessage());
            return false;
        }else{

            boolean isValid = service.validateParam(waybillWeightVO);
            if (!isValid) {
                //没通过
                waybillWeightVO.setErrorMessage(InvokeResult.PARAM_ERROR);
                waybillWeightVO.setErrorCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                return false;
            }

            //校验重量体积是否超标
            InvokeResult invokeResult = service.checkIsExcess(codeStr, weight.toString(), volume.toString());
            if(invokeResult != null && invokeResult.getCode() == EXCESS_CODE){
                //没通过
                waybillWeightVO.setErrorMessage(invokeResult.getMessage());
                waybillWeightVO.setErrorCode(invokeResult.getCode());
                //可让前台强制提交
                waybillWeightVO.setCanSubmit(1);
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
            InvokeResult<Boolean> insertWaybillWeightResult = this.insertWaybillWeight(waybillWeightVO,erpUser,baseStaffSiteOrgDto);
            if(InvokeResult.RESULT_INTERCEPT_CODE == insertWaybillWeightResult.getCode()){
                waybillWeightVO.setErrorMessage(insertWaybillWeightResult.getMessage());
                waybillWeightVO.setErrorCode(InvokeResult.RESULT_INTERCEPT_CODE);
                return true;
            } else if(InvokeResult.RESULT_SUCCESS_CODE != insertWaybillWeightResult.getCode()){
                //插入失败
                waybillWeightVO.setErrorMessage(insertWaybillWeightResult.getMessage());
                return false;
            }
        }

        return true;
    }

    private InvokeResult<Boolean> insertWaybillWeight(WaybillWeightVO vo, ErpUserClient.ErpUser erpUser, BaseStaffSiteOrgDto baseStaffSiteOrgDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();

        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setData(true);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);

        /*参数校验*/
        boolean isValid = service.validateParam(vo);
        if (!isValid) {
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            result.setData(false);
            return result;
        }
        //todo 临时测试数据 删掉
        /*erpUser = new ErpUserClient.ErpUser();
        erpUser.setUserCode("wuyoude");
        erpUser.setUserName("吴有德");

        baseStaffSiteOrgDto = new BaseStaffSiteOrgDto();
        baseStaffSiteOrgDto.setSiteCode(910);
        baseStaffSiteOrgDto.setSiteName("北京马驹桥分拣中心");
        baseStaffSiteOrgDto.setStaffName("吴有德");
        baseStaffSiteOrgDto.setStaffNo(17331);*/
        /*插入记录*/
        try {
            try {
                if(StringUtils.isBlank(vo.getOperatorName())){
                    //入参自带操作人时不需要查操作人信息
                    if(erpUser==null || baseStaffSiteOrgDto==null){
                        //todo 为了测试临时删除
                        erpUser = ErpUserClient.getCurrUser();
                        if (erpUser != null) {
                            vo.setOperatorId(erpUser.getStaffNo());
                            vo.setOperatorName(erpUser.getUserName());
                            baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
                            if (baseStaffSiteOrgDto != null) {
                                vo.setOperatorSiteCode(baseStaffSiteOrgDto.getSiteCode());
                                vo.setOperatorSiteName(baseStaffSiteOrgDto.getSiteName());
                            }else {
                                log.warn("运单称重：未获取到当前操作人机构信息：{}", JsonHelper.toJson(erpUser));
                                service.errorLogForOperator(vo, LoginContext.getLoginContext(),false);
                                if(isOpenIntercept()){
                                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                                    result.setMessage("未获取到当前操作人机构信息，请在青龙基础资料维护员工信息");
                                    result.setData(false);
                                    return result;
                                }
                            }
                        }else {
                            log.warn("运单称重：未获取到当前操作人信息:{}", JsonHelper.toJson(erpUser));
                            service.errorLogForOperator(vo, LoginContext.getLoginContext(),false);
                            if(isOpenIntercept()) {
                                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                                result.setMessage("未获取到当前操作人信息，请在青龙基础资料维护员工信息");
                                result.setData(false);
                                return result;
                            }
                        }
                    }else if(erpUser!=null && baseStaffSiteOrgDto!=null){
                        //供批量导入使用
                        vo.setOperatorId(baseStaffSiteOrgDto.getStaffNo());
                        vo.setOperatorName(baseStaffSiteOrgDto.getStaffName());
                        vo.setOperatorSiteCode(baseStaffSiteOrgDto.getSiteCode());
                        vo.setOperatorSiteName(baseStaffSiteOrgDto.getSiteName());
                    }
                }

            } catch (Exception e) {
                log.error("运单称重：获取操作用户Erp账号失败:{}", JsonHelper.toJson(erpUser),e);
                service.errorLogForOperator(vo, LoginContext.getLoginContext(),false);
                if(this.isOpenIntercept()) {
                    result.setCode(InvokeResult.SERVER_ERROR_CODE);
                    result.setMessage("获取操作用户信息异常");
                    result.setData(false);
                    return result;
                }
            }
            //且调用第三方接口 需要修改
            service.insertPackageWeightEntry(vo);
            //todo 判断是否转网因为全是B网 用redis缓存存储已经判断过的运单号
            if(weighByWaybillService.waybillTransferB2C(vo)){
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(MessageFormat.format(InvokeResult.RESULT_INTERCEPT_MESSAGE, WaybillUtil.getWaybillCode(vo.getCodeStr())));
                result.setData(true);
            }
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
        } catch (Exception e){
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            result.setData(false);
        }
        return result;
    }

    /**
     * b2b.weight.user.switch
     * 等于1 开启校验
     * 不维护 或者 等于0 不校验
     * @return
     */
    private boolean isOpenIntercept(){

        return sysConfigService.getConfigByName("b2b.weight.user.switch");

    }
}

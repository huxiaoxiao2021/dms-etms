package com.jd.bluedragon.distribution.web.kuaiyun.weight;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.PackageWeightImportResponse;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.PackageWeightVO;
import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.exception.WeighByWaybillExcpetion;
import com.jd.bluedragon.distribution.kuaiyun.weight.service.WeighByPackageService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.view.DefaultExcelView;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.handler.WeightVolumeHandlerStrategy;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.common.web.LoginContext;
import com.jd.jim.cli.Cluster;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运单称重
 * 包裹导入excel
 * @author zfy 2021-03
 */
@Controller
@RequestMapping("/b2b/express/weightpackage")
public class WeighByPackageController extends DmsBaseController {
    private static final Logger log = LoggerFactory.getLogger(WeighByPackageController.class);

    /*10：表示经调取运单接口WaybillQueryApi，已查到该运单，可直接入库*/
    /*20：表示经调取运单接口WaybillQueryApi，未查到该运单，需经处理*/
    private final Integer VALID_EXISTS_STATUS_CODE = 10;
    private final Integer VALID_NOT_EXISTS_STATUS_CODE = 20;

    private final Integer EXCESS_CODE = 600;
    private static final String UPLOADKEY = "uploadExcelByPackageKey";
    @Autowired
    private WeighByPackageService service;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping(value = "/uploadExcelByPackage", method = RequestMethod.POST)
    public @ResponseBody JdResponse uploadExcelByPackage(@RequestParam("importbyPackageExcelFile") MultipartFile file){
        log.debug("uploadExcelByPackage begin...");
        CallerInfo callerInfo = ProfilerHelper.registerInfo(  "DMS.BASE.WeighByPackageController.uploadExcelByPackage");
        String errorString = "";
        try {
            if(redisClientCache.incr(UPLOADKEY)>20){
                log.info("当前操作人较多;稍后重试,uploadExcelByPackageKey值为:{}",redisClientCache.get(UPLOADKEY));
                return new JdResponse(JdResponse.CODE_FAIL,"当前操作人较多;请稍后重试!");
            }
            //提前获取一次
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            BaseStaffSiteOrgDto bssod = null;
            String userCode = "";

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
            //解析excel
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(file.getOriginalFilename());
            List<PackageWeightVO> dataList = null;
            List<String> resultMessages = new ArrayList<String>();
            List<PackageWeightVO> successList = new ArrayList<PackageWeightVO>();
            List<PackageWeightVO> errorList = new ArrayList<PackageWeightVO>();
            List<PackageWeightVO> warnList = new ArrayList<>();
            PackageWeightImportResponse packageWeightImportResponse =  new PackageWeightImportResponse();
            packageWeightImportResponse.setErrorList(errorList);
            packageWeightImportResponse.setSuccessList(successList);
            packageWeightImportResponse.setWarnList(warnList);
            dataList = dataResolver.resolver(file.getInputStream(), PackageWeightVO.class, new PropertiesMetaDataFactory("/excel/packageWeight.properties"),true,resultMessages);
            log.info("WeighByWaybillController-uploadExcelByPackage转成WaybillWeightVO-List参数:{}", JsonUtils.toJSONString(dataList));
            if (dataList != null && dataList.size() > 0) {
                //取出 成功的数据 继续校验重泡比 成功直接保存 失败的数据返回给前台
                Map<String,Integer> maps = new HashMap<>();
                for(int i=0;i<resultMessages.size();i++){
                    PackageWeightVO packageWeightVO = dataList.get(i);
                    String resultMessage = resultMessages.get(i);
                    if(resultMessage.equals(JdResponse.CODE_SUCCESS.toString())){
                        // 按照前台JS逻辑编写此校验逻辑
                        if(checkOfImportPackage(packageWeightVO,erpUser,bssod,maps)){
                            //校验通过
                            successList.add(packageWeightVO);
                            //判断是否有提示信息
                            if(InvokeResult.RESULT_INTERCEPT_CODE.equals(packageWeightVO.getErrorCode())) {
                                warnList.add(packageWeightVO);
                            }
                        }else{
                            errorList.add(packageWeightVO);
                        }

                    }else{
                        packageWeightVO.setErrorMessage(resultMessage);
                        errorList.add(packageWeightVO);
                    }
                }
                //拼装返回数据
                packageWeightImportResponse.setSuccessCount(successList.size());
                packageWeightImportResponse.setErrorCount(errorList.size());
                packageWeightImportResponse.setWarnCount(warnList.size());
                packageWeightImportResponse.setCount(errorList.size()+successList.size());

                JdResponse response = new JdResponse();
                //有拦截提示的数据
                if(warnList.size() > 0){
                    response.setData(packageWeightImportResponse);
                }

                //存在失败的数据
                if(errorList.size()>0){
                    int key = 0 ;
                    for(PackageWeightVO errorVo :errorList){
                        errorVo.setKey(key++);
                    }
                    response.setCode(JdResponse.CODE_PARTIAL_SUCCESS);
                    response.setMessage(JdResponse.MESSAGE_PARTIAL_SUCCESS);
                    response.setData(packageWeightImportResponse);
                }

                return response;

            } else {
                errorString = "导入数据表格为空或数据量超过1000条;请检查excel数据";
                return new JdResponse(JdResponse.CODE_FAIL,errorString);
            }

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                errorString = e.getMessage();
            } else {
                log.error("导入异常信息：", e);
                errorString = "导入出现异常";
            }
            Profiler.functionError(callerInfo);
            return new JdResponse(JdResponse.CODE_FAIL,errorString);
        }finally {
            redisClientCache.decr(UPLOADKEY);
        }
    }

    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping(value = "/toExportPackage")
    public ModelAndView toExportPackage(String json, Model model) {
        try {
            List list = JsonHelper.fromJson(json,new ArrayList().getClass());
            model.addAttribute("filename", "packageWeight.xls");
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
    public InvokeResult<Boolean> insertWaybillWeightPackage(PackageWeightVO vo) {

        return insertWaybillWeight(vo,null,null);
    }
    /**校验是否是包裹号 且包裹号是否存在运单中*/
    @Authorization(Constants.DMS_WEB_TOOL_B2BWEIGHT_R)
    @RequestMapping("/verifyWaybillReality")
    @ResponseBody
    public InvokeResult<Boolean> verifyPackageReality(String codeStr) {
        Map<String,Integer> maps = new HashMap<>();
        return service.verifyPackageReality(codeStr, maps, getLoginUser().getSiteCode());
    }



    private boolean checkOfImportPackage(PackageWeightVO vo, ErpUserClient.ErpUser erpUser, BaseStaffSiteOrgDto baseStaffSiteOrgDto, Map<String, Integer> map)
    {
        //默认设置不可进行强制提交
        vo.setCanSubmit(0);
        //默认设置存在
        vo.setStatus(VALID_EXISTS_STATUS_CODE);
        vo.setErrorCode(0);
        //必输项检查 这校验全在JS。。后台还得在来一遍
        String codeStr = vo.getCodeStr();
        Double weight = vo.getWeight();
        //Double volume = waybillWeightVO.getVolume();
        //长
        Double length = vo.getPackageLength();
        //宽
        Double width = vo.getPackageWidth();
        //高
        Double high = vo.getPackageHigh();
        if(StringUtils.isBlank(codeStr)){
            vo.setErrorMessage("包裹号必输");
            return false;
        }else if(weight == null || 0>weight){
            vo.setErrorMessage("重量必输,并且大于0");
            return false;
        }else if(length == null || 0>length){
            vo.setErrorMessage("长必输,并且大于0");
            return false;
        }else if(width == null || 0>width ){
            vo.setErrorMessage("宽必输,并且大于0");
            return false;
        }else if(high == null || 0>high){
            vo.setErrorMessage("高必输,并且大于0");
            return false;
        }
        //设置体积
        Double volume = MathUtils.mul(length,width,high,6);
        vo.setVolume(volume);
        //存在性校验
        InvokeResult<Boolean> verifyWaybillRealityResult = service.verifyPackageReality(vo.getCodeStr(), map, getLoginUser().getSiteCode());
        if(InvokeResult.RESULT_NULL_CODE == verifyWaybillRealityResult.getCode()){
            //不存在
            vo.setErrorMessage(verifyWaybillRealityResult.getMessage());
            vo.setStatus(VALID_NOT_EXISTS_STATUS_CODE);
            //不可让前台强制提交
            vo.setCanSubmit(0);
            return false;
        }else if(InvokeResult.RESULT_SUCCESS_CODE != verifyWaybillRealityResult.getCode()){
            //失败
            vo.setErrorMessage(verifyWaybillRealityResult.getMessage());
            return false;
        }else{

            boolean isValid = service.validateParam(vo);
            if (!isValid) {
                //没通过
                vo.setErrorMessage(InvokeResult.PARAM_ERROR);
                vo.setErrorCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                return false;
            }

            //校验重量体积是否超标
            if (uccPropertyConfiguration.getWeightVolumeSwitchVersion() == 0) {
                InvokeResult invokeResult = service.checkIsExcess(codeStr, weight.toString(), volume.toString());
                if(invokeResult != null && invokeResult.getCode() == EXCESS_CODE){
                    //没通过
                    vo.setErrorMessage(invokeResult.getMessage());
                    vo.setErrorCode(invokeResult.getCode());
                    //可让前台强制提交
                    vo.setCanSubmit(1);
                    return false;
                }

                //校验重泡比
                if(!BusinessHelper.checkWaybillWeightAndVolume(vo.getWeight(),vo.getVolume())){
                    //没通过
                    vo.setErrorMessage(Constants.CBM_DIV_KG_MESSAGE);
                    vo.setErrorCode(Constants.CBM_DIV_KG_CODE);
                    //可让前台强制提交
                    vo.setCanSubmit(1);
                    return false;
                }
            } else if (uccPropertyConfiguration.getWeightVolumeSwitchVersion() == 1) {
                WeightVolumeRuleCheckDto condition = new WeightVolumeRuleCheckDto();
                condition.setBarCode(codeStr);
                condition.setVolume(volume);
                condition.setCheckVolume(Boolean.TRUE);
                condition.setWeight(weight);
                condition.setCheckWeight(Boolean.TRUE);
                condition.setCheckLWH(Boolean.FALSE);
                condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE.name());
                InvokeResult result = weightVolumeHandlerStrategy.weightVolumeRuleCheck(condition);
                if (InvokeResult.CODE_CONFIRM.equals(result.getCode())) {
                    //没通过
                    vo.setErrorMessage(result.getMessage());
                    vo.setErrorCode(EXCESS_CODE);
                    //可让前台强制提交
                    vo.setCanSubmit(1);
                    return false;
                } else if (InvokeResult.RESULT_PARAMETER_ERROR_CODE == result.getCode()) {
                    vo.setErrorMessage(result.getMessage());
                    vo.setErrorCode(InvokeResult.RESULT_INTERCEPT_CODE);
                    //可让前台强制提交
                    vo.setCanSubmit(0);
                    return false;
                }

            }

            //存在
            //校验成功 执行插入
            InvokeResult<Boolean> insertWaybillWeightResult = this.insertWaybillWeight(vo,erpUser,baseStaffSiteOrgDto);
            if(InvokeResult.RESULT_INTERCEPT_CODE == insertWaybillWeightResult.getCode()){
                vo.setErrorMessage(insertWaybillWeightResult.getMessage());
                vo.setErrorCode(InvokeResult.RESULT_INTERCEPT_CODE);
                return true;
            } else if(InvokeResult.RESULT_SUCCESS_CODE != insertWaybillWeightResult.getCode()){
                //插入失败
                vo.setErrorMessage(insertWaybillWeightResult.getMessage());
                return false;
            }
        }

        return true;
    }

    private InvokeResult<Boolean> insertWaybillWeight(PackageWeightVO vo, ErpUserClient.ErpUser erpUser, BaseStaffSiteOrgDto baseStaffSiteOrgDto) {
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

        /*插入记录*/
        try {
            try {
                if(StringUtils.isBlank(vo.getOperatorName())){
                    //入参自带操作人时不需要查操作人信息
                    if(erpUser==null || baseStaffSiteOrgDto==null){
                        erpUser = ErpUserClient.getCurrUser();
                        if (erpUser != null) {
                            vo.setOperatorId(erpUser.getStaffNo());
                            vo.setOperatorName(erpUser.getUserName());
                            vo.setOperatorCode(erpUser.getUserCode());
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
                        vo.setOperatorCode(baseStaffSiteOrgDto.getUserCode());
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

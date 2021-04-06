package com.jd.bluedragon.distribution.wastePackage.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.request.WastePackageRequest;
import com.jd.bluedragon.distribution.arAbnormal.ArAbnormalService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedPackageStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.wastePackage.service.WastePackageService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_PARAMETER_ERROR_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;

/**
 * 弃件暂存
 * @author biyuo
 * @date 2021/3/23
 */
@Service("wastePackageService")
public class WastePackageServiceImpl implements WastePackageService {
    private final Logger log = LoggerFactory.getLogger(WastePackageServiceImpl.class);

    @Qualifier("waybillPackageManager")
    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private ArAbnormalService arAbnormalService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private DiscardedPackageStorageTempDao discardedPackageStorageTempDao;

    /**
     * 弃件暂存上报
     * @param request
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.com.WastePackageServiceImpl.wastepackagestorage", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> wastepackagestorage(WastePackageRequest request) {
        InvokeResult<Boolean> result = checkParam(request);
        if(RESULT_SUCCESS_CODE != result.getCode()){
            return result;
        }

        try {
            if (!waybillTraceManager.isWaybillWaste(request.getWaybillCode())){
                result.error("不是弃件，请勿操作弃件暂存");
                return result;
            }

            BaseStaffSiteOrgDto siteDto = baseMajorManager.getBaseSiteBySiteId(request.getSiteCode());
            if(siteDto==null){
                result.error("没有查询到操作站点信息");
                return result;
            }

            WChoice choice = new WChoice();
            choice.setQueryWaybillC(true);
            choice.setQueryPackList(true);
            choice.setQueryGoodList(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(request.getWaybillCode(), choice);
            if (baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode() && baseEntity.getData() != null) {
                result.error("查询到运单信息失败:"+baseEntity.getMessage());
                return result;
            }

            if(baseEntity.getData() == null || baseEntity.getData().getWaybill()==null){
                result.error("没有查询到运单信息");
                return result;
            }

            List<DiscardedPackageStorageTemp> dbList=getDBList(baseEntity.getData(),siteDto,request);
            if(dbList==null){
                result.error("没有查询到运单包裹信息");
                return result;
            }

            DiscardedPackageStorageTempQo dbQuery=new DiscardedPackageStorageTempQo();
            dbQuery.setWaybillCode(request.getWaybillCode());
            dbQuery.setYn(Constants.YN_YES);
            long total = discardedPackageStorageTempDao.selectCount(dbQuery);
            int dbRes=-1;
            if(total>0){
                dbRes=discardedPackageStorageTempDao.batchUpdate(dbList);
            }else {
                dbRes=discardedPackageStorageTempDao.batchInsert(dbList);
            }

            if(dbRes<0){
                result.error("弃件暂存失败，存储数据出现错误");
                return result;
            }

            log.info("发送弃件全程跟踪。运单号：{}",request.getWaybillCode());
            BdTraceDto packagePrintBdTraceDto = getPackagePrintBdTraceDto(request);
            //发送全程跟踪消息
            waybillQueryManager.sendBdTrace(packagePrintBdTraceDto);
        }catch (Exception e){
            log.error("弃件暂存异常,请求参数：{}", JsonHelper.toJson(request),e);
            result.error("弃件暂存异常,请联系分拣小秘！");
        }

        return result;
    }

    /**
     * 组装DB数据
     * @param bigWaybillDto
     * @param siteDto
     * @return
     */
    private List<DiscardedPackageStorageTemp> getDBList(BigWaybillDto bigWaybillDto,BaseStaffSiteOrgDto siteDto,WastePackageRequest request){
        List<DiscardedPackageStorageTemp> dbList=new ArrayList<>();
        if(bigWaybillDto.getPackageList()==null){
            return null;
        }

        Waybill WaybillInfo=bigWaybillDto.getWaybill();
        for (DeliveryPackageD pack : bigWaybillDto.getPackageList()){
            DiscardedPackageStorageTemp db=new DiscardedPackageStorageTemp();
            db.setWaybillCode(request.getWaybillCode());
            db.setPackageCode(pack.getPackageBarcode());
            db.setStatus(request.getStatus());
            db.setWaybillProduct(setTransportMode(WaybillInfo));
            db.setConsignmentName(arAbnormalService.getConsignmentNameByWaybillDto(bigWaybillDto));
            db.setWeight(BigDecimal.valueOf(WaybillInfo.getGoodWeight()));
            if(WaybillInfo.getPayment()!=null && (WaybillInfo.getPayment()==1 || WaybillInfo.getPayment()==3)){
                db.setCod(1);
            }else {
                db.setCod(0);
            }
            String codMoney = WaybillInfo.getCodMoney();
            if (codMoney != null) {
                db.setCodAmount(new BigDecimal(codMoney));
            }
            db.setBusinessCode(String.valueOf(WaybillInfo.getBusiId()));
            db.setBusinessName(WaybillInfo.getBusiName());
            db.setOperatorCode(request.getUserCode().longValue());
            db.setBusinessName(request.getUserName());
            db.setOperatorErp(request.getOperatorERP());
            db.setSiteCode(request.getSiteCode());
            db.setSiteName(request.getSiteName());
            db.setSiteCity(siteDto.getCityName());
            db.setOrgCode((int)siteDto.getAreaId());
            db.setOrgName(siteDto.getAreaName());
            Integer prevSiteCode=getPreSiteCode(pack.getPackageBarcode(),request.getSiteCode());
            db.setPrevSiteCode(prevSiteCode);
            if(prevSiteCode!=null){
                BaseStaffSiteOrgDto prevSiteDto = baseMajorManager.getBaseSiteBySiteId(request.getSiteCode());
                if(prevSiteDto==null){
                    db.setPrevSiteName(prevSiteDto.getSiteName());
                    db.setPrevProvinceCompanyCode(Integer.valueOf(prevSiteDto.getProvinceCompanyCode()));
                    db.setPrevProvinceCompanyName(prevSiteDto.getProvinceCompanyName());
                }
            }
            db.setCreateTime(DateHelper.parseAllFormatDateTime(request.getOperateTime()));

            dbList.add(db);
        }

        return dbList;
    }

    private Integer getPreSiteCode(String packageCode, Integer currentSiteCode) {
        Integer preSiteCode = null;

        //查全程跟踪
        BaseEntity<List<PackageState>> waybillTrackResult = waybillTraceManager.getPkStateByPCode(packageCode);
        // 解析全程跟踪数据
        if(waybillTrackResult != null && waybillTrackResult.getData() != null && waybillTrackResult.getData().size() > 0){
            List<PackageState> packageStateList = waybillTrackResult.getData();
            // 按全程跟踪的创建时间降序排列排序
            Collections.sort(packageStateList, new Comparator<PackageState>() {
                @Override
                public int compare(PackageState v1, PackageState v2) {
                    return v2.getCreateTime().compareTo(v1.getCreateTime());
                }
            });
            // 遍历，找到当前分拣中心之前的操作数据
            boolean findCurrentSiteOpLog = false;
            for(PackageState packageState : packageStateList){
                if(Objects.equals(packageState.getOperatorSiteId(), currentSiteCode)) {
                    //  找到当前分拣中心的操作记录，往后就是上游节点的数据
                    findCurrentSiteOpLog = true;
                }
                if (findCurrentSiteOpLog && !Objects.equals(packageState.getOperatorSiteId(), currentSiteCode)){
                    preSiteCode = packageState.getOperatorSiteId();
                    break;
                }
            }
        }

        return preSiteCode;
    }



    private String setTransportMode(Waybill waybill) {
        String res="";

        if(waybill == null){
            return res;
        }
        String waybillSign = waybill.getWaybillSign();
        if(StringHelper.isEmpty(waybillSign)){
            return res;
        }

        if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_0)){
            //5-特惠送
            res = TextConstants.PRODUCT_NAME_THS;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_6)){
            //13\14-京准达
            res = TextConstants.PRODUCT_NAME_JZD;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_F)){
            //31 = F  特惠小件
            res = TextConstants.PRODUCT_NAME_THXJ;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_G)){
            //31 = G  冷链专送
            res = TextConstants.PRODUCT_NAME_LLZS;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_7)
                && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_29, WaybillSignConstants.CHAR_29_8)){
            if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_55, WaybillSignConstants.CHAR_55_0)){
                //4-同城速配
                res = TextConstants.PRODUCT_NAME_TCSP;
            }else{
                //3-极速达
                res = TextConstants.PRODUCT_NAME_JSD;
            }
        }
        if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_55, WaybillSignConstants.CHAR_55_0)){
            if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_5)){
                //1-特惠送
                res = TextConstants.PRODUCT_NAME_THS;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_B)){
                //2-函速达
                res = TextConstants.PRODUCT_NAME_HSD;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_3)){
                //6-城际即日
                res = TextConstants.PRODUCT_NAME_CJJR;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_A)){
                //7-生鲜特惠
                res = TextConstants.PRODUCT_NAME_SXTH;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_9)){
                //8-生鲜特快
                res = TextConstants.PRODUCT_NAME_SXTK;
                // 8.1 - 生鲜特快下运营类型
                if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_2)) {
                    res += TextConstants.PRODUCT_NAME_SXTK_JR;
                }
                if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_3)) {
                    res += TextConstants.PRODUCT_NAME_SXTK_CC;
                }
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_C)){
                //16-特惠包裹
                res = TextConstants.PRODUCT_NAME_THBG;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                    && BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_0,WaybillSignConstants.CHAR_116_1)){
                //10-特快送
                res = TextConstants.PRODUCT_NAME_TKS;
            }else if((BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_2)
                    && BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_16, WaybillSignConstants.CHAR_16_1,WaybillSignConstants.CHAR_16_2
                    ,WaybillSignConstants.CHAR_16_3,WaybillSignConstants.CHAR_16_7,WaybillSignConstants.CHAR_16_8))
                    ||
                    (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                            && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_2))){
                //11-特快送即日
                res = TextConstants.PRODUCT_NAME_TKSJR;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_4)
                    ||
                    (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                            && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_3))){
                //12-特快送次晨
                res = TextConstants.PRODUCT_NAME_TKSCC;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                    && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_4)){
                //15-特快送
                res = TextConstants.PRODUCT_NAME_TKS;
            }

        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_55, WaybillSignConstants.CHAR_55_1)){
            //9-生鲜专送
            res = TextConstants.PRODUCT_NAME_SXZS;
        }

        return res;
    }

    private BdTraceDto getPackagePrintBdTraceDto(WastePackageRequest request) {
        BdTraceDto bdTraceDto = new BdTraceDto();
        bdTraceDto.setWaybillCode(request.getWaybillCode());
        bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL);
        bdTraceDto.setOperatorDesp(WaybillStatus.WAYBILL_TRACK_WASTE_WAYBILL_MSG);
        bdTraceDto.setOperatorSiteId(null!=request.getSiteCode()?request.getSiteCode():0);
        bdTraceDto.setOperatorSiteName(request.getSiteName());
        bdTraceDto.setOperatorUserName(request.getUserName());
        bdTraceDto.setOperatorUserId(null!=request.getUserCode()?request.getUserCode():0);
        bdTraceDto.setOperatorTime(new Date());
        return bdTraceDto;
    }

    /**
     * 参数检查
     * @param request
     * @return
     */
    private InvokeResult<Boolean> checkParam(WastePackageRequest request){
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.setCode(RESULT_PARAMETER_ERROR_CODE);
        if(request == null){
            invokeResult.setMessage("弃件暂存请求参数null");
            log.warn("弃件暂存上报请求参数为空");
            return invokeResult;
        }
        String barcode = request.getWaybillCode();
        if(StringUtils.isBlank(barcode)){
            invokeResult.setMessage("弃件暂存请求参数错误，运单号为空");
            log.warn("弃件暂存请求参数错误，运单号为空");
            return invokeResult;
        }

        if(!WaybillUtil.isWaybillCode(barcode)){
            invokeResult.setMessage("弃件暂存请求参数错误，运单号不正确");
            log.warn("弃件暂存请求参数错误，运单号不正确");
            return invokeResult;
        }

        invokeResult.success();
        return invokeResult;
    }
}

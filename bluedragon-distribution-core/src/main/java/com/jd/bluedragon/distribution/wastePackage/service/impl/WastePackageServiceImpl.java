package com.jd.bluedragon.distribution.wastePackage.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.OperateUser;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.WastePackageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao.DiscardedPackageStorageTempDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.enums.DiscardedPackageSiteDepartTypeEnum;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.service.DiscardedPackageStorageTempService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.record.entity.DmsHasnoPresiteWaybillMq;
import com.jd.bluedragon.distribution.record.enums.DmsHasnoPresiteWaybillMqOperateEnum;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.wastePackage.service.WastePackageService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private DiscardedPackageStorageTempDao discardedPackageStorageTempDao;
    @Autowired
    private WaybillHasnoPresiteRecordService waybillHasnoPresiteRecordService;

    @Qualifier("bdBlockerCompleteMQ")
    @Autowired
    private DefaultJMQProducer bdBlockerCompleteMQ;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DiscardedPackageStorageTempService discardedPackageStorageTempService;

    /**
     * 弃件暂存上报
     * @param request
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.com.WastePackageServiceImpl.wastepackagestorage", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> wastepackagestorage(WastePackageRequest request) {
        final InvokeResult<Boolean> result = new InvokeResult<>();
        final Result<List<DiscardedWaybillScanResultItemDto>> handleResult = discardedPackageStorageTempService.scanDiscardedPackage(this.convert2ScanDiscardedPackagePo(request));
        if(handleResult.isSuccess()){
            result.setData(true);
            result.setMessage(handleResult.getMessage());
            return result;
        } else {
            result.error(handleResult.getMessage());
            return result;
        }
    }

    private ScanDiscardedPackagePo convert2ScanDiscardedPackagePo(WastePackageRequest wastePackageRequest){
        final ScanDiscardedPackagePo scanDiscardedPackagePo = new ScanDiscardedPackagePo();
        scanDiscardedPackagePo.setBarCode(wastePackageRequest.getWaybillCode());
        if(StringUtils.isNotBlank(wastePackageRequest.getPackageCode())){
            scanDiscardedPackagePo.setBarCode(wastePackageRequest.getPackageCode());
        }
        scanDiscardedPackagePo.setWaybillType(wastePackageRequest.getWaybillType());
        scanDiscardedPackagePo.setStatus(wastePackageRequest.getStatus());
        scanDiscardedPackagePo.setOperateType(wastePackageRequest.getOperateType());
        scanDiscardedPackagePo.setSiteDepartType(DiscardedPackageSiteDepartTypeEnum.SORTING.getCode());
        OperateUser operateUser = new OperateUser();
        operateUser.setUserId(wastePackageRequest.getUserCode().longValue());
        operateUser.setUserCode(wastePackageRequest.getOperatorERP());
        operateUser.setUserName(wastePackageRequest.getUserName());
        operateUser.setSiteCode(wastePackageRequest.getSiteCode());
        operateUser.setSiteName(wastePackageRequest.getSiteName());
        scanDiscardedPackagePo.setOperateUser(operateUser);
        return scanDiscardedPackagePo;
    }
	/**
     * 弃件暂存处理
     * @param request
     * @return
     */
    public InvokeResult<Boolean> wasteWithStorage(WastePackageRequest request) {
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
            log.info("查询到运单信息。运单号：{}。返回结果：{}",request.getWaybillCode(),JsonHelper.toJson(baseEntity));
            if (baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()) {
                result.error("查询到运单信息失败:"+baseEntity.getMessage());
                return result;
            }

            if(baseEntity.getData() == null || baseEntity.getData().getWaybill()==null){
                result.error("没有查询到运单信息");
                return result;
            }

            DiscardedPackageStorageTempQo dbQuery=new DiscardedPackageStorageTempQo();
            dbQuery.setWaybillCode(request.getWaybillCode());
            dbQuery.setYn(Constants.YN_YES);
            long total = discardedPackageStorageTempDao.selectCount(dbQuery);
            boolean isUpdate=false;
            if(total>0){
                isUpdate=true;
            }

            List<DiscardedPackageStorageTemp> dbList=getDBList(baseEntity.getData(),siteDto,request,isUpdate);
            if(dbList==null || dbList.size()<=0){
                result.error("没有查询到运单包裹信息");
                return result;
            }

            int dbRes=-1;
            if(isUpdate){
                dbRes=discardedPackageStorageTempDao.updateByWaybillCode(dbList.get(0));
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
            waybillHasnoPresiteRecordService.sendDataChangeMq(toDmsHasnoPresiteWaybillMq(request));
        }catch (Exception e){
            log.error("弃件暂存异常,请求参数：{}", JsonHelper.toJson(request),e);
            result.error("弃件暂存异常,请联系分拣小秘！");
        }

        return result;
    }
	/**
     * 弃件废弃处理
     * @param request
     * @return
     */
    private InvokeResult<Boolean> wasteWithScrap(WastePackageRequest request) {
        InvokeResult<Boolean> result = new InvokeResult<>();

        if(!WaybillUtil.isPackageCode(request.getPackageCode())){
        	result.error("请输入有效的包裹号！");
            log.warn("弃件暂存请求参数错误，包裹号无效！");
            return result;
        }
        String waybillCode = WaybillUtil.getWaybillCodeByPackCode(request.getPackageCode());
        try {
            BaseStaffSiteOrgDto siteDto = baseMajorManager.getBaseSiteBySiteId(request.getSiteCode());
            if(siteDto==null){
                result.error("没有查询到操作站点信息");
                return result;
            }
            WChoice choice = new WChoice();
            choice.setQueryWaybillC(true);
            choice.setQueryPackList(true);
            choice.setQueryGoodList(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, choice);
            log.info("查询到运单信息。运单号：{}。返回结果：{}",waybillCode,JsonHelper.toJson(baseEntity));
            if (baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()) {
                result.error("查询到运单信息失败:"+baseEntity.getMessage());
                return result;
            }
            if(baseEntity.getData() == null || baseEntity.getData().getWaybill()==null){
                result.error("没有查询到运单信息");
                return result;
            }
            String sendPay = baseEntity.getData().getWaybill().getSendPay();
            String waybillSign = baseEntity.getData().getWaybill().getWaybillSign();
            if(!BusinessUtil.isScrapSortingSite(waybillSign)) {
                result.error("提交失败，非返分拣报废运单！");
                return result;
            }
            DiscardedPackageStorageTempQo dbQuery=new DiscardedPackageStorageTempQo();
            dbQuery.setPackageCode(request.getPackageCode());
            dbQuery.setYn(Constants.YN_YES);
            DiscardedPackageStorageTemp oldData = discardedPackageStorageTempDao.selectOne(dbQuery);
            boolean isUpdate=false;
            if(oldData != null){
                isUpdate=true;
            }

            DiscardedPackageStorageTemp packageStorageTemp = buildDiscardedPackageStorageTemp(baseEntity.getData(),siteDto,request,request.getPackageCode());
            int dbRes=-1;
            if(isUpdate){
            	packageStorageTemp.setId(oldData.getId());
                dbRes=discardedPackageStorageTempDao.updateById(packageStorageTemp);
            }else {
                dbRes=discardedPackageStorageTempDao.insertSelective(packageStorageTemp);
            }
            if(dbRes<0){
                result.error("弃件暂存失败，存储数据出现错误");
                return result;
            }

            log.info("发送弃件废弃全程跟踪，运单号：{}",waybillCode);
            //发送全程跟踪消息
            taskService.add(toWasteScrapTraceTask(request,waybillCode));
            //发送bd_blocker_complete的MQ
            if(BusinessUtil.isSx(sendPay)) {
                String mqData = BusinessUtil.bdBlockerCompleteMQ(waybillCode, DmsConstants.ORDER_TYPE_REVERSE, DmsConstants.MESSAGE_TYPE_BAOFEI, DateHelper.formatDateTimeMs(new Date()));
                this.bdBlockerCompleteMQ.send( waybillCode,mqData);
            }
        }catch (Exception e){
            log.error("弃件废弃异常,请求参数：{}", JsonHelper.toJson(request),e);
            result.error("弃件废弃异常,请联系分拣小秘！");
        }

        return result;
	}
    /**
     * 发送mq
     * @param request
     */
    private DmsHasnoPresiteWaybillMq toDmsHasnoPresiteWaybillMq(WastePackageRequest request) {
    	DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq = new DmsHasnoPresiteWaybillMq();
    	dmsHasnoPresiteWaybillMq.setWaybillCode(request.getWaybillCode());
    	dmsHasnoPresiteWaybillMq.setOperateCode(DmsHasnoPresiteWaybillMqOperateEnum.WASTE.getCode());
    	dmsHasnoPresiteWaybillMq.setOperateUserErp(request.getOperatorERP());
    	dmsHasnoPresiteWaybillMq.setOperateUserName(request.getUserName());
    	dmsHasnoPresiteWaybillMq.setOperateSiteCode(request.getSiteCode());
    	dmsHasnoPresiteWaybillMq.setOperateTime(new Date());
    	return dmsHasnoPresiteWaybillMq;
    }
    /**
     * 组装DB数据
     * @param bigWaybillDto
     * @param siteDto
     * @return
     */
    private List<DiscardedPackageStorageTemp> getDBList(BigWaybillDto bigWaybillDto,BaseStaffSiteOrgDto siteDto,WastePackageRequest request,boolean isUpdate){
        List<DiscardedPackageStorageTemp> dbList=new ArrayList<>();
        if(bigWaybillDto.getPackageList()==null){
            return null;
        }

        for (DeliveryPackageD pack : bigWaybillDto.getPackageList()){
            DiscardedPackageStorageTemp db=buildDiscardedPackageStorageTemp(bigWaybillDto,siteDto,request,pack.getPackageBarcode());
            dbList.add(db);

            if(isUpdate){
                break;
            }
        }

        return dbList;
    }
    /**
     * 组装数据库对象
     * @param bigWaybillDto
     * @param siteDto
     * @param request
     * @param packageCode
     * @return
     */
    private DiscardedPackageStorageTemp buildDiscardedPackageStorageTemp(BigWaybillDto bigWaybillDto,BaseStaffSiteOrgDto siteDto, WastePackageRequest request, String packageCode) {
            DiscardedPackageStorageTemp db=new DiscardedPackageStorageTemp();
        Waybill WaybillInfo=bigWaybillDto.getWaybill();
        db.setWaybillCode(WaybillInfo.getWaybillCode());
        db.setPackageCode(packageCode);
        db.setOperateType(request.getOperateType());
        db.setWaybillType(request.getWaybillType());
        db.setStatus(request.getStatus());
        db.setWaybillProduct(waybillQueryManager.getTransportMode(WaybillInfo));
        String consignmentName = waybillQueryManager.getConsignmentNameByWaybillDto(bigWaybillDto);
        //consignmentName 超过30位截取
        consignmentName = StringHelper.substring(consignmentName,0,30);
        db.setConsignmentName(consignmentName);
        db.setWeight(BigDecimal.valueOf(WaybillInfo.getGoodWeight()));
        if(WaybillInfo.getPayment()!=null && (WaybillInfo.getPayment()==1 || WaybillInfo.getPayment()==3)){
            db.setCod(1);
        }else {
            db.setCod(0);
        }
        String codMoney = WaybillInfo.getCodMoney();
        if (codMoney != null) {
            db.setCodAmount(codMoney);
        }
        db.setBusinessCode(String.valueOf(WaybillInfo.getBusiId()));
        db.setBusinessName(WaybillInfo.getBusiName());
        db.setOperatorCode(request.getUserCode().longValue());
        db.setOperatorName(request.getUserName());
        db.setOperatorErp(request.getOperatorERP());
        db.setSiteCode(request.getSiteCode());
        db.setSiteName(request.getSiteName());
        db.setSiteCity(siteDto.getCityName());
        db.setOrgCode(siteDto.getOrgId());
        db.setOrgName(siteDto.getOrgName());
        Integer prevSiteCode=getPreSiteCode(packageCode,request.getSiteCode());
            db.setPrevSiteCode(prevSiteCode);
            if(prevSiteCode!=null){
                BaseStaffSiteOrgDto prevSiteDto = baseMajorManager.getBaseSiteBySiteId(prevSiteCode);
                if(prevSiteDto!=null){
                    db.setPrevSiteName(prevSiteDto.getSiteName());
                    db.setPrevProvinceCompanyCode(prevSiteDto.getProvinceCompanyCode());
                    db.setPrevProvinceCompanyName(prevSiteDto.getProvinceCompanyName());
                }
            }
            db.setCreateTime(DateHelper.parseAllFormatDateTime(request.getOperateTime()));
        return db;
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
    /**
     * 转成全程跟踪任务对象
     * @param request
     * @return0
     */
    private Task toWasteScrapTraceTask(WastePackageRequest request,String waybillCode) {
        WaybillStatus waybillStatus = new WaybillStatus();
        //设置站点相关属性
        waybillStatus.setPackageCode(request.getPackageCode());
        waybillStatus.setWaybillCode(waybillCode);
        waybillStatus.setCreateSiteCode(null!=request.getSiteCode()?request.getSiteCode():0);
        waybillStatus.setCreateSiteName(request.getSiteName());

        waybillStatus.setOperatorId(null!=request.getUserCode()?request.getUserCode():0);
        waybillStatus.setOperator(request.getUserName());
        waybillStatus.setOperateTime(new Date());
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP);

        waybillStatus.setRemark(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP_MSG);

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_WAYBILL);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(waybillStatus.getPackageCode());
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setFingerprint(Md5Helper.encode(waybillStatus.getCreateSiteCode() + "_"
                + waybillStatus.getPackageCode() + "-" + waybillStatus.getOperateType() + "-" + waybillStatus.getOperateTime().getTime() ));
        return task;
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

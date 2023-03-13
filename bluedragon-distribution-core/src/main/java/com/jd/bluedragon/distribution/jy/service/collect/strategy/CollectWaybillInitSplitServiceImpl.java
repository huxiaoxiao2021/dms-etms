package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.constant.CollectConstant;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectInitSplitServiceFactory;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jsf.gd.util.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Description //集齐初始化： 无任务扫描，按运单号分页拆分
 * @date
 **/
public class CollectWaybillInitSplitServiceImpl implements CollectInitSplitService, InitializingBean {
    private Logger log = LoggerFactory.getLogger(CollectWaybillInitSplitServiceImpl.class);

//    @Autowired
//    private CargoDetailServiceManager cargoDetailServiceManager;
    @Autowired
    private JyCollectService jyCollectService;
//    @Autowired
//    private VosManager vosManager;
//    @Autowired
//    private BaseMajorManager baseMajorManager;
    @Autowired
    WaybillQueryManager waybillQueryManager;
    @Autowired
    @Qualifier(value = "jyCollectDataPageInitProducer")
    private DefaultJMQProducer jyCollectDataPageInitProducer;
    @Autowired
    WaybillPackageManager waybillPackageManager;
    @Autowired
    private WaybillService waybillService;


    @Override
    public void afterPropertiesSet() throws Exception {
        CollectInitSplitServiceFactory.registerCollectInitSplitService(CollectInitNodeEnum.NULL_TASK_INIT.getCode(), this);
    }

    @Override
    public boolean splitBeforeInit(InitCollectDto initCollectDto) {
        String methodDesc = "CollectWaybillInitSplitServiceImpl.splitBeforeInit:集齐数据初始化前按运单拆分批次：";
        String waybillCode = WaybillUtil.getWaybillCode(initCollectDto.getTaskNullScanCode());

        BigWaybillDto bigWaybillDto = getWaybillPackage(waybillCode);
        if (bigWaybillDto == null || CollectionUtils.isEmpty(bigWaybillDto.getPackageList())) {
            log.warn("{}运单{}多包裹拆分任务, 查询包裹为空, reqDto:[{}]", methodDesc, waybillCode, JsonHelper.toJson(initCollectDto));
            return false;
        }
        int collectOneBatchSize = CollectConstant.COLLECT_INIT_BATCH_DEAL_SIZE;
        int totalPackageNum = bigWaybillDto.getPackageList().size();
        int collectBatchPageTotal = (totalPackageNum % collectOneBatchSize) == 0 ? (totalPackageNum / collectOneBatchSize) : (totalPackageNum / collectOneBatchSize) + 1;
        for (int pageNo = 0; pageNo < collectBatchPageTotal; pageNo++) {
            InitCollectSplitDto mqDto = new InitCollectSplitDto();
            mqDto.setBizId(initCollectDto.getBizId());
            mqDto.setOperateTime(initCollectDto.getOperateTime());
            mqDto.setPageSize(collectBatchPageTotal);
            mqDto.setPageNo(pageNo);
            mqDto.setOperateNode(initCollectDto.getOperateNode());
            mqDto.setOperatorErp(initCollectDto.getOperatorErp());

            mqDto.setTaskNullScanCode(initCollectDto.getTaskNullScanCode());
            mqDto.setWaybillCode(waybillCode);
            mqDto.setTaskNullScanCodeType(initCollectDto.getTaskNullScanCodeType());
            mqDto.setTaskNullScanSiteCode(initCollectDto.getTaskNullScanSiteCode());
            String businessId = String.format("%:%s", mqDto.getSealBatchCode(), mqDto.getPageNo());
            String msg = JsonUtils.toJSONString(mqDto);
            if(log.isInfoEnabled()) {
                log.info("{}.splitSendMq, msg={}", msg);
            }
            jyCollectDataPageInitProducer.sendOnFailPersistent(businessId, msg);
        }

        return true;
    }

    @Override
    public boolean initAfterSplit(InitCollectSplitDto request) {
        String waybillCode = getWaybillCode(request);
        List<String> packageCodeList = getPageNoPackageCodeListFromWaybill(waybillCode, request.getPageNo(), request.getPageSize());
        Integer nextSiteId = waybillService.getRouterFromMasterDb(waybillCode, request.getTaskNullScanSiteCode());
        if(nextSiteId == null) {
            log.warn("CollectWaybillInitSplitServiceImpl.initAfterSplit集齐运单查询下游流向为空，reqDto={}", JsonHelper.toJson(request));
        }
        CollectDto collectDto = new CollectDto();
        collectDto.setCollectNodeSiteCode(request.getTaskNullScanSiteCode());
        collectDto.setBizId(request.getBizId());
        collectDto.setWaybillCode(waybillCode);
        collectDto.setNextSiteCode(nextSiteId);
        collectDto.setOperatorErp(request.getOperatorErp());
        return jyCollectService.initCollect(collectDto, packageCodeList);
    }

    private List<String> getPageNoPackageCodeListFromWaybill(String waybillCode, int pageNo, int pageSize) {
        // 分页查询包裹数据
        BigWaybillDto bigWaybillDto = getWaybill(waybillCode, pageNo, pageSize);
        List<String> res = new ArrayList<>();
        List<DeliveryPackageD> packages=bigWaybillDto.getPackageList();
        for (DeliveryPackageD pack : packages) {
            res.add(pack.getPackageBarcode());
        }
        return res;
    }

    private BigWaybillDto getWaybillPackage(String waybillCode) {
        BigWaybillDto result = null;
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true, false, true, true);
        if (baseEntity != null) {
            result = baseEntity.getData();
        }
        if (log.isInfoEnabled()){
            log.info(MessageFormat.format("获取运单信息{0}, 结果为{1}", waybillCode, JsonHelper.toJson(result)));
        }

        return result;
    }

    private String getWaybillCode(InitCollectSplitDto request) {
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            return request.getWaybillCode();
        }
        else {
            String waybillCode = WaybillUtil.getWaybillCode(request.getTaskNullScanCode());
            request.setWaybillCode(waybillCode);
            return waybillCode;
        }
    }

    /**
     * 分页获取包裹数据
     * @param waybillCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    private BigWaybillDto getWaybill(String waybillCode, int pageNo, int pageSize) {

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(false);
        wChoice.setQueryWaybillM(true); // 查询waybillState

        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
        BigWaybillDto bigWaybillDto = null;
        if (baseEntity != null && baseEntity.getData()!= null) {
            bigWaybillDto = baseEntity.getData();
            BaseEntity<List<DeliveryPackageD>> pageLists =
                    this.waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
            if (pageLists != null && pageLists.getData() != null ) {
                bigWaybillDto.setPackageList(pageLists.getData());
            }
        }

        return bigWaybillDto;
    }

}

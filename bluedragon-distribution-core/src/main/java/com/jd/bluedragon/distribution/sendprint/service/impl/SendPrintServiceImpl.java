package com.jd.bluedragon.distribution.sendprint.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.security.log.SecurityLogWriter;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineWaybillDTO;
import com.jd.bluedragon.distribution.quickProduce.domain.JoinDetail;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareService;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.dao.SendMReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sendprint.domain.*;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.distribution.sendprint.utils.SendPrintConstants;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.bluedragon.distribution.weightAndMeasure.service.DmsOutWeightAndVolumeService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverListDto;
import com.jd.dms.wb.report.api.dto.printhandover.PrintHandoverLitQueryCondition;
import com.jd.dms.workbench.utils.sdk.base.PageData;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.jsf.gd.error.RpcException;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.BoardMeasureDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("sendPrintService")
public class SendPrintServiceImpl implements SendPrintService {

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private SendMReadDao sendMReadDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    WaybillPackageApi waybillPackageApi;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillPickupTaskApi waybillPickupTaskApi;

    @Autowired
    private SealBoxService tSealBoxService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    private final Logger log = LoggerFactory.getLogger(SendPrintServiceImpl.class);

    @Autowired
    private QuickProduceService quickProduceService;

    @Autowired
    private BoxService boxService;

    @Autowired
    BoardCombinationService boardCombinationService;

    @Autowired
    private DmsOutWeightAndVolumeService dmsOutWeightAndVolumeService;

    @Autowired
    private SendMService sendMService;

    @Autowired
    WaybillPackageManager waybillPackageManager;

    @Autowired
    private SendCodeService sendCodeService;


    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private PrintHandoverListManager printHandoverListManager;

    @Autowired
    private FlowServiceManager flowServiceManager;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private ReverseSpareService reverseSpareService;

    @Autowired
    private LogEngine logEngine;

    private static int PARAM_CM3_M3 = 1000000;//立方厘米和立方米的换算基数

    public final static Integer ASM_TYPE = Integer.parseInt(PropertiesHelper.newInstance().getValue("asm_type"));

    /**
     * 一次批量查询运单数据的大小
     */
    private final static int QUERY_WAYBILL_SIZE = 50;

    /**
     * 一次批量查询包裹数据的大小
     */
    private final static int QUERY_PACKAGE_SIZE = 50;

    /**
     * 一次批量查询SENDM的大小
     */
    private final static int QUERY_SENDM_SIZE = 500;

    private final static int THIRD_EXPORT_SUC_CODE = 201;

    private final static int BATCH_SUMMARY_QUERY_ES_CODE = 10000;

    /**
     * 批次汇总&&批次汇总打印
     */
    @Override
    public SummaryPrintResultResponse batchSummaryPrintQuery(PrintQueryCriteria criteria) {
        SummaryPrintResultResponse tSummaryPrintResultResponse = new SummaryPrintResultResponse();
        List<SummaryPrintResult> results = new ArrayList<SummaryPrintResult>();

        CallerInfo info = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.batchSummaryPrintQuery", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            // 判断是否走新汇总查询
            if(uccPropertyConfiguration.getNewPrintHandoverListSwitch()){
                return newBatchSummaryPrintQuery(criteria);
            }
            SendM nSendM = tosendM(criteria);
            Date startDate = new Date();
            log.debug("打印交接清单-批次汇总开始:{}" , DateHelper.formatDate(startDate));
            //满足条件的所有箱号
            List<SendM> sendMs = this.selectUniquesSendMs(nSendM); //this.sendMDao.selectBySendSiteCode(nSendM);
            if (sendMs != null && !sendMs.isEmpty()) {
                log.debug("打印交接清单-批次汇总数目:{}" , sendMs.size());
                results = this.summaryPrintResultToList(sendMs, criteria);
            }
            tSummaryPrintResultResponse.setCode(JdResponse.CODE_OK);
            tSummaryPrintResultResponse.setMessage(JdResponse.MESSAGE_OK);
            tSummaryPrintResultResponse.setData(results);
            Date endDate = new Date();
            log.debug("打印交接清单-批次汇总结束-{}" , (startDate.getTime() - endDate.getTime()));
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("批次汇总&&批次汇总打印异常", e);
            tSummaryPrintResultResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tSummaryPrintResultResponse.setMessage("批次汇总打印异常");
            tSummaryPrintResultResponse.setData(results);
            return tSummaryPrintResultResponse;
        }finally {
            Profiler.registerInfoEnd(info);
        }
        // 记录安全日志
        SecurityLogWriter.sendPrintBasicPrintQueryWrite(criteria, null, tSummaryPrintResultResponse);
        return tSummaryPrintResultResponse;
    }

    /**
     * 根据es数据汇总查询
     * @param criteria
     * @return
     */
    private SummaryPrintResultResponse newBatchSummaryPrintQuery(PrintQueryCriteria criteria) {
        long startTime = System.currentTimeMillis();
        SummaryPrintResultResponse response = new SummaryPrintResultResponse();
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);
        CallerInfo info = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.newBatchSummaryPrintQuery", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            // 批次对应的基础数据
            Map<String,SummaryPrintResult> batchBasicMap = new HashMap<>();
            // 批次对应的箱号，箱号对应的信息
            Map<String,Map<String,SummaryPrintBoxEntity>> batchBoxMap = new HashMap<>();
            // 批次对应的箱号，箱号对应的运单号集合 fix:不统计箱号是包裹号的情况(运单数量肯定是1，尽量减少大对象)
            Map<String,Map<String,Set<String>>> batchBoxWaybillMap = new HashMap<>();

            // 单次scroll查询数量、scroll查询最大限制次数
            int batchSize = uccPropertyConfiguration.getScrollQuerySize();
            int printScrollQueryCountLimit = uccPropertyConfiguration.getPrintScrollQueryCountLimit();

            Pager<PrintHandoverLitQueryCondition> query = new Pager<PrintHandoverLitQueryCondition>();
            query.setPageNo(Constants.CONSTANT_NUMBER_ONE);
            query.setPageSize(batchSize);
            query.setSearchVo(convertToPrintHandoverListQueryCondition(criteria, false));
            int count = Constants.NUMBER_ZERO;
            while (count < printScrollQueryCountLimit){
                // 通过scrollId查询发货交接汇总数据
                PageData<PrintHandoverListDto> printHandoverListDtoPageData = printHandoverListManager.queryPrintHandOverListByScroll(query);
                if(printHandoverListDtoPageData == null ||  CollectionUtils.isEmpty(printHandoverListDtoPageData.getRecords())){
                    log.warn("scroll查询发货交接清单汇总数据为空!");
                    break;
                }
                // 设置scrollId值
                query.setScrollId(printHandoverListDtoPageData.getScrollId());

                // 通过明细获取单次的汇总数据
                List<SummaryPrintResult> singleSummaryPrintResult = getSummaryResultByPrintHandoverData(batchBoxWaybillMap, printHandoverListDtoPageData.getRecords());

                // 处理单批次的汇总数据
                singleDealPrintHandoverData(batchBasicMap, batchBoxMap, singleSummaryPrintResult);

                count ++;
            }
            log.info("批次对应的基础数据:{}", JsonHelper.toJson(batchBasicMap));
            log.info("批次对应的箱号，箱号对应的信息:{}", JsonHelper.toJson(batchBoxMap));

            // 获取最终汇总数据
            List<SummaryPrintResult> list = computeFinalSummaryResult(batchBasicMap, batchBoxWaybillMap, batchBoxMap);
            if(CollectionUtils.isEmpty(list)){
                response.setCode(JdResponse.CODE_OK_NULL);
                response.setMessage(JdResponse.MESSAGE_OK_NULL);
                return response;
            }
            response.setData(list);
            return response;
        }catch (RpcException e){
            log.warn("jsf服务端查询超时，请重新查询!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage("请重新查询!");
        }catch (Exception e){
            log.error("批次汇总新查询异常!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
            if(log.isInfoEnabled()){
                log.info("发货交接清单汇总查询耗时:{}", System.currentTimeMillis() - startTime);
            }
        }
        return response;
    }

    /**
     * 计算最终汇总数据
     * @param batchBasicMap
     * @param batchBoxMap
     * @return
     */
    private List<SummaryPrintResult> computeFinalSummaryResult(Map<String, SummaryPrintResult> batchBasicMap,
                                                               Map<String,Map<String,Set<String>>> batchBoxWaybillMap,
                                                               Map<String, Map<String, SummaryPrintBoxEntity>> batchBoxMap) {
        List<SummaryPrintResult> list = new ArrayList<>();
        if(batchBasicMap.isEmpty() || batchBoxMap.isEmpty()){
            return null;
        }
        for (Map.Entry<String, SummaryPrintResult> batchEntry : batchBasicMap.entrySet()) {
            SummaryPrintResult summaryPrintResult = batchEntry.getValue();
            if(batchBoxMap.containsKey(batchEntry.getKey())){
                Map<String, SummaryPrintBoxEntity> boxEntityMap = batchBoxMap.get(batchEntry.getKey());
                // 设置批次下箱的基础数据
                summaryPrintResult.setDetails(new ArrayList<>(boxEntityMap.values()));
            }
            // 设置批次下箱号、批次下箱号+包裹、批次下箱号下运单数量
            dealWithNum(summaryPrintResult, batchBoxWaybillMap.get(batchEntry.getKey()));
            list.add(summaryPrintResult);
        }
        return list;
    }

    /**
     * 设置批次下箱号、批次下箱号+包裹、批次下箱号下运单数量
     * @param summaryPrintResult
     * @param boxWaybillMap
     */
    private void dealWithNum(SummaryPrintResult summaryPrintResult, Map<String, Set<String>> boxWaybillMap) {
        if(CollectionUtils.isEmpty(summaryPrintResult.getDetails()) || boxWaybillMap == null){
            return;
        }
        Set<String> boxSet = new HashSet<>();
        for (SummaryPrintBoxEntity summaryPrintBoxEntity : summaryPrintResult.getDetails()) {

            // 批次下原包下运单数量
            summaryPrintBoxEntity.setWaybillNum(Constants.CONSTANT_NUMBER_ONE);

            if(BusinessUtil.isBoxcode(summaryPrintBoxEntity.getBoxCode())){
                boxSet.add(summaryPrintBoxEntity.getBoxCode());
                // 批次下箱号下运单数量
                Set<String> waybillSet = boxWaybillMap.get(summaryPrintBoxEntity.getBoxCode());
                summaryPrintBoxEntity.setWaybillNum(waybillSet == null ? Constants.NUMBER_ZERO : waybillSet.size());
            }
        }
        // 设置批次下箱号、箱号+包裹数量
        int boxSize = boxSet.size();
        summaryPrintResult.setTotalBoxNum(boxSize);
        summaryPrintResult.setTotalBoxAndPackageNum(boxSize + summaryPrintResult.getTotalPackageNum());
    }

    /**
     * 处理单批次汇总数据
     * @param batchBasicMap
     * @param batchBoxMap
     * @param singleSummaryPrintResult
     */
    private void singleDealPrintHandoverData(Map<String, SummaryPrintResult> batchBasicMap,
                                             Map<String, Map<String, SummaryPrintBoxEntity>> batchBoxMap,
                                             List<SummaryPrintResult> singleSummaryPrintResult) {
        if(CollectionUtils.isEmpty(singleSummaryPrintResult)){
            return;
        }
        for (SummaryPrintResult summaryPrintResult : singleSummaryPrintResult) {
            // 批次对应的基础数据处理
            if(batchBasicMap.containsKey(summaryPrintResult.getSendCode())){
                SummaryPrintResult computeSummaryPrintResult = batchBasicMap.get(summaryPrintResult.getSendCode());
                computeSummaryPrintResult.setTotalPackageNum(computeSummaryPrintResult.getTotalPackageNum() + summaryPrintResult.getTotalPackageNum());
                computeSummaryPrintResult.setTotalShouldSendPackageNum(computeSummaryPrintResult.getTotalShouldSendPackageNum() + summaryPrintResult.getTotalShouldSendPackageNum());
                computeSummaryPrintResult.setTotalRealSendPackageNum(computeSummaryPrintResult.getTotalRealSendPackageNum() + summaryPrintResult.getTotalRealSendPackageNum());
                computeSummaryPrintResult.setTotalBoardVolume((computeSummaryPrintResult.getTotalBoardVolume() == null ? Constants.DOUBLE_ZERO : computeSummaryPrintResult.getTotalBoardVolume())
                        + (summaryPrintResult.getTotalBoardVolume() == null ? Constants.DOUBLE_ZERO : summaryPrintResult.getTotalBoardVolume()));
                computeSummaryPrintResult.setTotalOutVolumeDynamic((computeSummaryPrintResult.getTotalOutVolumeDynamic() == null ? Constants.DOUBLE_ZERO : computeSummaryPrintResult.getTotalOutVolumeDynamic())
                        + (summaryPrintResult.getTotalOutVolumeDynamic() == null ? Constants.DOUBLE_ZERO : summaryPrintResult.getTotalOutVolumeDynamic()));
                computeSummaryPrintResult.setTotalOutVolumeStatic((computeSummaryPrintResult.getTotalOutVolumeStatic() == null ? Constants.DOUBLE_ZERO : computeSummaryPrintResult.getTotalOutVolumeStatic())
                        + (summaryPrintResult.getTotalOutVolumeStatic() == null ? Constants.DOUBLE_ZERO : summaryPrintResult.getTotalOutVolumeStatic()));
                computeSummaryPrintResult.setTotalInVolume((computeSummaryPrintResult.getTotalInVolume() == null ? Constants.DOUBLE_ZERO : computeSummaryPrintResult.getTotalInVolume())
                        + (summaryPrintResult.getTotalInVolume() == null ? Constants.DOUBLE_ZERO : summaryPrintResult.getTotalInVolume()));
            }else {
                batchBasicMap.put(summaryPrintResult.getSendCode(), summaryPrintResult);
            }
            // 批次对应的箱号，箱号对应的数据处理
            if(batchBoxMap.containsKey(summaryPrintResult.getSendCode())){
                // 箱号对应的数据处理
                Map<String, SummaryPrintBoxEntity> boxEntityMap = batchBoxMap.get(summaryPrintResult.getSendCode());
                if(CollectionUtils.isEmpty(summaryPrintResult.getDetails())){
                    continue;
                }
                for (SummaryPrintBoxEntity detail : summaryPrintResult.getDetails()) {
                    if(boxEntityMap.containsKey(detail.getBoxCode())){
                        SummaryPrintBoxEntity summaryPrintBoxEntity = boxEntityMap.get(detail.getBoxCode());
                        summaryPrintBoxEntity.setPackageBarNum(summaryPrintBoxEntity.getPackageBarNum() + detail.getPackageBarNum());
                        summaryPrintBoxEntity.setPackageBarRecNum(summaryPrintBoxEntity.getPackageBarRecNum() + detail.getPackageBarRecNum());
                        summaryPrintBoxEntity.setVolume((summaryPrintBoxEntity.getVolume() == null ? Constants.DOUBLE_ZERO : summaryPrintBoxEntity.getVolume())
                                + (detail.getVolume() == null ? Constants.DOUBLE_ZERO : detail.getVolume()));
                    }else {
                        boxEntityMap.put(detail.getBoxCode(), detail);
                    }
                }
            }else {
                Map<String, SummaryPrintBoxEntity> boxMap = new HashMap<>();
                if(CollectionUtils.isEmpty(summaryPrintResult.getDetails())){
                    continue;
                }
                for (SummaryPrintBoxEntity detail : summaryPrintResult.getDetails()) {
                    boxMap.put(detail.getBoxCode(), detail);
                }
                batchBoxMap.put(summaryPrintResult.getSendCode(), boxMap);
            }
        }

    }


    /**
     * 获取单批次的汇总数据
     * @param records
     * @return
     */
    private List<SummaryPrintResult> getSummaryResultByPrintHandoverData(Map<String,Map<String,Set<String>>> batchBoxWaybillMap,
                                                                         List<PrintHandoverListDto> records) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.getSummaryResultByPrintHandoverData", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            //按批次分类
            Map<String,List<PrintHandoverListDto>> sendBaseMap = new HashMap<String, List<PrintHandoverListDto>>();
            for (PrintHandoverListDto record : records) {
                String sendCode = record.getSendCode();
                if(sendBaseMap.containsKey(sendCode)){
                    sendBaseMap.get(sendCode).add(record);
                }else {
                    List<PrintHandoverListDto> entities =  new ArrayList<PrintHandoverListDto>();
                    entities.add(record);
                    sendBaseMap.put(sendCode,entities);
                }
            }
            //遍历批次，分别组装
            List<SummaryPrintResult> summaryPrintResultList = new ArrayList<SummaryPrintResult>();
            for(String sendCode : sendBaseMap.keySet()){
                SummaryPrintResult summaryPrintResult = new SummaryPrintResult();
                summaryPrintResult.setSendCode(sendCode);
                summaryPrintResult.setSendSiteName(records.get(0).getCreateSiteName());
                summaryPrintResult.setReceiveSiteName(records.get(0).getReceiveSiteName());
                Map<String,Set<String>> boxWaybillMap = new HashMap<>();
                if(batchBoxWaybillMap.containsKey(sendCode)){
                    boxWaybillMap = batchBoxWaybillMap.get(sendCode);
                }
                summaryPrintResultList.add(singleSendSummary(boxWaybillMap,summaryPrintResult,sendBaseMap.get(sendCode)));
                batchBoxWaybillMap.put(sendCode, boxWaybillMap);
            }
            return summaryPrintResultList;
        }catch (Exception e){
            log.error("处理单批次的汇总数据异常!", e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    /**
     * 获取单个批次号的汇总数据
     * @param summaryPrintResult
     * @param singlePrintHandoverList
     * @return
     */
    private SummaryPrintResult singleSendSummary(Map<String,Set<String>> boxWaybillMap,
                                                 SummaryPrintResult summaryPrintResult,
                                                 List<PrintHandoverListDto> singlePrintHandoverList){
        Map<String, SummaryPrintBoxEntity> boxMap = new HashMap<String, SummaryPrintBoxEntity>();
        List<SummaryPrintBoxEntity> details = new ArrayList<SummaryPrintBoxEntity>();

        Integer totalBoxNum = Constants.NUMBER_ZERO ; //单个批次内的箱子数量
        Integer totalPackageNum = Constants.NUMBER_ZERO; //单个批次内的包裹数量
        Double totalBoardVolume = Constants.DOUBLE_ZERO;  //总的板体积
        Double totalOutVolumeDy = Constants.DOUBLE_ZERO;  //总的应付自动测量体积
        Double totalOutVolumeSt = Constants.DOUBLE_ZERO;  //总的应付人工测量体积
        Double totalInVolume = Constants.DOUBLE_ZERO;     //总的应收体积

        String roadCode  = null; //路区号
        String sendTime = Constants.EMPTY_FILL;//发货时间

        /** 已经处理过的板号集合，每条记录的托盘体积都是整个托盘的体积，所以只需记一次 **/
        Set<String> boardVolumeSet = new HashSet<String>();
        /** 已经处理过的箱号集合，每条记录的箱体积都是整个箱的体积，所以只需记一次 **/
        Set<String> boxVolumeSet = new HashSet<String>();

        //循环处理批次内的每一条记录完成统计功能
        for(PrintHandoverListDto printHandoverListDto : singlePrintHandoverList) {
            //取每个批次中第一单的路区号
            if(StringUtils.isBlank(roadCode) && StringUtils.isNotBlank(printHandoverListDto.getRoadCode())){
                roadCode = printHandoverListDto.getRoadCode();
            }
            //发货时间
            if(StringUtils.isBlank(sendTime)){
                sendTime = printHandoverListDto.getSendTime() == null ? null : DateHelper.formatDateTime(new Date(printHandoverListDto.getSendTime()));
            }

            SummaryPrintBoxEntity summaryEntity = null;
            //如果是按箱处理的，把箱里的包裹进行组装
            if (BusinessHelper.isBoxcode(printHandoverListDto.getBoxCode())) {
                if (boxMap.containsKey(printHandoverListDto.getBoxCode())) {
                    summaryEntity = boxMap.get(printHandoverListDto.getBoxCode());
                    summaryEntity.setPackageBarNum(summaryEntity.getPackageBarNum() + Constants.CONSTANT_NUMBER_ONE);
                    summaryEntity.setPackageBarRecNum(summaryEntity.getPackageBarRecNum() + Constants.CONSTANT_NUMBER_ONE);
                } else {
                    summaryEntity = new SummaryPrintBoxEntity();
                    summaryEntity.setBoxCode(printHandoverListDto.getBoxCode());
                    summaryEntity.setPackageBarNum(Constants.CONSTANT_NUMBER_ONE);
                    summaryEntity.setPackageBarRecNum(Constants.CONSTANT_NUMBER_ONE);
                    //设置封签号、封车时间和箱的体积
                    summaryEntity.setSealNo1(printHandoverListDto.getSealNo());
                    summaryEntity.setSealNo2(Constants.EMPTY_FILL);
                    summaryEntity.setLockTime(printHandoverListDto.getSealTime() == null
                            ? null : DateHelper.formatDateTime(new Date(printHandoverListDto.getSealTime())));//封车时间
                    summaryEntity.setVolume(Constants.DOUBLE_ZERO);
                    boxMap.put(printHandoverListDto.getBoxCode(),summaryEntity);
                    totalBoxNum ++;
                }
                if (boxWaybillMap.containsKey(printHandoverListDto.getBoxCode())) {
                    // 设置箱号对应的运单数量
                    boxWaybillMap.get(printHandoverListDto.getBoxCode()).add(printHandoverListDto.getWaybillCode());
                }else {
                    // 初始化箱号对应的运单MAP
                    Set<String> waybillCodeSet = new HashSet<>();
                    waybillCodeSet.add(printHandoverListDto.getWaybillCode());
                    boxWaybillMap.put(printHandoverListDto.getBoxCode(), waybillCodeSet);
                }
            } else {
                //按包裹号处理的
                summaryEntity = new SummaryPrintBoxEntity();
                summaryEntity.setBoxCode(printHandoverListDto.getBoxCode());
                summaryEntity.setPackageBarNum(Constants.CONSTANT_NUMBER_ONE);
                summaryEntity.setPackageBarRecNum(Constants.CONSTANT_NUMBER_ONE);
                summaryEntity.setSealNo1(Constants.EMPTY_FILL);
                summaryEntity.setSealNo2(Constants.EMPTY_FILL);
                summaryEntity.setVolume(printHandoverListDto.getGoodVolume());

                details.add(summaryEntity);

                totalPackageNum ++;
            }

            /**
             * 体积汇总逻辑：
             * 有板体积以板的体积为主；
             * 没有板体积，有箱体积，则以箱体积为主；
             * 没有板体积，也没有箱体积，则以包裹体积为主；
             */

            //如果有板的体积，把已经计算过体积的板号写入boardVolumeSet，避免重复计算
            if(StringUtils.isNotBlank(printHandoverListDto.getBoardCode()) && NumberHelper.gt0(printHandoverListDto.getBoardVolume())){
                if(!boardVolumeSet.contains(printHandoverListDto.getBoardCode())){
                    boardVolumeSet.add(printHandoverListDto.getBoardCode());
                    totalBoardVolume += printHandoverListDto.getBoardVolume() == null ? Constants.DOUBLE_ZERO : printHandoverListDto.getBoardVolume();
                    totalOutVolumeSt += printHandoverListDto.getBoardVolume() == null ? Constants.DOUBLE_ZERO : printHandoverListDto.getBoardVolume(); //板的体积算作静态测量体积
                }
            }else if(StringUtils.isNotBlank(printHandoverListDto.getBoxCode()) && BusinessHelper.isBoxcode(printHandoverListDto.getBoxCode())){
                //没有板号，或者板的体积为空，但是有箱号（box_code字段为箱号）
                if(boxVolumeSet.contains(printHandoverListDto.getBoxCode())){
                    continue;
                }
                boxVolumeSet.add(printHandoverListDto.getBoxCode());

                totalOutVolumeDy += printHandoverListDto.getDmsOutVolumeStatic() == null ? Constants.DOUBLE_ZERO : printHandoverListDto.getDmsOutVolumeStatic();
                totalOutVolumeSt += printHandoverListDto.getDmsOutVolumeStatic() == null ? Constants.DOUBLE_ZERO : printHandoverListDto.getDmsOutVolumeStatic();

            }else{
                //按包裹测量
                totalOutVolumeDy += printHandoverListDto.getDmsOutVolumeStatic() == null ? Constants.DOUBLE_ZERO : printHandoverListDto.getDmsOutVolumeStatic();
                totalOutVolumeSt += printHandoverListDto.getDmsOutVolumeStatic() == null ? Constants.DOUBLE_ZERO : printHandoverListDto.getDmsOutVolumeStatic();
            }

            //应收体积
            totalInVolume += printHandoverListDto.getGoodVolume() == null ? Constants.DOUBLE_ZERO : printHandoverListDto.getGoodVolume();
        }

        //map转换成list
        details.addAll(boxMap.values());

        summaryPrintResult.setSendTime(sendTime);
        summaryPrintResult.setRoadCode(roadCode);
        summaryPrintResult.setTotalBoxNum(totalBoxNum);
        summaryPrintResult.setTotalPackageNum(totalPackageNum);
        summaryPrintResult.setTotalBoxAndPackageNum(totalBoxNum+totalPackageNum);
        summaryPrintResult.setTotalShouldSendPackageNum(singlePrintHandoverList.size());
        summaryPrintResult.setTotalRealSendPackageNum(singlePrintHandoverList.size());
        summaryPrintResult.setTotalBoardVolume(totalBoardVolume);
        summaryPrintResult.setTotalOutVolumeDynamic(totalOutVolumeDy);
        summaryPrintResult.setTotalOutVolumeStatic(totalOutVolumeSt);
        summaryPrintResult.setTotalInVolume(totalInVolume);

        summaryPrintResult.setDetails(details);

        return summaryPrintResult;
    }

    /**
     * 解析参数
     */
    private SendM tosendM(PrintQueryCriteria criteria) {
        SendM nSendM = new SendM();
        nSendM.setCreateSiteCode(criteria.getSiteCode());
        nSendM.setReceiveSiteCode(criteria.getReceiveSiteCode());

        if (StringUtils.isNotEmpty(criteria.getSendCode())) {
            nSendM.setSendCode(criteria.getSendCode());
        }
        if (StringUtils.isNotEmpty(criteria.getBoxCode())) {
            nSendM.setBoxCode(criteria.getBoxCode());
        } else {
            nSendM.setOperateTime(DateHelper.parseDateTime(criteria.getStartTime()));
            nSendM.setUpdateTime(DateHelper.parseDateTime(criteria.getEndTime()));
        }
        if (criteria.getSendUserCode() != null) {
            nSendM.setCreateUserCode(criteria.getSendUserCode());
        }
        return nSendM;
    }
    /**
     * 汇总多个发车批次统计信息,sendMList先按sendCode分组，然后统计每个分组的箱子、包裹信息
     *
     * @param sendMList
     * @param criteria
     * @return
     */
    public List<SummaryPrintResult> summaryPrintResultToList(List<SendM> sendMList, PrintQueryCriteria criteria) {
        List<SummaryPrintResult> res = new ArrayList<SummaryPrintResult>(8);
        if (sendMList == null || sendMList.isEmpty()) {
            return res;
        }
        //记录发车批次号和首条SendM映射关系
        Map<String, List<SendM>> sendMsMap = new HashMap<String, List<SendM>>();
        for (SendM sendM : sendMList) {
            String sendCode = sendM.getSendCode();
            if (StringHelper.isEmpty(sendCode)) {
                continue;
            }
            if (!sendMsMap.containsKey(sendCode)) {
                sendMsMap.put(sendCode, new ArrayList<SendM>(4));
            }
            sendMsMap.get(sendCode).add(sendM);
        }
        Map<String, Double> boardMap = getBoardValueMapByBoards(getBoardsFromSendMs(sendMList));
        if (!sendMsMap.isEmpty()) {
            for (String sendCode : sendMsMap.keySet()) {
                List<SendM> tmpList = sendMsMap.get(sendCode);
                res.add(summaryPrintResult(tmpList.get(0), tmpList, criteria, boardMap));
            }
        }
        return res;
    }

    /**
     * 汇总单个批次的统计信息
     * @param oriSendM
     * @param sendMList
     * @param criteria
     * @param boardMap
     * @return
     */
    public SummaryPrintResult summaryPrintResult(SendM oriSendM, List<SendM> sendMList, PrintQueryCriteria criteria, Map<String, Double> boardMap) {
        Date startDate = new Date();
        String roadCode = null;
        /**
         * 用于查询路区的运单数,增加获取路区成功的机会,查询运单接口为批量处理，性能影响不大
         */
        int waybillCodeNums = 5;
        /**
         * 用于保存当前批次用于查询路区号的运单号
         */
        List<String> waybillCodeList = new ArrayList<String>();
        /**
         * 用于记录waybillCodeList的个数
         */
        int cn = 0;
        log.debug("打印交接清单-summaryPrintQuery开始:{}" , DateHelper.formatDate(startDate));
        SummaryPrintResult result = new SummaryPrintResult();
        List<SummaryPrintBoxEntity> details = new ArrayList<SummaryPrintBoxEntity>();
        result.setSendCode(oriSendM.getSendCode());
        result.setReceiveSiteName(toSiteName(criteria.getReceiveSiteCode()));
        result.setSendSiteName(toSiteName(criteria.getSiteCode()));
        result.setSendTime(DateHelper.formatDateTime(oriSendM.getOperateTime()));
        log.debug("打印交接清单-批次汇总箱子数量:{}" , sendMList.size());

        Double totalBoardVolume = Constants.DOUBLE_ZERO;  //批次下板号的总体积
        Integer totalBoxNum = 0; //批次下合计箱个数
        Integer totalPackageNum = 0; //批次下合计原包个数
        Integer totalShouldSnedPackageNum = 0; //批次下合计应发包裹数
        Double totalOutVolumeDy = Constants.DOUBLE_ZERO; //批次下合计应付动态体积
        Double totalOutVolumeSt = Constants.DOUBLE_ZERO; //批次下合计应付静态体积
        Double totalInVolume = Constants.DOUBLE_ZERO; //批次下合计应收体积


        Set<String> dealedBoardCodes = new HashSet<String>();
        for (SendM sendM : sendMList) {
            Date startDate1 = new Date();
            log.debug("打印交接清单-批次单独批次开始:{}" , DateHelper.formatDate(startDate1));
            Set<String> packageBarcodeSet = new HashSet<String>();
            Set<String> waybillCodeSet = new HashSet<String>();
            SendDetail tSendDetail = new SendDetail();
            tSendDetail.setCreateSiteCode(sendM.getCreateSiteCode());
            tSendDetail.setBoxCode(sendM.getBoxCode());
            tSendDetail.setReceiveSiteCode(sendM.getReceiveSiteCode());
            tSendDetail.setSendCode(sendM.getSendCode());
            tSendDetail.setIsCancel(0);
            log.debug("打印交接清单-批次汇总箱子信息:{}" , sendM.getBoxCode());
            List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDetail);
            sendDetails = selectUniquesSendDetails(sendDetails);//create by wuzuxiang 2016年11月24日 T单、原单去重
//		    if(sendDetails!=null && !sendDetails.isEmpty()){ 使打印交接汇总清单时带出空箱，之前不打印空箱
            for (SendDetail sendDetail : sendDetails) {
                String packageBarcode = sendDetail.getPackageBarcode();
                if (criteria.getPackageBarcode() != null && !"".equals(criteria.getPackageBarcode()) &&
                        !criteria.getPackageBarcode().equals(packageBarcode)) {
                    continue;
                }
                String waybillCode = sendDetail.getWaybillCode();
                packageBarcodeSet.add(packageBarcode);
                waybillCodeSet.add(waybillCode);
                //取第一条运单号，用于获取本批次的路区号
                if (++cn <= waybillCodeNums) {
                    waybillCodeList.add(waybillCode);
                }
            }

            //批次内合计应发包裹数
            totalShouldSnedPackageNum  = totalShouldSnedPackageNum + packageBarcodeSet.size();

            SummaryPrintBoxEntity detail = new SummaryPrintBoxEntity();
            detail.setBoxCode(sendM.getBoxCode());
            detail.setPackageBarNum(packageBarcodeSet.size());
            detail.setPackageBarRecNum(packageBarcodeSet.size());
            detail.setWaybillNum(waybillCodeSet.size());
            Double boxOrPackVolume = 0.0;
            if (BusinessHelper.isBoxcode(sendM.getBoxCode())) {
                totalBoxNum ++ ;
                Box box = null;
                try {
                    box = boxService.findBoxByCode(sendM.getBoxCode());
                } catch (Exception e) {
                    log.error("打印交接清单获取箱号失败：{}",JsonHelper.toJson(sendM), e);
                }
                if (null != box && null != box.getLength() && null != box.getWidth() && null != box.getHeight()
                        && box.getLength() > 0 && box.getWidth() > 0 && box.getHeight() > 0) {
                    boxOrPackVolume = Double.valueOf(box.getLength() * box.getWidth() * box.getHeight());
                }
                SealBox tSealBox = this.tSealBoxService.findByBoxCode(sendM.getBoxCode());
                if (tSealBox != null) {
                    detail.setSealNo1(tSealBox.getCode());
                    detail.setSealNo2("");
                    detail.setLockTime(DateHelper.formatDateTime(tSealBox.getCreateTime()));
                } else {
                    detail.setSealNo1("");
                    detail.setSealNo2("");
                }
            } else {
                if (WaybillUtil.isPackageCode(sendM.getBoxCode())) {
                    totalPackageNum ++;
                    PackOpeFlowDto packOpeFlowDto = getOpeByPackageCode(sendM.getBoxCode());
                    if (null != packOpeFlowDto && null != packOpeFlowDto.getpLength() && null != packOpeFlowDto.getpWidth() && null != packOpeFlowDto.getpHigh()
                            && packOpeFlowDto.getpLength() > 0 && packOpeFlowDto.getpWidth() > 0 && packOpeFlowDto.getpHigh() > 0) {
                        boxOrPackVolume = packOpeFlowDto.getpLength() * packOpeFlowDto.getpWidth() * packOpeFlowDto.getpHigh();
                    }
                }
                detail.setSealNo1("");
                detail.setSealNo2("");
            }

            detail.setVolume(boxOrPackVolume);

            totalInVolume += boxOrPackVolume;

            /**
             * 设置应付体积：
             * 如果板号不为空，看是否有按板测量的体积
             * 如果板号为空，取按箱测量的体积
             */
            if(StringUtils.isNotBlank(sendM.getBoardCode()) && boardMap.containsKey(sendM.getBoardCode()) && !dealedBoardCodes.contains(sendM.getBoardCode())){
                dealedBoardCodes.add(sendM.getBoardCode());
                totalBoardVolume = totalBoardVolume + boardMap.get(sendM.getBoardCode());
                totalOutVolumeSt += boardMap.get(sendM.getBoardCode());

            } else {
                DmsOutWeightAndVolume weightAndVolume = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(sendM.getBoxCode(),criteria.getSiteCode());
                if(weightAndVolume != null){
                    //立方厘米转立方米
                    Double volume = BigDecimalHelper.div(weightAndVolume.getVolume(), PARAM_CM3_M3, 6);
                    if(weightAndVolume.getOperateType().equals(DmsOutWeightAndVolume.OPERATE_TYPE_STATIC)){
                        totalOutVolumeSt += volume;
                    }else{
                        totalOutVolumeDy += volume;
                    }
                }
            }

            details.add(detail);

//		    }
            Date endDate1 = new Date();
            log.debug("打印交接清单-批次单独批次结束-{}" , (startDate1.getTime() - endDate1.getTime()));
        }
        /**
         * 加载本批次的路区信息
         */
        if (cn > 0) {
            HashMap<String, BigWaybillDto> waybillInfos = new HashMap<String, BigWaybillDto>();
            sendToWaybill(waybillInfos, waybillCodeList, false);
            for (String waybillCode : waybillCodeList) {
                BigWaybillDto v = waybillInfos.get(waybillCode);
                if (v != null && v.getWaybill() != null && StringHelper.isNotEmpty(v.getWaybill().getRoadCode())) {
                    roadCode = v.getWaybill().getRoadCode();
                    break;
                }
            }
        }
        result.setRoadCode(roadCode);
        result.setDetails(details);
        result.setTotalBoardVolume(totalBoardVolume.doubleValue() > Constants.DOUBLE_ZERO ? totalBoardVolume : null);
        result.setTotalBoxNum(totalBoxNum);
        result.setTotalPackageNum(totalPackageNum);
        result.setTotalBoxAndPackageNum(totalBoxNum + totalPackageNum);
        result.setTotalShouldSendPackageNum(totalShouldSnedPackageNum);
        result.setTotalRealSendPackageNum(totalShouldSnedPackageNum);
        result.setTotalOutVolumeDynamic(totalOutVolumeDy);
        result.setTotalOutVolumeStatic(totalOutVolumeSt);
        result.setTotalInVolume(totalInVolume);

        Date endDate = new Date();
        log.debug("打印交接清单-summaryPrintQuery结束-{}" , (startDate.getTime() - endDate.getTime()));
        return result;
    }

    private PackOpeFlowDto getOpeByPackageCode(String packageCode) {
        try {
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            BaseEntity<List<PackOpeFlowDto>> packageOpe = waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
            for (PackOpeFlowDto packOpeFlowDto : packageOpe.getData()) {
                if (packOpeFlowDto.getPackageCode().equals(packageCode)) {
                    return packOpeFlowDto;
                }
            }
        } catch (Exception e) {
            log.error("获取包裹量方信息接口失败，{}",packageCode, e);
        }
        return null;
    }

    /**
     * 获取sendM的板号并去重
     * @param sendMs
     * @return
     */
    private List<String> getBoardsFromSendMs(List<SendM> sendMs){
        Set<String> boardSet = new HashSet<String>();
        for (SendM sendM : sendMs) {
            if(StringUtils.isNotBlank(sendM.getBoardCode())){
                boardSet.add(sendM.getBoardCode());
            }
        }
        List<String> boardList = null;
        if(!boardSet.isEmpty()){
            boardList = new ArrayList<String>(boardSet);
        }
        return boardList;
    }

    /**
     * 获取板号-体积的map
     * @param boardCodeList 板号集合
     * @return
     */
    private Map<String, Double> getBoardValueMapByBoards(List<String> boardCodeList){
        Map<String, Double> boardMap = new HashMap<String, Double>();
        if(boardCodeList != null && !boardCodeList.isEmpty()){
            try{
                List<BoardMeasureDto> boards = boardCombinationService.getBoardVolumeByBoardCode(boardCodeList);
                if(boards != null && !boards.isEmpty()){
                    for(BoardMeasureDto board : boards){
                        if(NumberHelper.gt0(board.getVolume().doubleValue())){
                            //立方厘米转立方米
                            double volume = board.getVolume()/PARAM_CM3_M3;
                            boardMap.put(board.getBoardCode(), NumberHelper.doubleFormat(volume));
                        }
                    }
                }
            }catch (Exception e){
                log.error("发货清单打印查询托盘体积异常，不再打印托盘体积。板号数据：{}" , boardCodeList.toString(), e);
            }
        }
        return boardMap;
    }

    private Map<String, Double> getAllGoodVolume(List<String> waybillCodes) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.detailPrintQuery.getAllGoodVolume", false, true);
        Map<String, Double> goodVolumeMap = new HashMap<>();
        if (waybillCodes != null && !waybillCodes.isEmpty()) {
            for (String waybillCode : waybillCodes) {
                try {
                    BaseEntity<List<PackOpeFlowDto>> packageOpe = waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
                    if (packageOpe != null && packageOpe.getData() != null) {
                        for (PackOpeFlowDto packOpeFlowDto : packageOpe.getData()) {
                            if (null != packOpeFlowDto && null != packOpeFlowDto.getpLength() && null != packOpeFlowDto.getpWidth() && null != packOpeFlowDto.getpHigh()
                                    && packOpeFlowDto.getpLength() > 0 && packOpeFlowDto.getpWidth() > 0 && packOpeFlowDto.getpHigh() > 0) {
                                goodVolumeMap.put(packOpeFlowDto.getPackageCode(), packOpeFlowDto.getpLength() * packOpeFlowDto.getpWidth() * packOpeFlowDto.getpHigh());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("获取包裹量方信息接口失败，waybillCodes={}",waybillCode, e);
                }
            }
        }
        Profiler.registerInfoEnd(callerInfo);
        return goodVolumeMap;
    }

    /**
     * 根据运单号分页获取运单数据
     *
     * @param waybillCodes
     * @return
     */
    private HashMap<String, BigWaybillDto> getBigWaybillDtoMap(List<String> waybillCodes, Boolean isPackList) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.detailPrintQuery.getBigWaybillDtoMap", false, true);
        HashMap<String, BigWaybillDto> deliveryPackageMap = new HashMap<String, BigWaybillDto>();
        if (waybillCodes != null && !waybillCodes.isEmpty()) {
            int n = waybillCodes.size() / QUERY_WAYBILL_SIZE;
            int m = waybillCodes.size() % QUERY_WAYBILL_SIZE;
            if (n > 0) {
                List<String> waybills = new ArrayList<String>();
                int num = 0;
                for (String code : waybillCodes) {
                    num++;
                    waybills.add(code);
                    if (num / QUERY_WAYBILL_SIZE > 0 && num % QUERY_WAYBILL_SIZE == 0 && n > 0) {
                        sendToWaybill(deliveryPackageMap, waybills, isPackList);
                        waybills = new ArrayList<>();
                        n--;
                    } else if (n == 0 && m > 0 && waybillCodes.size() == num) {
                        sendToWaybill(deliveryPackageMap, waybills, isPackList);
                    }
                }
            } else {
                sendToWaybill(deliveryPackageMap, waybillCodes, isPackList);
            }
        }
        Profiler.registerInfoEnd(callerInfo);
        return deliveryPackageMap;
    }

    /**
     * 调用取件单接口获取取件单信息
     *
     * @param dBasicQueryEntity
     * @return
     */
    private String buildPickUpParams(BasicQueryEntity dBasicQueryEntity) {
        String message = null;
        if (dBasicQueryEntity.getInvoice() != null) {
            try {
                BaseEntity<PickupTask> tPickupTask = waybillPickupTaskApi.getPickTaskByPickCode(dBasicQueryEntity.getInvoice());
                if (tPickupTask != null && tPickupTask.getResultCode() > 0) {
                    PickupTask mPickupTask = tPickupTask.getData();
                    if (mPickupTask != null) {
                        dBasicQueryEntity.setInvoice(mPickupTask.getInvoiceId());
                        if (mPickupTask.getNewWaybillCode() != null) {
                            dBasicQueryEntity.setIsnew(SendPrintConstants.TEXT_YES);
                        }
                    }
                } else {
                    message = "打印交接清单-取件单基础信息调用失败，运单号：" + dBasicQueryEntity.getWaybill() + "；取件单接口获取取件单信息结果：" + JsonHelper.toJson(tPickupTask);
                    log.warn(message);
                }
            } catch (Exception e) {
                message = "打印交接清单-取件单基础信息调用发生异常" + dBasicQueryEntity.getWaybill();
                log.error(message, e);
            }
        }
        return message;
    }

    /**
     * @param tBasicQueryEntity
     * @param sendDetail
     * @param sendM
     * @param criteria
     */
    private void buildBasicQueryEntity(BasicQueryEntity tBasicQueryEntity, SendDetail sendDetail, SendM sendM, PrintQueryCriteria criteria) {
        tBasicQueryEntity.setBoxCode(sendM.getBoxCode());
        if (sendDetail.getIsCancel() != null && sendDetail.getIsCancel() == 0) {
            tBasicQueryEntity.setIscancel(SendPrintConstants.TEXT_NO);
        } else {
            tBasicQueryEntity.setIscancel(SendPrintConstants.TEXT_YES);
        }
        tBasicQueryEntity.setIsnew(SendPrintConstants.TEXT_NO);
        tBasicQueryEntity.setPackageBarWeight(0.0);
        tBasicQueryEntity.setPackageBarWeight2(0.0);
        tBasicQueryEntity.setSiteCode(0);
        tBasicQueryEntity.setFcNo(0);
        tBasicQueryEntity.setPackageBar(sendDetail.getPackageBarcode());
        tBasicQueryEntity.setReceiveSiteCode(sendM.getReceiveSiteCode());
        tBasicQueryEntity.setSendCode(sendM.getSendCode());
        tBasicQueryEntity.setSendUser(sendM.getCreateUser());
        tBasicQueryEntity.setSendUserCode(sendM.getCreateUserCode());
        tBasicQueryEntity.setWaybill(sendDetail.getWaybillCode());
        tBasicQueryEntity.setInvoice(sendDetail.getPickupCode());
        tBasicQueryEntity.setBoxCode(sendM.getBoxCode());
    }

    /**
     * 根据运单信息构建打印对象
     *
     * @param dBasicQueryEntity
     * @param data
     * @param goodVolumeMap
     * @param rSiteType
     */
    private void buildWaybillAttributes(BasicQueryEntity dBasicQueryEntity, BigWaybillDto data, Map<String, Double> goodVolumeMap, Integer rSiteType) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.detailPrintQuery.buildWaybillAttributes", false, true);
        Waybill waybill = data.getWaybill();
        WaybillManageDomain waybillState = data.getWaybillState();
        List<DeliveryPackageD> deliveryPackage = data.getPackageList();

        dBasicQueryEntity.setDeclaredValue(waybill.getCodMoney());
        dBasicQueryEntity.setGoodValue(waybill.getPrice() == null ? SendPrintConstants.TEXT_DEFAULT_PRICE : waybill.getPrice());
        dBasicQueryEntity.setGoodWeight(waybill.getGoodWeight() == null ? 0.0 : waybill.getGoodWeight());
        dBasicQueryEntity.setGoodWeight2(waybill.getAgainWeight() == null ? 0.0 : waybill.getAgainWeight());
        dBasicQueryEntity.setPackageBarNum(waybill.getGoodNumber() == null ? 0 : waybill.getGoodNumber());
        dBasicQueryEntity.setWaybillType(waybill.getWaybillType() == null ? SendPrintConstants.TEXT_GENERAL_ORDER : getWaybillType(waybill.getWaybillType()));
        dBasicQueryEntity.setReceiverName(waybill.getReceiverName());

        Integer oldSiteId = waybill.getOldSiteId();
        if (oldSiteId != null) {
            dBasicQueryEntity.setSiteCode(oldSiteId);
            BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(oldSiteId);
            if (bDto != null) {
                dBasicQueryEntity.setSiteName(bDto.getSiteName());
                Integer siteType = bDto.getSiteType();
                if (siteType != null && !siteType.equals(16)) {
                    dBasicQueryEntity.setSiteType(SendPrintConstants.TEXT_SELF_SUPPORT);
                }
            }
        }

        if (deliveryPackage != null && !deliveryPackage.isEmpty() && BusinessHelper.checkIntNumRange(deliveryPackage.size())) {
            for (DeliveryPackageD delivery : deliveryPackage) {
                if (delivery.getPackageBarcode().equals(dBasicQueryEntity.getPackageBar())) {
                    dBasicQueryEntity.setPackageBarWeight(delivery.getGoodWeight());
                    dBasicQueryEntity.setPackageBarWeight2(delivery.getAgainWeight());
                    Double goodVolume = goodVolumeMap.get(dBasicQueryEntity.getPackageBar());
                    if (goodVolume != null) {
                        dBasicQueryEntity.setGoodVolume(goodVolume);
                    } else {
                        dBasicQueryEntity.setGoodVolume(0.0);
                    }
                    break;
                }
            }
        }

        if (waybillState != null && waybillState.getStoreId() != null) {
            dBasicQueryEntity.setFcNo(waybillState.getStoreId());
        }

        if (rSiteType.equals(16)) {
            dBasicQueryEntity.setReceiverAddress(waybill.getReceiverAddress() == null ? "" : waybill.getReceiverAddress());
            if (waybill.getReceiverMobile() == null && waybill.getReceiverTel() == null) {
                dBasicQueryEntity.setReceiverMobile(SendPrintConstants.TEXT_DOUBLE_BAR);
            } else {
                dBasicQueryEntity.setReceiverMobile("");
            }
        } else {
            dBasicQueryEntity.setReceiverMobile(SendPrintConstants.TEXT_DOUBLE_BAR);
        }

        Integer payment = waybill.getPayment();
        if (payment == null) {
            dBasicQueryEntity.setPayment(0);
            dBasicQueryEntity.setSendPay("");
        } else {
            dBasicQueryEntity.setPayment(payment);
            dBasicQueryEntity.setSendPay(getSendPay(payment));
            if (payment != 1 && payment != 3) {
                dBasicQueryEntity.setDeclaredValue(SendPrintConstants.TEXT_ZERO);
            }
        }

        String sendPay = waybill.getSendPay();
        //是否是奢侈品
        if (sendPay != null && sendPay.charAt(19) == '1') {
            dBasicQueryEntity.setLuxury(SendPrintConstants.TEXT_YES);
        } else {
            dBasicQueryEntity.setLuxury(SendPrintConstants.TEXT_NO);
        }
        Profiler.registerInfoEnd(callerInfo);
    }

    /**
     * 明细打印
     */
    public BasicQueryEntityResponse detailPrintQuery(List<SendM> sendMs, PrintQueryCriteria criteria) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.detailPrintQuery", false, true);
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        List<BasicQueryEntity> fzList = new ArrayList<BasicQueryEntity>();
        String rSiteName = toSiteName(criteria.getReceiveSiteCode());
        String fSiteName = toSiteName(criteria.getSiteCode());
        Integer rSiteType = toSiteType(criteria.getReceiveSiteCode());
        Map<String, Double> boardMap = getBoardValueMapByBoards(getBoardsFromSendMs(sendMs));
        String message = JdResponse.MESSAGE_OK;
        for (SendM sendM : sendMs) {
            /** 设置应付体积 **/
            Double outVolumeDynamic = 0.0;
            Double outVolumeStatic = 0.0;

            CallerInfo info = null;
            try {
                info = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.detailPrintQuery.getOutVolume",Constants.UMP_APP_NAME_DMSWEB,false, true);
                DmsOutWeightAndVolume weightAndVolume = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(sendM.getBoxCode(), sendM.getCreateSiteCode());
                if (weightAndVolume != null) {
                    Double volume = weightAndVolume.getVolume();
                    //将cm³转换成m³
                    volume = BigDecimalHelper.div(volume, PARAM_CM3_M3, 6);
                    if (weightAndVolume.getOperateType().equals(DmsOutWeightAndVolume.OPERATE_TYPE_STATIC)) {
                        outVolumeStatic = volume;
                    } else {
                        outVolumeDynamic = volume;
                    }
                }
            }catch(Exception e){
                log.error("发货交接清单打印-明细打印-获取出分拣中心体积异常.sendM={}", JsonHelper.toJson(sendM), e);
                Profiler.functionError(info);
            }finally{
                Profiler.registerInfoEnd(info);
            }

            CallerInfo innerCallerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.detailPrintQuery.buildSingleBySendM", false, true);
            List<BasicQueryEntity> tList = new ArrayList<BasicQueryEntity>();
            List<BasicQueryEntity> mList = new ArrayList<BasicQueryEntity>();
            String sealNo = "";
            if (BusinessHelper.isBoxcode(sendM.getBoxCode())) {
                SealBox tSealBox = this.tSealBoxService.findByBoxCode(sendM.getBoxCode());
                if (tSealBox != null) {
                    sealNo = tSealBox.getCode();
                }
            }

            List<SendDetail> sendDetails = this.getUniquesSendDetails(sendM.getCreateSiteCode(), sendM.getReceiveSiteCode(), sendM.getBoxCode());
            if (sendDetails != null && !sendDetails.isEmpty()) {
                Set<String> waybillCodesSet = new HashSet<String>();
                try {
                    for (SendDetail dSendDetail : sendDetails) {
                        if (criteria.getPackageBarcode() == null || "".equals(criteria.getPackageBarcode()) || criteria.getPackageBarcode().equals(dSendDetail.getPackageBarcode())) {
                            if (dSendDetail.getWaybillCode() != null && !dSendDetail.getWaybillCode().isEmpty()) {
                                waybillCodesSet.add(dSendDetail.getWaybillCode());
                            }
                            BasicQueryEntity tBasicQueryEntity = new BasicQueryEntity();
                            tBasicQueryEntity.setReceiveSiteName(rSiteName);
                            tBasicQueryEntity.setReceiveSiteType(rSiteType);
                            tBasicQueryEntity.setSendSiteName(fSiteName);
                            tBasicQueryEntity.setSealNo(sealNo);
                            tBasicQueryEntity.setDmsOutVolumeDynamic(outVolumeDynamic);
                            tBasicQueryEntity.setDmsOutVolumeStatic(outVolumeStatic);
                            this.buildBasicQueryEntity(tBasicQueryEntity, dSendDetail, sendM, criteria);
                            tList.add(tBasicQueryEntity);
                        }
                    }

                    List<String> waybillCodes = new ArrayList<String>(waybillCodesSet);

                    HashMap<String, BigWaybillDto> waybillDtoMap = this.getBigWaybillDtoMap(waybillCodes, true);

                    Map<String, Double> goodVolumeMap = this.getAllGoodVolume(waybillCodes);

                    for (BasicQueryEntity dBasicQueryEntity : tList) {
                        if (rSiteType != null && rSiteType.equals(ASM_TYPE)) {
                            String callbackMsg = this.buildPickUpParams(dBasicQueryEntity);
                            if (callbackMsg != null)
                                message = callbackMsg;
                        }
                        // 判断运单号是否为空
                        if (StringUtils.isEmpty(dBasicQueryEntity.getWaybill())) {
                            log.warn("打印交接清单-如果运单号为空直接加入list返回");
                            continue;
                        }

                        BigWaybillDto data = waybillDtoMap.get(dBasicQueryEntity.getWaybill());
                        if (data == null || data.getWaybill() == null) {
                            continue;
                        }

                        this.buildWaybillAttributes(dBasicQueryEntity, data, goodVolumeMap, rSiteType);

                        WaybillManageDomain waybillState = data.getWaybillState();
                        if (waybillState != null && waybillState.getStoreId() != null) {
                            if (criteria.getFc() != null && !criteria.getFc().equals(0) && !criteria.getFc().equals(waybillState.getStoreId())) {
                                mList.add(dBasicQueryEntity);
                            }
                        }

                        String sendPay = data.getWaybill().getSendPay();
                        if (criteria.isIs211() &&
                                (sendPay != null && !"1".equals(sendPay.substring(0, 1)))) {
                            mList.add(dBasicQueryEntity);
                        }
                    }
                    if (mList != null && !mList.isEmpty()) {
                        for (BasicQueryEntity dBasicQueryEntity : mList) {
                            tList.remove(dBasicQueryEntity);
                        }
                    }
                } catch (Exception e) {
                    message = "同步运单基本信息异常错误原因为" + e.getMessage();
                    log.error(message, e);
                }
            }
            if (tList != null && !tList.isEmpty()) {
                for (BasicQueryEntity tBasicQueryEntity : tList) {
                    if (StringUtils.isNotBlank(sendM.getBoardCode())) {
                        tBasicQueryEntity.setBoardCode(sendM.getBoardCode());
                        tBasicQueryEntity.setBoardVolume(boardMap.get(sendM.getBoardCode()));
                        //板体积不为null或0 应付静态量方取板体积
                        if(tBasicQueryEntity.getBoardVolume()  != null && NumberHelper.gt0(tBasicQueryEntity.getBoardVolume())){
                            tBasicQueryEntity.setDmsOutVolumeStatic(tBasicQueryEntity.getBoardVolume());
                        }
                    }
                    fzList.add(tBasicQueryEntity);
                }
            }
            Profiler.registerInfoEnd(innerCallerInfo);
        }
        tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK);
        tBasicQueryEntityResponse.setMessage(message);
        tBasicQueryEntityResponse.setData(fzList);
        Profiler.registerInfoEnd(callerInfo);
        return tBasicQueryEntityResponse;
    }

    /**
     * @param createSiteCode
     * @param receiveSiteCode
     * @param boxCode
     * @return
     */
    private List<SendDetail> getUniquesSendDetails(Integer createSiteCode, Integer receiveSiteCode, String boxCode) {
        SendDetail tSendDetail = new SendDetail();
        tSendDetail.setCreateSiteCode(createSiteCode);
        tSendDetail.setBoxCode(boxCode);
        tSendDetail.setReceiveSiteCode(receiveSiteCode);
        tSendDetail.setIsCancel(1);
        List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDetail);
        //create by wuzuxiang 2016年11月24日 T单、原单去重
        return selectUniquesSendDetails(sendDetails);
    }

    public String getWaybillType(int waybillType) {
        if (waybillType == 0) {
            return "一般订单";
        } else if (waybillType == 127) {
            return "奢侈品";
        } else if (waybillType == 6) {
            return "分期付款";
        } else if (waybillType == 17) {
            return "在线分期";
        } else if (waybillType == 2) {
            return "拍卖订单";
        } else if (waybillType == 4) {
            return "虚拟产品";
        } else if (waybillType == 7) {
            return "内部订单";
        } else if (waybillType == 8) {
            return "服务产品订单";
        } else if (waybillType == 9) {
            return "备件库-行政";
        } else if (waybillType == 10) {
            return "备件库-售后";
        } else if (waybillType == 11) {
            return "售后调货";
        } else if (waybillType == 12) {
            return "家电下乡";
        } else if (waybillType == 13) {
            return "企销部订单";
        } else if (waybillType == 15) {
            return "返修订单";
        } else if (waybillType == 16) {
            return "直接赔偿";
        } else if (waybillType == 18) {
            return "厂商直送";
        } else if (waybillType == 19) {
            return "客服补件";
        } else if (waybillType == 21 || waybillType == 22 || waybillType == 23 || waybillType == 24 || waybillType == 25) {
            return "POP订单";
        } else if (waybillType == 20) {
            return "以旧换新";
        } else if (waybillType == 28 || waybillType == 29) {
            return "团购订单";
        } else if (waybillType == 10000) {
            return "B商家订单";
        }
        return "一般订单";
    }

    public String getSendPay(int payment) {
        if (payment == 1) {
            return "货到付款";
        } else if (payment == 2) {
            return "邮局汇款";
        } else if (payment == 3) {
            return "上门自提";
        } else if (payment == 4) {
            return "在线支付";
        } else if (payment == 5) {
            return "公司转帐";
        } else if (payment == 6) {
            return "银行卡转帐";
        } else if (payment == 8) {
            return "分期付款";
        } else if (payment == 10) {
            return "高校代理-自己支付";
        } else if (payment == 11) {
            return "高校代理-代理垫付";
        } else if (payment == 12) {
            return "月结";
        }
        return "";
    }

    @Override
    public SendCodePrintEntity getSendCodePrintEntity(PrintQueryCriteria criteria) {
        SendCodePrintEntity entity = new SendCodePrintEntity();
        CrossPackageTagNew crossPackageTag = baseMinorManager.queryNonDmsSiteCrossPackageTagForPrint(criteria.getReceiveSiteCode(), criteria.getSiteCode());
        if (crossPackageTag != null) {
            entity.setDestinationCrossCode(crossPackageTag.getDestinationCrossCode());
            entity.setDestinationTabletrolleyCode(crossPackageTag.getDestinationTabletrolleyCode());
        }

        /* 读取flagText字段：1.是否生鲜批次，追加“鲜”字 */
        if (sendCodeService.isFreshSendCode(criteria.getSendCode())) {
            entity.setFlagText("鲜");
        }

        return entity;
    }

    @Override
    public List<PrintOnlineWaybillDTO> queryWaybillCountBySendCode(String sendCode, Integer createSiteCode) {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setCreateSiteCode(createSiteCode);
        sendDetail.setSendCode(sendCode);
        return sendDatailDao.queryWaybillCountBySendCode(sendDetail);
    }

    @Override
    public InvokeResult<Boolean> batchPrintExport(PrintExportCriteria printExportCriteria) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setMessage("发货交接清单excel稍后将发送至您咚咚，请注意查收!");
        boolean isGoESQuery = checkGoESQuery(printExportCriteria.getCreateSiteCode());
        if(!uccPropertyConfiguration.getNewPrintHandoverListSwitch() ){
            // 自定义编码10000表示：走老查询（非ES查询）
            result.setCode(BATCH_SUMMARY_QUERY_ES_CODE);
            result.setMessage("当前场地未开启新查询!（ES）");
            return result;
        }
        // 校验是否审批
        boolean isApproval = checkIsApproval(printExportCriteria);
        if(isApproval){
            result.setCode(THIRD_EXPORT_SUC_CODE);
            result.setMessage("目的地包含三方站点需上级领导审批，审批通过后发送至您咚咚，请注意查收!");
        }

        batchPrintExportASync(printExportCriteria, isApproval);

        return result;
    }

    @Override
    public InvokeResult<Boolean> batchPrintExportToTripartite(TripartiteEntity tripartiteEntity, String content, List<String> tos, List<String> ccs) {

        com.jd.dms.wb.report.api.dto.base.BaseEntity<Boolean>
                baseEntity = printHandoverListManager.doBatchExportAsyncToTripartite(createESQueryCondition(tripartiteEntity), content, tos, ccs);
        if(baseEntity != null && Objects.equals(baseEntity.getData(),true)){
            log.info("操作人【{}】始发地【{}】的交接清单导出成功!", tripartiteEntity.getErpCode(), tripartiteEntity.getSiteCode());
            return new InvokeResult<>(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE, Boolean.TRUE);
        }else {
            log.error("操作人【{}】始发地【{}】的交接清单导出失败!失败原因:{}", tripartiteEntity.getErpCode(),
                    tripartiteEntity.getSiteCode(), baseEntity == null ? Constants.EMPTY_FILL : baseEntity.getMessage());
            return new InvokeResult<>(InvokeResult.RESULT_PARAMETER_ERROR_CODE, baseEntity == null ? Constants.EMPTY_FILL : baseEntity.getMessage(), Boolean.FALSE);
        }
    }

    /**
     * 发货交接清单批量导出
     * @param printExportCriteria
     * @param isApproval
     */
    private void batchPrintExportASync(PrintExportCriteria printExportCriteria, boolean isApproval) {
        long startTime = System.currentTimeMillis();
        // 三方站点则接入审批
        if(isApproval){
            // OA数据
            Map<String,Object> oaMap = new HashMap<>();
            oaMap.put(FlowConstants.FLOW_OA_JMEREQNAME, FlowConstants.FLOW_FLOW_WORK_THEME_PRINT_HANDOVER);
            oaMap.put(FlowConstants.FLOW_OA_JMEREQCOMMENTS, FlowConstants.FLOW_FLOW_WORK_REMARK_PRINT_HANDOVER);
            List<String> mainColList = new ArrayList<>();
            oaMap.put(FlowConstants.FLOW_OA_JMEMAINCOLLIST,mainColList);
            PrintQueryCriteria printQueryCriteria = printExportCriteria.getList().get(0);
            mainColList.add("发货始发地:" + toSiteName(printExportCriteria.getCreateSiteCode()));
            mainColList.add("发货目的地:" + toSiteName(printQueryCriteria.getReceiveSiteCode()) + "...等等");
            mainColList.add("发货开始时间:" + printQueryCriteria.getStartTime());
            mainColList.add("发货结束时间:" + printQueryCriteria.getEndTime());

            // 业务数据
            Map<String,Object> businessMap = new HashMap<>();
            // 设置业务唯一编码
            businessMap.put(FlowConstants.FLOW_BUSINESS_NO_KEY,FlowConstants.FLOW_CODE_PRINT_HANDOVER + Constants.SEPARATOR_VERTICAL_LINE
                    + FlowConstants.FLOW_VERSION + Constants.SEPARATOR_VERTICAL_LINE + System.currentTimeMillis());
            // 设置业务的查询条件
            businessMap.put(FlowConstants.FLOW_BUSINESS_QUERY_CONDITION,JsonHelper.toJson(createESQueryCondition(printExportCriteria, false)));

            // 提交申请单
            String flowWorkNo = flowServiceManager.startFlow(oaMap, businessMap, null,
                    FlowConstants.FLOW_CODE_PRINT_HANDOVER, printExportCriteria.getUserCode(), String.valueOf(businessMap.get(FlowConstants.FLOW_BUSINESS_NO_KEY)));

            if(log.isInfoEnabled() && StringUtils.isNotEmpty(flowWorkNo)){
                log.info("打印交接清单导出已提交申请单【{}】业务唯一标识码【{}】", flowWorkNo, businessMap.get(FlowConstants.FLOW_BUSINESS_NO_KEY));
            }
            // 记录businessLog
            addBusinessLog(startTime, printExportCriteria, true, flowWorkNo);
            return;
        }
        // 记录businessLog
        addBusinessLog(startTime, printExportCriteria, false, null);
        com.jd.dms.wb.report.api.dto.base.BaseEntity<Boolean>
                baseEntity = printHandoverListManager.doBatchExportAsync(createESQueryCondition(printExportCriteria, isShowAddress(printExportCriteria.getList())));
        if(baseEntity != null && Objects.equals(baseEntity.getData(),true)){
            log.info("操作人【{}】始发地【{}】的交接清单导出成功!", printExportCriteria.getUserCode(), printExportCriteria.getCreateSiteCode());
        }else {
            log.error("操作人【{}】始发地【{}】的交接清单导出失败!失败原因:{}", printExportCriteria.getUserCode(),
                    printExportCriteria.getCreateSiteCode(), baseEntity == null ? Constants.EMPTY_FILL : baseEntity.getMessage());
        }
    }

    /**
     * 校验是否需要审批
     *  1、ucc开启 && 有三方目的地
     * @param printExportCriteria
     * @return
     */
    private boolean checkIsApproval(PrintExportCriteria printExportCriteria) {
        // 未开启审批
        if(!uccPropertyConfiguration.getApprovalSwitch()){
            return false;
        }
        return isShowAddress(printExportCriteria.getList());
    }

    /**
     * 是否显示目的地
     *  三方目的才显示客户地址和客户姓名
     *
     * @param receiveSiteCodeList
     * @return
     */
    private boolean isShowAddress(List<PrintQueryCriteria> receiveSiteCodeList) {
        for (PrintQueryCriteria item : receiveSiteCodeList) {
            if(Objects.equals(toSiteType(item.getReceiveSiteCode()), Constants.RETURN_PARTNER_SITE_TYPE)
                    && switchSiteSubTypeCheck(item.getReceiveSiteCode())){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否校验站点子类型为三方
     * @param receiveSiteCode
     * @return
     */
    private boolean switchSiteSubTypeCheck(Integer receiveSiteCode) {
        return uccPropertyConfiguration.getCheckSiteSubType()
                || Objects.equals(toSiteSubType(receiveSiteCode), Constants.RETURN_PARTNER_SITE_TYPE);
    }

    private void addBusinessLog(long startTime, PrintExportCriteria printExportCriteria, boolean isTripartite, String flowWorkNo) {
        try {
            JSONObject logRequest = new JSONObject();
            logRequest.put("createSiteCode", printExportCriteria.getCreateSiteCode());
            logRequest.put("operatorCode", printExportCriteria.getUserCode());

            JSONObject logResponse = new JSONObject();
            logResponse.put("isTripartite", isTripartite);
            logResponse.put("flowWorkNo", flowWorkNo);

            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.APPROVAL_START_FLOW)
                    .methodName("SendPrintServiceImpl#batchPrintExportASync")
                    .operateRequest(logRequest)
                    .operateResponse(logResponse)
                    .processTime(System.currentTimeMillis(),startTime)
                    .build();
            logEngine.addLog(businessLogProfiler);
        }catch (Exception e){
            log.error("记录businessLog日志异常!", e);
        }

    }

    @Override
    public PrintHandoverListDto buildPrintHandoverListDto(SendDetail sendDetail) {

        // 获取运单对象
        String waybillCode = dealCreateWaybillCode(sendDetail);
        if(StringUtils.isEmpty(waybillCode)){
            log.warn("未获取到运单号!包裹号：{},站点：{}", sendDetail.getPackageBarcode(), sendDetail.getCreateSiteCode());
            return null;
        }
        BigWaybillDto bigWaybillDto = getBigWaybillDto(waybillCode);
        if(bigWaybillDto == null || bigWaybillDto.getWaybill() == null){
            log.warn("根据运单号{}获取运单信息为空!", waybillCode);
            return null;
        }
        // 获取发货sendM对象
        SendM sendM = getSendMDto(sendDetail);
        if(sendM == null){
            log.warn("根据始发{}目的{}箱号{}获取发货sendM为空!",sendDetail.getCreateSiteCode(),sendDetail.getReceiveSiteCode(),sendDetail.getBoxCode());
            return null;
        }

        // 构建打印接交接对象的基础属性
        PrintHandoverListDto printHandoverListDto = buildBasicData(sendDetail,sendM,waybillCode);

        // 构建打印接交接对象的运单属性
        buildWaybillData(bigWaybillDto,printHandoverListDto);

        // 构建打印接交接对象的包裹属性
        buildPackageData(printHandoverListDto);

        // 构建取件单信息
        buildPickupData(printHandoverListDto);

        // 构建打印交接清单对象的应付量方属性
        buildDmsOutWeightData(printHandoverListDto);

        // 构建打印交接清单对象的板属性
        buildBoardVolumeData(printHandoverListDto,sendM);

        // 构建打印交接清单对象的封车数据
        buildSealCarData(printHandoverListDto);

        return printHandoverListDto;
    }

    /**
     * 获取运单号
     * @param sendDetail
     * @return
     */
    private String dealCreateWaybillCode(SendDetail sendDetail) {
        // 运单号则直接返回
        if(WaybillUtil.isWaybillCode(sendDetail.getWaybillCode())){
            return sendDetail.getWaybillCode();
        }
        String packageBarcode = sendDetail.getPackageBarcode();
        // 从包裹号中获取运单号
        if(WaybillUtil.isPackageCode(packageBarcode)){
            return WaybillUtil.getWaybillCode(packageBarcode);
        }
        // 包裹号是备件条码
        if(WaybillUtil.isReverseSpareCode(packageBarcode)){
            try {
                ReverseSpare reverseSpare = reverseSpareService.queryBySpareCode(packageBarcode);
                if(reverseSpare != null){
                    return reverseSpare.getWaybillCode();
                }
            }catch (Exception e){
                log.error("根据备件条码查询运{}单号异常!", packageBarcode);
            }
        }
        return null;
    }

    /**
     * 获取运单数据
     * @param waybillCode
     * @return
     */
    private BigWaybillDto getBigWaybillDto(String waybillCode) {
        BaseEntity<BigWaybillDto> baseEntity
                = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
        return baseEntity == null ? null : baseEntity.getData();
    }

    /**
     * 获取发货sendM记录
     * @param sendDetail
     * @return
     */
    private SendM getSendMDto(SendDetail sendDetail) {
        SendM queryParams = new SendM();
        queryParams.setCreateSiteCode(sendDetail.getCreateSiteCode());
        queryParams.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        queryParams.setBoxCode(sendDetail.getBoxCode());
        queryParams.setSendCode(sendDetail.getSendCode());
        List<SendM> sendMList = sendMService.findByParams(queryParams);
        return CollectionUtils.isEmpty(sendMList) ? null : sendMList.get(0);
    }

    /**
     * 构建打印接交接对象的基础属性
     * @param sendDetail
     * @param sendM
     * @param waybillCode
     * @return
     */
    private PrintHandoverListDto buildBasicData(SendDetail sendDetail,SendM sendM,String waybillCode) {
        PrintHandoverListDto printHandoverListDto = new PrintHandoverListDto();
        printHandoverListDto.setCreateSiteCode(sendDetail.getCreateSiteCode());
        printHandoverListDto.setCreateSiteName(toSiteName(sendDetail.getCreateSiteCode()));
        printHandoverListDto.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        printHandoverListDto.setReceiveSiteName(toSiteName(sendDetail.getReceiveSiteCode()));
        printHandoverListDto.setReceiveSiteType(toSiteType(sendDetail.getReceiveSiteCode()));

        printHandoverListDto.setSendCode(sendDetail.getSendCode());
        printHandoverListDto.setBoxCode(sendDetail.getBoxCode());
        printHandoverListDto.setWaybillCode(waybillCode);
        printHandoverListDto.setPackageCode(sendDetail.getPackageBarcode());

        printHandoverListDto.setSendUser(sendM.getCreateUser());
        printHandoverListDto.setSendUserCode(sendM.getCreateUserCode());

        printHandoverListDto.setIsCancel((Byte.valueOf(String.valueOf(Constants.NUMBER_ZERO))));
        printHandoverListDto.setIsNew(Byte.valueOf(String.valueOf(Constants.NUMBER_ZERO)));

        printHandoverListDto.setSendTime(sendM.getOperateTime() == null ? null : sendM.getOperateTime().getTime());

        printHandoverListDto.setPackageWeight(Constants.DOUBLE_ZERO);
        printHandoverListDto.setPackageAgainWeight(Constants.DOUBLE_ZERO);
        printHandoverListDto.setSiteCode(Constants.NUMBER_ZERO);
        printHandoverListDto.setFcNo(Constants.NUMBER_ZERO);
        // 包裹还是箱号
        printHandoverListDto.setIsPack(BusinessUtil.isBoxcode(sendDetail.getBoxCode())
                ? Byte.parseByte(String.valueOf(Constants.NUMBER_ZERO)) : (byte)Constants.CONSTANT_NUMBER_ONE);

        return printHandoverListDto;
    }

    /**
     * 构建打印接交接对象的运单属性
     * @param data
     * @param printHandoverListDto
     */
    private void buildWaybillData(BigWaybillDto data, PrintHandoverListDto printHandoverListDto) {

        Waybill waybill = data.getWaybill();

        printHandoverListDto.setDeclaredValue(waybill.getCodMoney() == null ? Constants.DOUBLE_ZERO : Double.valueOf(waybill.getCodMoney()));
        printHandoverListDto.setGoodValue(waybill.getPrice() == null ? Constants.DOUBLE_ZERO : Double.valueOf(waybill.getPrice()));
        printHandoverListDto.setGoodWeight(waybill.getGoodWeight() == null ? Constants.DOUBLE_ZERO : waybill.getGoodWeight());
        printHandoverListDto.setAgainWeight(waybill.getAgainWeight() == null ? Constants.DOUBLE_ZERO : waybill.getAgainWeight());
        printHandoverListDto.setPackageNum(waybill.getGoodNumber() == null ? Constants.CONSTANT_NUMBER_ONE : waybill.getGoodNumber());
        printHandoverListDto.setWaybillType(waybill.getWaybillType());
        printHandoverListDto.setRoadCode(waybill.getRoadCode());

        Integer oldSiteId = waybill.getOldSiteId();
        if (oldSiteId != null) {
            printHandoverListDto.setSiteCode(oldSiteId);
            BaseStaffSiteOrgDto bDto = baseMajorManager.getBaseSiteBySiteId(oldSiteId);
            printHandoverListDto.setSiteName(bDto == null ? null : bDto.getSiteName());
            printHandoverListDto.setSiteType(bDto == null ? null : bDto.getSiteType());
        }

        // 库房号
        printHandoverListDto.setFcNo((data.getWaybillState() == null || data.getWaybillState().getStoreId() == null)
                ? Constants.NUMBER_ZERO : data.getWaybillState().getStoreId());

        // 客户信息
        printHandoverListDto.setReceiverName(waybill.getReceiverName());
        printHandoverListDto.setReceiverAddress(waybill.getReceiverAddress() == null ? Constants.EMPTY_FILL : waybill.getReceiverAddress());
        printHandoverListDto.setReceiverMobile(StringUtils.isEmpty(waybill.getReceiverMobile())
                ? (StringUtils.isEmpty(waybill.getReceiverTel()) ? SendPrintConstants.TEXT_DOUBLE_BAR : waybill.getReceiverTel())
                : waybill.getReceiverMobile());

        // 支付方式
        Integer payment = waybill.getPayment();
        if (payment == null) {
            printHandoverListDto.setPayment(Constants.NUMBER_ZERO);
        } else {
            printHandoverListDto.setPayment(payment);
            if (payment != 1 && payment != 3) {
                printHandoverListDto.setDeclaredValue(Constants.DOUBLE_ZERO);
            }
        }

        // 是否是奢侈品、是否211时效订单
        if (BusinessUtil.isLuxury(waybill.getSendPay())) {
            printHandoverListDto.setLuxury(Byte.valueOf(String.valueOf(Constants.CONSTANT_NUMBER_ONE)));
        } else {
            printHandoverListDto.setLuxury(Byte.valueOf(String.valueOf(Constants.NUMBER_ZERO)));
        }
        if (BusinessHelper.is211(waybill.getSendPay())) {
            printHandoverListDto.setIs211(Byte.valueOf(String.valueOf(Constants.CONSTANT_NUMBER_ONE)));
        } else {
            printHandoverListDto.setIs211(Byte.valueOf(String.valueOf(Constants.NUMBER_ZERO)));
        }
    }

    /**
     * 构建打印接交接对象的包裹属性
     * @param printHandoverListDto
     */
    private void buildPackageData(PrintHandoverListDto printHandoverListDto) {
        String packageCode = printHandoverListDto.getPackageCode();
        if(!WaybillUtil.isPackageCode(packageCode)){
            return;
        }
        DeliveryPackageD deliveryPackage = getDeliveryPackageInfo(packageCode);
        if(deliveryPackage == null){
            log.warn("根据包裹号{}获取包裹明细为空!", packageCode);
            return;
        }
        // 包裹重量、包裹复重、包裹体积
        printHandoverListDto.setPackageWeight(deliveryPackage.getGoodWeight());
        printHandoverListDto.setPackageAgainWeight(deliveryPackage.getAgainWeight());
        printHandoverListDto.setGoodVolume(!NumberHelper.isBigDecimal(deliveryPackage.getGoodVolume()) ? Constants.DOUBLE_ZERO : Double.parseDouble(deliveryPackage.getGoodVolume()));
    }

    /**
     * 获取包裹明细数据
     *
     * @param packageCode
     * @return
     */
    private DeliveryPackageD getDeliveryPackageInfo(String packageCode) {
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.queryPackageListForParcodes(Collections.singletonList(packageCode));
        if(baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())){
            log.warn("根据包裹号{}查询包裹明细为空!", packageCode);
            return null;
        }
        return baseEntity.getData().get(0);
    }

    /**
     * 构建打印交接清单-取件单信息
     * @param printHandoverListDto
     */
    private void buildPickupData(PrintHandoverListDto printHandoverListDto) {
        if(StringUtils.isEmpty(printHandoverListDto.getInvoice())){
            return;
        }
        try {
            BaseEntity<PickupTask> baseEntity = waybillPickupTaskApi.getPickTaskByPickCode(printHandoverListDto.getInvoice());
            if(baseEntity != null && baseEntity.getData() != null){
                PickupTask pickupTask = baseEntity.getData();
                printHandoverListDto.setInvoice(pickupTask.getInvoiceId());
                if (StringUtils.isNotEmpty(pickupTask.getNewWaybillCode())) {
                    printHandoverListDto.setIsNew(Byte.valueOf(String.valueOf(Constants.CONSTANT_NUMBER_ONE)));
                }
            }
        }catch (Exception e){
            log.error("打印交接清单-根据{}获取件单基础信息调用发生异常",printHandoverListDto.getWaybillCode(),e);
        }

    }

    /**
     * 构建打印交接清单-应付量方属性
     *  1）、如果包裹装在箱里，按照箱操作过应付量方，该包裹的体积赋值为箱的体积
     *  2）、如果没有装在箱里，则赋值为包裹的应付体积
     * @param printHandoverListDto
     */
    private void buildDmsOutWeightData(PrintHandoverListDto printHandoverListDto) {
        Double outVolumeDynamic = Constants.DOUBLE_ZERO;
        Double outVolumeStatic = Constants.DOUBLE_ZERO;
        try {
            DmsOutWeightAndVolume weightAndVolume
                    = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(printHandoverListDto.getBoxCode(), printHandoverListDto.getCreateSiteCode());
            if(weightAndVolume == null && BusinessHelper.isBoxcode(printHandoverListDto.getBoxCode())){
                weightAndVolume = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(printHandoverListDto.getPackageCode(),printHandoverListDto.getCreateSiteCode());
            }
            if (weightAndVolume != null) {
                Double volume = weightAndVolume.getVolume();
                //将cm³转换成m³
                volume = BigDecimalHelper.div(volume, PARAM_CM3_M3, 6);
                if (weightAndVolume.getOperateType().equals(DmsOutWeightAndVolume.OPERATE_TYPE_STATIC)) {
                    outVolumeStatic = volume;
                } else {
                    outVolumeDynamic = volume;
                }
            }
        } catch (Exception e) {
            log.error("发货交接清单打印-明细打印-查询分拣中心[{}]操作对该箱号/包裹[{}]的体积.", printHandoverListDto.getBoxCode(), printHandoverListDto.getCreateSiteCode(), e);
        }
        printHandoverListDto.setDmsOutVolumeDynamic(outVolumeDynamic);
        printHandoverListDto.setDmsOutVolumeStatic(outVolumeStatic);
    }

    /**
     * 构建打印交接清单对象的板属性
     * @param printHandoverListDto
     * @param sendM
     */
    private void buildBoardVolumeData(PrintHandoverListDto printHandoverListDto,SendM sendM) {
        String boardCode = sendM.getBoardCode();
        if(StringUtils.isEmpty(boardCode)){
            return;
        }
        printHandoverListDto.setBoardCode(boardCode);
        // 板号对应的体积
        Map<String, Double> boardVolumeMap = getBoardValueMapByBoards(Collections.singletonList(boardCode));

        printHandoverListDto.setBoardVolume(boardVolumeMap.get(boardCode));
        //板体积不为null或0 应付静态量方取板体积
        if (printHandoverListDto.getBoardVolume() != null && NumberHelper.gt0(printHandoverListDto.getBoardVolume())) {
            printHandoverListDto.setDmsOutVolumeStatic(printHandoverListDto.getBoardVolume());
        }

    }

    /**
     * 构建打印交接清单对象的封车数据
     * @param printHandoverListDto
     */
    private void buildSealCarData(PrintHandoverListDto printHandoverListDto) {
        try {
            SealBox tSealBox = tSealBoxService.findByBoxCode(printHandoverListDto.getBoxCode());
            if (tSealBox != null) {
                printHandoverListDto.setSealNo(tSealBox.getCode());
                printHandoverListDto.setSealTime(tSealBox.getCreateTime() == null ? null : tSealBox.getCreateTime().getTime());
            }
        }catch (Exception e){
            log.error("根据箱号{}获取封车时间异常!",printHandoverListDto.getBoxCode());
        }
    }

    /**
     * 基本查询
     */
    @Override
    public BasicQueryEntityResponse basicPrintQuery(PrintQueryCriteria criteria) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.basicPrintQuery", Constants.UMP_APP_NAME_DMSWEB, false, true);
        Date startDate = new Date();
        log.debug("打印交接清单-基本信息查询开始:{}" , DateHelper.formatDate(startDate));
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        try {
            SendM qSendM = tosendM(criteria);
            List<SendM> sendMs = this.selectUniquesSendMs(qSendM);
            if (sendMs != null && !sendMs.isEmpty()) {
                tBasicQueryEntityResponse = detailPrintQuery(sendMs, criteria);
            }else {
                tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK_NULL);
                tBasicQueryEntityResponse.setMessage(JdResponse.MESSAGE_OK_NULL);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("打印明细基本查询异常:{}",JsonHelper.toJson(criteria), e);
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        } finally {
            Profiler.registerInfoEnd(info);
        }
        Date endDate = new Date();
        log.debug("打印交接清单-基本信息查询结束-{}" , (startDate.getTime() - endDate.getTime()));
        // 记录安全日志
        SecurityLogWriter.sendPrintBasicPrintQueryWrite(criteria, tBasicQueryEntityResponse, null);
        return tBasicQueryEntityResponse;
    }

    /**
     * 基本查询
     */
    @Override
    public BasicQueryEntityResponse basicPrintQueryForPage(PrintQueryCriteria criteria) {
        // 检查是否走es查询
        if(uccPropertyConfiguration.getNewPrintHandoverListSwitch()){
            return basicPrintQueryForPageByES(criteria);
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.basicPrintQueryForPage", Constants.UMP_APP_NAME_DMSWEB, false, true);
        long startTime = System.currentTimeMillis();
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        try {
            SendDetailDto queryParam = this.getSendDQueryParams(criteria);

            List<SendDetail> details = sendDetailService.findSendPageByParams(queryParam);
            if (details != null && !details.isEmpty()) {
                tBasicQueryEntityResponse = this.assembleDetailPrintQueryBySendD(details, criteria);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("打印明细基本查询异常:{}",JsonHelper.toJson(criteria), e);
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        } finally {
            Profiler.registerInfoEnd(info);
        }
        log.debug("打印交接清单-分页-基本信息查询结束-{}" , (startTime - System.currentTimeMillis()));
        // 记录安全日志
        SecurityLogWriter.sendPrintBasicPrintQueryWrite(criteria, tBasicQueryEntityResponse, null);
        return tBasicQueryEntityResponse;
    }

    /**
     * 检查是否走es查询
     *  1、字符串false表示不开启
     *  2、字符串true表示开启全国场地的es查询
     *  3、多个场地开启es查询以,分隔
     * @return
     */
    private boolean checkGoESQuery(Integer siteCode) {
        String printHandoverListSites;
        try {
            printHandoverListSites = uccPropertyConfiguration.getPrintHandoverListSites();
            if(Boolean.TRUE.toString().equals(printHandoverListSites)){
                return true;
            }else if(Boolean.FALSE.toString().equals(printHandoverListSites)){
                return false;
            }else {
                List<String> printHandoverListSiteList = Arrays.asList(printHandoverListSites.split(Constants.SEPARATOR_COMMA));
                return CollectionUtils.isNotEmpty(printHandoverListSiteList) && printHandoverListSiteList.contains(String.valueOf(siteCode));
            }
        }catch (Exception e){
            log.error("查询ucc配置异常!");
        }
        return false;
    }

    /**
     * 打印交接清单es查询
     * @param criteria
     * @return
     */
    private BasicQueryEntityResponse basicPrintQueryForPageByES(PrintQueryCriteria criteria) {
        long startTime = System.currentTimeMillis();
        BasicQueryEntityResponse basicQueryEntityResponse = new BasicQueryEntityResponse();
        try {
            PageData<PrintHandoverListDto> pagerResult
                    = printHandoverListManager.queryPrintHandOverListByQueryCondition(createESQueryCondition(criteria,false));
            if(pagerResult != null && CollectionUtils.isNotEmpty(pagerResult.getRecords())){
                List<BasicQueryEntity> basicQueryEntityList = new ArrayList<>();
                // 循环组装前台对象
                List<PrintHandoverListDto> printHandoverListDtos = pagerResult.getRecords();
                for (PrintHandoverListDto dto : printHandoverListDtos) {
                    basicQueryEntityList.add(convertToBasicQueryEntity(dto));
                }
                basicQueryEntityResponse.setCode(JdResponse.CODE_OK);
                basicQueryEntityResponse.setData(basicQueryEntityList);
                return basicQueryEntityResponse;
            }
            // 未查询到结果
            basicQueryEntityResponse.setCode(JdResponse.CODE_OK_NULL);
            basicQueryEntityResponse.setMessage(JdResponse.MESSAGE_OK_NULL);
        }catch (Exception e){
            log.error("根据条件{}查询打印交接清单异常!",JsonHelper.toJson(criteria),e);
            basicQueryEntityResponse.setCode(JdResponse.CODE_INTERNAL_ERROR);
            basicQueryEntityResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR_C);
        }
        if(log.isInfoEnabled()){
            log.info("发货交接清单分页查询耗时：{}", System.currentTimeMillis() - startTime);
        }
        return basicQueryEntityResponse;
    }

    /**
     * 构建es查询对象
     * @param criteria
     * @return
     */
    private Pager<PrintHandoverLitQueryCondition> createESQueryCondition(PrintQueryCriteria criteria, boolean sensitiveQuery) {
        Pager<PrintHandoverLitQueryCondition> pager = new Pager<PrintHandoverLitQueryCondition>();
        pager.setPageNo(criteria.getPageNo());
        pager.setPageSize(criteria.getPageSize());
        pager.setSearchVo(convertToPrintHandoverListQueryCondition(criteria,sensitiveQuery));
        return pager;
    }

    /**
     * 构建批量导出es查询条件
     * @param printExportCriteria
     * @return
     */
    private Pager<PrintHandoverLitQueryCondition> createESQueryCondition(PrintExportCriteria printExportCriteria, boolean isApproval) {
        Pager<PrintHandoverLitQueryCondition> pager = new Pager<PrintHandoverLitQueryCondition>();
        pager.setPageNo(Constants.CONSTANT_NUMBER_ONE);
        pager.setPageSize(uccPropertyConfiguration.getScrollQuerySize());
        Set<Integer> receiveSiteSet = new HashSet<>();
        for (PrintQueryCriteria item : printExportCriteria.getList()) {
            receiveSiteSet.add(item.getReceiveSiteCode());
        }
        PrintHandoverLitQueryCondition condition = convertToPrintHandoverListQueryCondition(printExportCriteria.getList().get(0), isApproval);
        condition.setReceiveSiteCodeList(new ArrayList<Integer>(receiveSiteSet));
        pager.setSearchVo(condition);
        return pager;
    }

    /**
     * 构建导出到三方人员的查询条件
     * @param tripartiteEntity
     * @return
     */
    private Pager<PrintHandoverLitQueryCondition> createESQueryCondition(TripartiteEntity tripartiteEntity) {
        Pager<PrintHandoverLitQueryCondition> pager = new Pager<PrintHandoverLitQueryCondition>();
        pager.setPageNo(Constants.CONSTANT_NUMBER_ONE);
        pager.setPageSize(uccPropertyConfiguration.getScrollQuerySize());
        Set<Integer> receiveSiteSet = new HashSet<>();
        for (PrintQueryCriteria item : tripartiteEntity.getList()) {
            receiveSiteSet.add(item.getReceiveSiteCode());
        }
        PrintHandoverLitQueryCondition condition = convertToPrintHandoverListQueryCondition(tripartiteEntity.getList().get(0), true);
        condition.setReceiveSiteCodeList(new ArrayList<Integer>(receiveSiteSet));
        pager.setSearchVo(condition);
        return pager;
    }

    private PrintHandoverLitQueryCondition convertToPrintHandoverListQueryCondition(PrintQueryCriteria criteria,boolean sensitiveQuery) {
        PrintHandoverLitQueryCondition condition = new PrintHandoverLitQueryCondition();
        condition.setCreateSiteCode(criteria.getSiteCode());
        condition.setReceiveSiteCode(criteria.getReceiveSiteCode());
        condition.setSendCode(criteria.getSendCode());
        condition.setBoxCode(criteria.getBoxCode());
        condition.setWaybillCode(criteria.getWaybillcode());
        condition.setPackageCode(criteria.getPackageBarcode());
//        condition.setFcNo(criteria.getFc());
        condition.setSendUserCode(criteria.getSendUserCode());
        condition.setSendStartTime(criteria.getStartTime() == null ? null : DateHelper.parseDateTime(criteria.getStartTime()).getTime());
        condition.setSendEndTime(criteria.getEndTime() == null ? null : DateHelper.parseDateTime(criteria.getEndTime()).getTime());
//        condition.setIs211(criteria.isIs211() ? Byte.parseByte(String.valueOf(Constants.CONSTANT_NUMBER_ONE))
//                : Byte.parseByte(String.valueOf(Constants.NUMBER_ZERO)));
        condition.setUserCode(criteria.getUserCode());
        condition.setSensitiveQuery(sensitiveQuery);
        return condition;
    }

    private BasicQueryEntity convertToBasicQueryEntity(PrintHandoverListDto dto) {
        BasicQueryEntity basicQueryEntity = new BasicQueryEntity();
        basicQueryEntity.setReceiveSiteName(dto.getReceiveSiteName());
        basicQueryEntity.setReceiveSiteCode(dto.getReceiveSiteCode());
        basicQueryEntity.setSiteName(dto.getSiteName());
        basicQueryEntity.setSiteCode(dto.getSiteCode());
        basicQueryEntity.setSendCode(dto.getSendCode());
        basicQueryEntity.setBoxCode(dto.getBoxCode());
        basicQueryEntity.setFcNo(dto.getFcNo());
        basicQueryEntity.setGoodWeight(dto.getGoodWeight());
        basicQueryEntity.setPayment(dto.getPayment());
        basicQueryEntity.setSendUser(dto.getSendUser());
        basicQueryEntity.setSendUserCode(dto.getSendUserCode());

        // 查询结果不显示敏感数据
        basicQueryEntity.setReceiverName(SendPrintConstants.TEXT_DOUBLE_BAR);
        basicQueryEntity.setReceiverAddress(SendPrintConstants.TEXT_DOUBLE_BAR);
        basicQueryEntity.setReceiverMobile(SendPrintConstants.TEXT_DOUBLE_BAR);

        basicQueryEntity.setSealNo(dto.getSealNo());
        basicQueryEntity.setReworkNo(dto.getReworkNo());
        basicQueryEntity.setInvoice(dto.getInvoice());
        basicQueryEntity.setReceiveSiteType(dto.getReceiveSiteType());
        basicQueryEntity.setGoodVolume(dto.getGoodVolume());
        basicQueryEntity.setBoardCode(dto.getBoardCode());
        basicQueryEntity.setBoardVolume(dto.getBoardVolume());
        basicQueryEntity.setDmsOutVolumeDynamic(dto.getDmsOutVolumeDynamic());
        basicQueryEntity.setDmsOutVolumeStatic(dto.getDmsOutVolumeStatic());
        basicQueryEntity.setRoadCode(dto.getRoadCode());
        basicQueryEntity.setIs211(Integer.valueOf(dto.getIs211()));

        basicQueryEntity.setSendSiteName(dto.getCreateSiteName());
        if (dto.getSiteType() != null && dto.getSiteType() != BaseContants.SANFANGKUAIDI) {
            basicQueryEntity.setSiteType(SendPrintConstants.TEXT_SELF_SUPPORT);
        }
        basicQueryEntity.setWaybill(dto.getWaybillCode());
        basicQueryEntity.setPackageBarNum(dto.getPackageNum());
        basicQueryEntity.setPackageBar(dto.getPackageCode());
        basicQueryEntity.setPackageBarWeight(dto.getPackageWeight());
        basicQueryEntity.setGoodWeight2(dto.getAgainWeight());
        basicQueryEntity.setPackageBarWeight2(dto.getPackageAgainWeight());
        basicQueryEntity.setSendPay(dto.getPayment() == null ? null : getSendPay(dto.getPayment()));
        basicQueryEntity.setWaybillType(dto.getWaybillType() == null ? SendPrintConstants.TEXT_GENERAL_ORDER : getWaybillType(dto.getWaybillType()));
        basicQueryEntity.setDeclaredValue(String.valueOf(dto.getDeclaredValue()));
        basicQueryEntity.setGoodValue(String.valueOf(dto.getGoodValue()));
        basicQueryEntity.setIsnew(dto.getIsNew() == null ? SendPrintConstants.TEXT_NO
                : dto.getIsNew() == Constants.CONSTANT_NUMBER_ONE ? SendPrintConstants.TEXT_YES : SendPrintConstants.TEXT_NO);
        basicQueryEntity.setIscancel(dto.getIsCancel() == null ? SendPrintConstants.TEXT_NO
                : dto.getIsCancel() == Constants.CONSTANT_NUMBER_ONE ? SendPrintConstants.TEXT_YES : SendPrintConstants.TEXT_NO);
        basicQueryEntity.setLuxury(dto.getLuxury() == null ? SendPrintConstants.TEXT_NO
                : dto.getLuxury() == Constants.CONSTANT_NUMBER_ONE ? SendPrintConstants.TEXT_YES : SendPrintConstants.TEXT_NO);
        basicQueryEntity.setOperateTime(dto.getSendTime() == null ? null : DateHelper.formatDateTime(new Date(dto.getSendTime())));
        basicQueryEntity.setSealTime(dto.getSealTime() == null ? null : DateHelper.formatDateTime(new Date(dto.getSealTime())));
        return basicQueryEntity;
    }

    private List<String> getWaybillCodeList(List<SendDetail> sendDetails){
        Set<String> waybillCodeSet = new HashSet<>();
        for (SendDetail sendDetail : sendDetails){
            if (StringUtils.isNotBlank(sendDetail.getWaybillCode())) {
                waybillCodeSet.add(sendDetail.getWaybillCode());
            }

        }
        return new ArrayList<>(waybillCodeSet);
    }

    private List<String> getPackageCodeList(List<SendDetail> sendDetails){
        Set<String> packageCodeSet = new HashSet<>();
        for (SendDetail sendDetail : sendDetails){
            if (StringUtils.isNotBlank(sendDetail.getPackageBarcode())) {
                packageCodeSet.add(sendDetail.getPackageBarcode());
            }

        }
        return new ArrayList<>(packageCodeSet);
    }

    private List<String> getBoxCodeList(List<SendDetail> sendDetails){
        Set<String> boxCodeSet = new HashSet<>();
        for (SendDetail sendDetail : sendDetails){
            if (StringUtils.isNotBlank(sendDetail.getBoxCode())) {
                boxCodeSet.add(sendDetail.getBoxCode());
            }

        }
        return new ArrayList<>(boxCodeSet);
    }

    /**
     * 分页明细打印
     *
     * @param sendDetails
     * @param criteria
     * @return
     */
    private BasicQueryEntityResponse assembleDetailPrintQueryBySendD(List<SendDetail> sendDetails, PrintQueryCriteria criteria) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.assembleDetailPrintQueryBySendD", false, true);

        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        String rSiteName = toSiteName(criteria.getReceiveSiteCode());
        Integer rSiteType = toSiteType(criteria.getReceiveSiteCode());
        String fSiteName = toSiteName(criteria.getSiteCode());

        String message = JdResponse.MESSAGE_OK;

        List<String> waybillCodes = this.getWaybillCodeList(sendDetails);
        List<String> boxCodes = this.getBoxCodeList(sendDetails);
        List<String> packageCodes = this.getPackageCodeList(sendDetails);

        // 预加载组板体积信息(箱号对应的板的体积)
        Map<String, Double> boardVolumeMap = this.getBoxCodeBoardVolumeByParam(criteria, boxCodes);
        // 预加载运单维度的商品体积（包裹号对应的包裹体积）
        Map<String, Double> goodVolumeMap = this.getAllGoodVolume(waybillCodes);
        // 预加载运单信息 批量获取不包含包裹信息的运单信息对象（运单号对应的不含包裹信息的运单对象）
        HashMap<String, BigWaybillDto> waybillDtoMap = this.getBigWaybillDtoMap(waybillCodes, false);
        // 批量获取包裹称重上传信息（包裹号对应的包裹重量对象）
        Map<String, PackageWeight> packageWeightMap = this.getWaybillPackageWeight(packageCodes);
        // 批量获取应付体积信息（箱号对应的应付量方体积对象）
        Map<String, DmsOutWeightAndVolume> dmsOutWeightAndVolumeMap = this.getDmsOutVolume(boxCodes, criteria.getSiteCode());

        // 批量获取封签号（箱号对应的封签号）
        Map<String, String> sealNoCache = this.getSealNoMap(boxCodes);

        List<BasicQueryEntity> resultList = new ArrayList<>();
        for (SendDetail detail : sendDetails) {
            try {
                BasicQueryEntity basicQueryEntity = this.assembleBasicQueryEntity(detail);
                basicQueryEntity.setReceiveSiteName(rSiteName);
                basicQueryEntity.setReceiveSiteType(rSiteType);
                basicQueryEntity.setSendSiteName(fSiteName);

                // 判断运单号是否为空
                if (StringUtils.isEmpty(basicQueryEntity.getWaybill())) {
                    resultList.add(basicQueryEntity);
                    log.warn("打印交接清单-如果运单号为空直接加入list返回");
                    continue;
                }

                if (rSiteType != null && rSiteType.equals(ASM_TYPE)) {
                    String callbackMsg = this.buildPickUpParams(basicQueryEntity);
                    if (callbackMsg != null) {
                        message = callbackMsg;
                    }
                }

                BigWaybillDto data = waybillDtoMap.get(basicQueryEntity.getWaybill());
                // 运单信息为空
                if (data == null || data.getWaybill() == null) {
                    resultList.add(basicQueryEntity);
                    continue;
                }

                this.assembleBySendPay(basicQueryEntity, data.getWaybill().getSendPay());
                // 组装应付体积信息
                this.assembleDmsOutVolume(basicQueryEntity, dmsOutWeightAndVolumeMap.get(basicQueryEntity.getBoxCode()));
                // 组装封签号
                this.assembleSealNo(basicQueryEntity, sealNoCache);
                // 构建运单信息中的属性
                this.buildWaybillAttributes(basicQueryEntity, data, goodVolumeMap, rSiteType);
                // 组装包裹重量和体积信息
                this.assemblePackageWeightAndVolume(basicQueryEntity, packageWeightMap.get(basicQueryEntity.getPackageBar()), goodVolumeMap);
                // 组装组板体积信息
                this.assembleBoardVolume(basicQueryEntity, boardVolumeMap.get(basicQueryEntity.getBoxCode()));
                resultList.add(basicQueryEntity);
            } catch (Exception e) {
                message = "同步运单基本信息异常错误原因为" + e.getMessage();
                log.error(message, e);
            }
        }
        tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK);
        tBasicQueryEntityResponse.setMessage(message);
        tBasicQueryEntityResponse.setData(resultList);
        Profiler.registerInfoEnd(callerInfo);
        return tBasicQueryEntityResponse;
    }

    private Map<String, Double> getBoxCodeBoardVolumeByParam(PrintQueryCriteria criteria, List<String> boxCodeList) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.assembleDetailPrintQueryBySendD.getBoxCodeBoardVolumeByParam", false, true);
        Map<String, Double> result = new HashMap<>();
        List<SendM> sendMList = this.getBoardSendMByParam(criteria, boxCodeList);
        if (sendMList.size() > 0) {
            Map<String, Double> boardVolume = getBoardValueMapByBoards(this.getBoardCodeBySendM(sendMList));
            for (int i = sendMList.size() - 1; i >= 0; i--) {
                SendM sendM = sendMList.get(i);
                result.put(sendM.getBoxCode(), boardVolume.get(sendM.getBoardCode()));
            }
        }
        Profiler.registerInfoEnd(callerInfo);
        return result;
    }

    private List<String> getBoardCodeBySendM(List<SendM> sendMList) {
        List<String> boardCodeList = new ArrayList<>();
        for (SendM sendM : sendMList) {
            boardCodeList.add(sendM.getBoardCode());
        }
        return boardCodeList;
    }

    private List<SendM> getBoardSendMByParam(PrintQueryCriteria criteria, List<String> boxCodeList) {
        SendM queryParams = new SendM();
        queryParams.setCreateSiteCode(criteria.getSiteCode());
        queryParams.setReceiveSiteCode(criteria.getReceiveSiteCode());

        int totalSize = boxCodeList.size();
        if (totalSize > QUERY_SENDM_SIZE) {
            int times = totalSize / QUERY_SENDM_SIZE;
            int mod = totalSize % QUERY_SENDM_SIZE;
            if (mod > 0) {
                times++;
            }
            List<SendM> sendMList = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                int start = i * QUERY_SENDM_SIZE;
                int end = start + QUERY_SENDM_SIZE;
                if (end > totalSize) {
                    end = totalSize;
                }
                queryParams.setBoxCodeList(boxCodeList.subList(start, end));
                sendMList.addAll(this.boardSendMFilter(sendMService.findByParams(queryParams)));
            }
            return sendMList;
        }
        queryParams.setBoxCodeList(boxCodeList);
        return this.boardSendMFilter(sendMService.findByParams(queryParams));
    }

    private List<SendM> boardSendMFilter(List<SendM> sendMList) {
        if (sendMList.size() > 0) {
            Iterator<SendM> iterator = sendMList.iterator();
            while (iterator.hasNext()) {
                SendM sendM = iterator.next();
                if (StringUtils.isBlank(sendM.getBoardCode())) {
                    iterator.remove();
                }
            }
        }
        return sendMList;
    }

    /**
     * 批量获取运单中包裹的重量信息
     *
     * @param packageCodeList
     * @return
     */
    private Map<String, PackageWeight> getWaybillPackageWeight(List<String> packageCodeList) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.assembleDetailPrintQueryBySendD.getWaybillPackageWeight", false, true);
        Map<String, PackageWeight> result = new HashMap<>(packageCodeList.size());
        if (packageCodeList != null && packageCodeList.size() > 0) {
            int totalSize = packageCodeList.size();
            int times = totalSize / QUERY_PACKAGE_SIZE;
            int mod = totalSize % QUERY_PACKAGE_SIZE;
            if (mod > 0) {
                times++;
            }
            for (int i = 0; i < times; i++) {
                int start = i * QUERY_PACKAGE_SIZE;
                int end = start + QUERY_PACKAGE_SIZE;
                if (end > totalSize) {
                    end = totalSize;
                }
                BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.queryPackageListForParcodes(packageCodeList.subList(start, end));
                if (baseEntity.getData() != null && baseEntity.getData().size() > 0) {
                    for (DeliveryPackageD deliveryPackageD : baseEntity.getData()) {
                        PackageWeight packageWeight = new PackageWeight();
                        packageWeight.setPackageCode(deliveryPackageD.getPackageBarcode());
                        packageWeight.setGoodWeight(deliveryPackageD.getGoodWeight());
                        packageWeight.setAgainWeight(deliveryPackageD.getAgainWeight());
                        result.put(deliveryPackageD.getPackageBarcode(), packageWeight);
                    }
                }
            }
        }
        Profiler.registerInfoEnd(callerInfo);
        return result;
    }

    private void assemblePackageWeightAndVolume(BasicQueryEntity basicQueryEntity, PackageWeight packageWeight, Map<String, Double> goodVolumeMap) {
        if (packageWeight != null) {
            basicQueryEntity.setPackageBarWeight(packageWeight.getGoodWeight());
            basicQueryEntity.setPackageBarWeight2(packageWeight.getAgainWeight());
        }
        Double goodVolume = goodVolumeMap.get(basicQueryEntity.getPackageBar());
        if (goodVolume != null) {
            basicQueryEntity.setGoodVolume(goodVolume);
        } else {
            basicQueryEntity.setGoodVolume(0.0);
        }
    }

    private void assembleBoardVolume(BasicQueryEntity basicQueryEntity, Double boardVolume) {
        if (boardVolume != null) {
            basicQueryEntity.setBoardVolume(boardVolume);
            //板体积不为null或0 应付静态量方取板体积
            if (basicQueryEntity.getBoardVolume() != null && NumberHelper.gt0(basicQueryEntity.getBoardVolume())) {
                basicQueryEntity.setDmsOutVolumeStatic(basicQueryEntity.getBoardVolume());
            }
        }
    }

    private void assembleBySendPay(BasicQueryEntity basicQueryEntity, String sendPay) {
        if (BusinessHelper.is211(sendPay)) {
            basicQueryEntity.setIs211(1);
        } else {
            basicQueryEntity.setIs211(0);
        }
    }

    /**
     * 组装数据对象
     *
     * @param sendDetail
     * @return
     */
    private BasicQueryEntity assembleBasicQueryEntity(SendDetail sendDetail) {
        BasicQueryEntity tBasicQueryEntity = new BasicQueryEntity();
        tBasicQueryEntity.setBoxCode(sendDetail.getBoxCode());
        if (sendDetail.getIsCancel() != null && sendDetail.getIsCancel() == 0) {
            tBasicQueryEntity.setIscancel(SendPrintConstants.TEXT_NO);
        } else {
            tBasicQueryEntity.setIscancel(SendPrintConstants.TEXT_YES);
        }
        tBasicQueryEntity.setIsnew(SendPrintConstants.TEXT_NO);
        tBasicQueryEntity.setPackageBarWeight(0.0);
        tBasicQueryEntity.setPackageBarWeight2(0.0);
        tBasicQueryEntity.setSiteCode(0);
        tBasicQueryEntity.setFcNo(0);
        tBasicQueryEntity.setPackageBar(sendDetail.getPackageBarcode());
        tBasicQueryEntity.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        tBasicQueryEntity.setSendCode(sendDetail.getSendCode());
        tBasicQueryEntity.setSendUser(sendDetail.getCreateUser());
        tBasicQueryEntity.setSendUserCode(sendDetail.getCreateUserCode());
        tBasicQueryEntity.setWaybill(sendDetail.getWaybillCode());
        tBasicQueryEntity.setInvoice(sendDetail.getPickupCode());
        tBasicQueryEntity.setBoxCode(sendDetail.getBoxCode());
        return tBasicQueryEntity;
    }

    /**
     * 批量获取封签号
     *
     * @param barCodeList
     * @return
     */
    private Map<String, String> getSealNoMap(List<String> barCodeList) {
        Map<String, String> sealNoCache = new HashMap<>();
        List<String> boxCodeList = new ArrayList<>();
        for (String barCode : barCodeList) {
            if (BusinessHelper.isBoxcode(barCode)) {
                boxCodeList.add(barCode);
            }
        }
        if (boxCodeList.size() > 0) {
            List<SealBox> tSealBox = this.tSealBoxService.findListByBoxCodes(boxCodeList);
            if (tSealBox.size() > 0) {
                for (int i = tSealBox.size() - 1; i >= 0; i--) {
                    SealBox sealBox = tSealBox.get(i);
                    sealNoCache.put(sealBox.getBoxCode(), sealBox.getCode());
                }
            }
        }

        return sealNoCache;
    }

    /**
     * 组装封签号
     *
     * @param basicQueryEntity
     * @param sealNoCache
     */
    private void assembleSealNo(BasicQueryEntity basicQueryEntity, Map<String, String> sealNoCache){
        String boxCode = basicQueryEntity.getBoxCode();
        if (BusinessHelper.isBoxcode(boxCode)) {
            sealNoCache.put(boxCode, sealNoCache.get(boxCode));
        }
    }

    /**
     * 批量获取应付体积信息
     *
     * @param boxCodeList
     * @param createSiteCode
     * @return
     */
    private Map<String, DmsOutWeightAndVolume> getDmsOutVolume(List<String> boxCodeList, Integer createSiteCode) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.assembleDetailPrintQueryBySendD.getDmsOutVolume", false, true);
        Map<String, DmsOutWeightAndVolume> resultMap = new HashMap<>();
        if (boxCodeList != null && boxCodeList.size() > 0 && createSiteCode != null) {
            List<DmsOutWeightAndVolume> outList = dmsOutWeightAndVolumeService.getListByBarCodesAndDms(boxCodeList, createSiteCode);
            for (int i = outList.size() - 1; i >= 0; i--) {
                DmsOutWeightAndVolume weightAndVolume = outList.get(i);
                resultMap.put(weightAndVolume.getBarCode(), weightAndVolume);
            }
        }
        Profiler.registerInfoEnd(callerInfo);
        return resultMap;
    }

    /**
     * 组装应付体积信息
     *
     * @param tBasicQueryEntity
     * @param weightAndVolume
     */
    private void assembleDmsOutVolume(BasicQueryEntity tBasicQueryEntity, DmsOutWeightAndVolume weightAndVolume) {
        /** 设置应付体积 **/
        Double outVolumeDynamic = 0.0;
        Double outVolumeStatic = 0.0;
        try {
            if (weightAndVolume != null) {
                Double volume = weightAndVolume.getVolume();
                //将cm³转换成m³
                volume = BigDecimalHelper.div(volume, PARAM_CM3_M3, 6);
                if (weightAndVolume.getOperateType().equals(DmsOutWeightAndVolume.OPERATE_TYPE_STATIC)) {
                    outVolumeStatic = volume;
                } else {
                    outVolumeDynamic = volume;
                }
            }
        } catch (Exception e) {
            log.error("发货交接清单打印-明细打印-查询分拣中心[{}]操作对该箱号/包裹[{}]的体积.", tBasicQueryEntity.getBoxCode(), tBasicQueryEntity.getSiteCode(), e);
        }
        tBasicQueryEntity.setDmsOutVolumeDynamic(outVolumeDynamic);
        tBasicQueryEntity.setDmsOutVolumeStatic(outVolumeStatic);
    }

    private SendDetailDto getSendDQueryParams(PrintQueryCriteria criteria){
        SendDetailDto sendDetail = new SendDetailDto();
        sendDetail.setCreateSiteCode(criteria.getSiteCode());
        sendDetail.setReceiveSiteCode(criteria.getReceiveSiteCode());
        if (StringUtils.isNotEmpty(criteria.getSendCode())) {
            sendDetail.setSendCode(criteria.getSendCode());
        }

        if (StringUtils.isNotEmpty(criteria.getBoxCode())) {
            sendDetail.setBoxCode(criteria.getBoxCode());
        } else {
            sendDetail.setStartTime(DateHelper.parseDateTime(criteria.getStartTime()));
            sendDetail.setEndTime(DateHelper.parseDateTime(criteria.getEndTime()));
        }

        if (StringUtils.isNotEmpty(criteria.getPackageBarcode())) {
            sendDetail.setPackageBarcode(criteria.getPackageBarcode());
        }

        if (criteria.getSendUserCode() != null) {
            sendDetail.setCreateUserCode(criteria.getSendUserCode());
        }
        sendDetail.setLimit(criteria.getPageSize());
        sendDetail.setOffset(criteria.getPageSize() * (criteria.getPageNo() - 1));
        return sendDetail;
    }

    private String toSiteName(Integer siteCode) {
        BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (bDto == null) {
            return null;
        }
        return bDto.getSiteName();
    }

    private Integer toSiteType(Integer siteCode) {
        BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (bDto == null) {
            return null;
        }
        return bDto.getSiteType();
    }

    private Integer toSiteSubType(Integer siteCode) {
        BaseStaffSiteOrgDto bDto = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (bDto == null) {
            return null;
        }
        return bDto.getSubType();
    }

    private HashMap<String, BigWaybillDto> sendToWaybill(HashMap<String, BigWaybillDto> deliveryPackageMap, List<String> waybillCodes, Boolean isPackList) {
        BaseEntity<List<BigWaybillDto>> results = this.waybillQueryManager.getDatasByChoice(waybillCodes, true, true, true, isPackList);
        if (results != null && results.getResultCode() > 0) {
            List<BigWaybillDto> dataList = results.getData();
            if (dataList != null && !dataList.isEmpty()) {
                for (BigWaybillDto data : dataList) {
                    if (data.getWaybill() != null) {
                        deliveryPackageMap.put(data.getWaybill().getWaybillCode(), data);
                    }
                }
            }
        }
        return deliveryPackageMap;
    }

    @Override
    public BasicQueryEntityResponse sopPrintQuery(PrintQueryCriteria criteria) {
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        try {
            SendM qSendM = tosendM(criteria);
            List<SendM> sendMs = this.sendMDao.selectBySendSiteCode(qSendM);
            if (sendMs != null && !sendMs.isEmpty()) {
                tBasicQueryEntityResponse = detailPrintQuerySop(sendMs, criteria);
            }
        } catch (Exception e) {
            log.error("打印明细基本查询异常:{}",JsonHelper.toJson(criteria));
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        }
        return tBasicQueryEntityResponse;
    }


    /**
     * 获取SendM 列表【去重后结果，按批次号及箱号去重】
     *
     * @param domain 查询参数
     * @return
     */
    private final List<SendM> selectUniquesSendMs(SendM domain) {
        List<SendM> sendMs = this.sendMDao.selectBySendSiteCode(domain);
        Set<String> set = new HashSet<String>();
        List<SendM> hasMap = new ArrayList<SendM>();
        if (null != sendMs) {
            for (SendM item : sendMs) {
                if (!set.contains(item.getSendCode() + "-" + item.getBoxCode())) {
                    set.add(item.getSendCode() + "-" + item.getBoxCode());
                    hasMap.add(item);
                }
            }
        }

        return hasMap;
    }

    /**
     * 对sendD列表进行去重【按包裹号对T单进行去重】
     * create By wuzuxiang at 2016年11月24日18:20:05
     *
     * @param sendDetailList send列表
     * @return 返回去重后的结果
     */
    private final List<SendDetail> selectUniquesSendDetails(List<SendDetail> sendDetailList) {
        List<SendDetail> result = new ArrayList<SendDetail>();
        Map<String, SendDetail> sendDMap = new HashMap<String, SendDetail>();
        if (null != sendDetailList) {
            for (SendDetail item : sendDetailList) {
                String packageBarCode;
                if (item.getPackageBarcode() == null) {
                    packageBarCode = "";
                } else {
                    packageBarCode = item.getPackageBarcode();
                }
                //如果单号以T开始，则从index=1开始截取子字符串
                String packageBarCodeNoT = packageBarCode.startsWith("T") ? packageBarCode.substring(1) : packageBarCode;
                if (!sendDMap.containsKey(packageBarCodeNoT)) {
                    sendDMap.put(packageBarCodeNoT, item);
                    result.add(item);
                } else {
                    if (packageBarCode.startsWith("T")) {
                        SendDetail itemUseless = sendDMap.get(packageBarCodeNoT);//找到原单的数据，标记无效
                        sendDMap.remove(packageBarCodeNoT);//从map中剔除原单数据
                        sendDMap.put(packageBarCodeNoT, item);//更新为新的T单数据
                        result.remove(itemUseless);//从list中去除原单
                        result.add(item);//将此T单添加至结果集中
                    }
                }
            }
        }
        return result;
    }

    /**
     * 明细打印
     */
    public BasicQueryEntityResponse detailPrintQuerySop(List<SendM> sendMs, PrintQueryCriteria criteria) {
        log.debug("SOP打印交接清单-detailPrintQuerySop开始");
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        List<BasicQueryEntity> fzList = new ArrayList<BasicQueryEntity>();
        String rsiteName = toSiteName(criteria.getReceiveSiteCode());
        String fsiteName = toSiteName(criteria.getSiteCode());

        String message = JdResponse.MESSAGE_OK;
        for (SendM sendM : sendMs) {
            List<BasicQueryEntity> tList = new ArrayList<BasicQueryEntity>();
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setCreateSiteCode(sendM.getCreateSiteCode());
            tSendDatail.setBoxCode(sendM.getBoxCode());
            tSendDatail.setReceiveSiteCode(sendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(1);
            List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);

            if (sendDetails != null && !sendDetails.isEmpty()) {
                for (SendDetail dSendDatail : sendDetails) {
                    if (criteria.getWaybillcode() == null || "".equals(criteria.getWaybillcode())
                            || criteria.getWaybillcode().equals(dSendDatail.getWaybillCode())) {
                        BasicQueryEntity tBasicQueryEntity = new BasicQueryEntity();
                        tBasicQueryEntity.setPackageBar(dSendDatail.getPackageBarcode());
                        tBasicQueryEntity.setReceiveSiteName(rsiteName);
                        tBasicQueryEntity.setSendSiteName(fsiteName);
                        tBasicQueryEntity.setSendUser(sendM.getCreateUser());
                        tBasicQueryEntity.setPackageBarNum(dSendDatail.getPackageNum());
                        tBasicQueryEntity.setWaybill(dSendDatail.getWaybillCode());
                        tBasicQueryEntity.setOperateTime(DateHelper.formatDateTime(sendM.getOperateTime()));
                        tList.add(tBasicQueryEntity);
                    }
                }
            }
            if (tList != null && !tList.isEmpty()) {
                for (BasicQueryEntity tBasicQueryEntity : tList) {
                    fzList.add(tBasicQueryEntity);
                }
            }
        }
        tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK);
        tBasicQueryEntityResponse.setMessage(message);
        tBasicQueryEntityResponse.setData(fzList);
        return tBasicQueryEntityResponse;
    }


    @Override
    public BatchSendInfoResponse selectBoxBySendCode(List<BatchSend> batchSends) {
        log.debug("获取发货批次下的原包及箱子信息-selectBoxBySendCode");
        BatchSendInfoResponse batchSendInfoResponse = new BatchSendInfoResponse();
        try {
            Map<String, String> boxes = new HashMap<String, String>();// 存放箱号用于计算箱子数量
            Map<String, String> packages = new HashMap<String, String>();// 存放包裹号用于计算包裹数量
            for (int i = 0; i < batchSends.size(); i++) {
                List<String> scanCodeList = this.sendMReadDao.selectBoxCodeBySendCode(batchSends.get(i).getSendCode());
                if (scanCodeList != null && scanCodeList.size() > 0) {
                    for (String scanCode : scanCodeList) {
                        if (BusinessHelper.isBoxcode(scanCode))
                            boxes.put(scanCode, scanCode);
                        else if (WaybillUtil.isPackageCode(scanCode))
                            packages.put(scanCode, scanCode);
                    }
                }
            }

            // 组装返回值对象
            BatchSendResult batchSendResult = new BatchSendResult();
            batchSendResult.setTotalBoxNum(boxes.size());
            batchSendResult.setPackageBarNum(packages.size());

            List<BatchSendResult> data = new ArrayList<BatchSendResult>();
            data.add(batchSendResult);


            batchSendInfoResponse.setData(data);
            batchSendInfoResponse.setCode(JdResponse.CODE_OK);
            batchSendInfoResponse.setMessage(JdResponse.MESSAGE_OK);
        } catch (Throwable e) {
            log.error("查询发货原包数量与箱子数量", e);
            batchSendInfoResponse.setCode(InvokeResult.SERVER_ERROR_CODE);
            batchSendInfoResponse.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return batchSendInfoResponse;
    }

    /**
     * 快生打印
     */
    public BasicQueryEntityResponse basicPrintQueryOffline(PrintQueryCriteria criteria) {
        Date startDate = new Date();
        log.debug("打印交接清单-基本信息查询开始:{}" , DateHelper.formatDate(startDate));
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        try {
            SendM qSendM = tosendM(criteria);
            List<SendM> sendMs = this.selectUniquesSendMs(qSendM);
            if (sendMs != null && !sendMs.isEmpty()) {
                tBasicQueryEntityResponse = detailPrintQueryOffline(sendMs, criteria);
            }
        } catch (Exception e) {
            log.error("打印明细基本查询异常");
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        }
        Date endDate = new Date();
        log.debug("打印交接清单-基本信息查询结束-{}" , (startDate.getTime() - endDate.getTime()));
        return tBasicQueryEntityResponse;
    }

    /**
     * 快生明细打印
     */
    public BasicQueryEntityResponse detailPrintQueryOffline(List<SendM> sendMs, PrintQueryCriteria criteria) {
        Date startDate = new Date();
        log.debug("打印交接清单-detailPrintQuery开始:{}" , DateHelper.formatDate(startDate));

        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        List<BasicQueryEntity> tList = new ArrayList<BasicQueryEntity>();
        String rsiteName = toSiteName(criteria.getReceiveSiteCode());
        String fsiteName = toSiteName(criteria.getSiteCode());
        Integer rSiteType = toSiteType(criteria.getReceiveSiteCode());

        String message = JdResponse.MESSAGE_OK;
        for (SendM sendM : sendMs) {
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setCreateSiteCode(sendM.getCreateSiteCode());
            tSendDatail.setBoxCode(sendM.getBoxCode());
            tSendDatail.setReceiveSiteCode(sendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(1);

            List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);

            if (sendDetails != null && !sendDetails.isEmpty()) {
                try {
                    for (SendDetail dSendDatail : sendDetails) {
                        if (criteria.getPackageBarcode() == null || "".equals(criteria.getPackageBarcode())
                                || criteria.getPackageBarcode().equals(dSendDatail.getPackageBarcode())) {
                            BasicQueryEntity tBasicQueryEntity = new BasicQueryEntity();
                            tBasicQueryEntity.setBoxCode(sendM.getBoxCode());
                            if (dSendDatail.getIsCancel() != null && dSendDatail.getIsCancel() == 0) {
                                tBasicQueryEntity.setIscancel("否");
                            } else {
                                tBasicQueryEntity.setIscancel("是");
                            }
                            tBasicQueryEntity.setIsnew("否");
                            tBasicQueryEntity.setPackageBarWeight(0.0);
                            tBasicQueryEntity.setPackageBarWeight2(0.0);
                            tBasicQueryEntity.setPackageBar(dSendDatail.getPackageBarcode());
                            tBasicQueryEntity.setReceiveSiteCode(sendM.getReceiveSiteCode());
                            tBasicQueryEntity.setReceiveSiteName(rsiteName);
                            tBasicQueryEntity.setSendCode(sendM.getSendCode());
                            tBasicQueryEntity.setReceiveSiteType(rSiteType);
                            tBasicQueryEntity.setSendSiteName(fsiteName);
                            tBasicQueryEntity.setSendUser(sendM.getCreateUser());
                            tBasicQueryEntity.setSendUserCode(sendM.getCreateUserCode());
                            tBasicQueryEntity.setWaybill(dSendDatail.getWaybillCode());
                            tBasicQueryEntity.setInvoice(dSendDatail.getPickupCode());

                            if (dSendDatail.getWaybillCode() != null) {
                                QuickProduceWabill tQuickProduceWabill = quickProduceService
                                        .getQuickProduceWabill(dSendDatail.getWaybillCode());
                                if (tQuickProduceWabill == null) {
                                    log.warn("打印交接清单-tQuickProduceWabill为空");
                                    tList.add(tBasicQueryEntity);
                                    continue;
                                }
                                JoinDetail tJoinDetail = tQuickProduceWabill.getJoinDetail();
                                if (tJoinDetail == null) {
                                    log.warn("打印交接清单-tJoinDetail为空");
                                    tList.add(tBasicQueryEntity);
                                    continue;
                                }
                                tBasicQueryEntity.setFcNo(tJoinDetail.getDistributeStoreId());
                                tBasicQueryEntity.setDeclaredValue(String.valueOf(tJoinDetail.getDeclaredValue()));
                                tBasicQueryEntity.setGoodValue(String.valueOf(tJoinDetail.getPrice()));
                                tBasicQueryEntity.setGoodWeight(tJoinDetail.getGoodWeight());
                                tBasicQueryEntity.setGoodWeight2(0.0);
                                tBasicQueryEntity.setPackageBarNum(WaybillUtil.getPackNumByPackCode(dSendDatail.getPackageBarcode()));
                                String sendPay = tJoinDetail.getSendPay();
                                // 是否是奢侈品
                                if (sendPay != null && sendPay.charAt(19) == '1') {
                                    tBasicQueryEntity.setLuxury("是");
                                } else {
                                    tBasicQueryEntity.setLuxury("否");
                                }

                                Integer siteId = tJoinDetail.getOldSiteId();
                                String siteName = null;
                                BaseStaffSiteOrgDto bDto = this.baseMajorManager
                                        .getBaseSiteBySiteId(siteId);
                                if (bDto != null) {
                                    siteName = bDto.getSiteName();
                                    Integer siteType = bDto.getSiteType();
                                    if (siteType != null && !siteType.equals(16)) {
                                        tBasicQueryEntity.setSiteType("自营");
                                    }
                                }

                                tBasicQueryEntity.setPayment(tJoinDetail.getPayment());
                                if (rSiteType.equals(16)) {
                                    tBasicQueryEntity.setReceiverAddress(tJoinDetail.getReceiverAddress() == null ? ""
                                            : tJoinDetail.getReceiverAddress());
                                    String receiverMobile = tJoinDetail.getReceiverMobile() == null ? ""
                                            : tJoinDetail.getReceiverMobile();
                                    String receiverTel = tJoinDetail.getReceiverTel() == null ? ""
                                            : tJoinDetail.getReceiverTel();
                                    if (tJoinDetail.getReceiverMobile() == null
                                            && tJoinDetail.getReceiverTel() == null) {
                                        tBasicQueryEntity.setReceiverMobile("--");
                                    } else {
                                        //tBasicQueryEntity.setReceiverMobile(receiverMobile + "/" + receiverTel);
                                        tBasicQueryEntity.setReceiverMobile("");
                                    }
                                } else {
                                    tBasicQueryEntity.setReceiverMobile("--");
                                }

                                tBasicQueryEntity.setReceiverName(tJoinDetail.getReceiverName());
                                if (tJoinDetail.getPayment() == null) {
                                    tBasicQueryEntity.setSendPay("");
                                } else {
                                    tBasicQueryEntity.setSendPay(getSendPay(tJoinDetail.getPayment()));
                                    if (tJoinDetail.getPayment() != 1 && tJoinDetail.getPayment() != 3) {
                                        tBasicQueryEntity.setDeclaredValue("0");
                                    }
                                }
                                tBasicQueryEntity.setSiteCode(siteId);
                                tBasicQueryEntity.setSiteName(siteName);
                                if (tJoinDetail.getWaybillType() == null) {
                                    tBasicQueryEntity.setWaybillType("一般订单");
                                } else {
                                    tBasicQueryEntity.setWaybillType(getWaybillType(tJoinDetail.getWaybillType()));
                                }
                            }

                            tList.add(tBasicQueryEntity);
                        }
                    }
                } catch (Exception e) {
                    message = "同步运单基本信息异常错误原因为" + e.getMessage();
                    log.error("同步运单基本信息异常错误原因为:" ,e);
                }
            }
        }
        tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK);
        tBasicQueryEntityResponse.setMessage(message);
        tBasicQueryEntityResponse.setData(tList);
        Date endDate = new Date();
        log.debug("打印交接清单-detailPrintQuery结束-{}" , (startDate.getTime() - endDate.getTime()));
        return tBasicQueryEntityResponse;
    }

    class PackageWeight{

        private String packageCode;

        private Double goodWeight;

        private Double againWeight;

        public String getPackageCode() {
            return packageCode;
        }

        public void setPackageCode(String packageCode) {
            this.packageCode = packageCode;
        }

        public Double getGoodWeight() {
            return goodWeight;
        }

        public void setGoodWeight(Double goodWeight) {
            this.goodWeight = goodWeight;
        }

        public Double getAgainWeight() {
            return againWeight;
        }

        public void setAgainWeight(Double againWeight) {
            this.againWeight = againWeight;
        }
    }
}
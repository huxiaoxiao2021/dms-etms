package com.jd.bluedragon.distribution.sendprint.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.quickProduce.domain.JoinDetail;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.dao.SendMReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sendprint.domain.*;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.distribution.sendprint.utils.SendPrintConstants;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.bluedragon.distribution.weightAndMeasure.service.DmsOutWeightAndVolumeService;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.BoardMeasureDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import oracle.jdbc.driver.Const;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    private final Logger logger = Logger.getLogger(SendPrintServiceImpl.class);

    @Autowired
    private QuickProduceService quickProduceService;

    @Autowired
    private BoxService boxService;

    @Autowired
    BoardCombinationService boardCombinationService;

    @Autowired
    private DmsOutWeightAndVolumeService dmsOutWeightAndVolumeService;

    private static int PARAM_CM3_M3 = 1000000;//立方厘米和立方米的换算基数

    public final static Integer ASM_TYPE = Integer.parseInt(PropertiesHelper.newInstance().getValue("asm_type"));

    /**
     * 一次批量查询运单数据的大小
     */
    private final static int QUERY_SIZE = 50;

    /**
     * 批次汇总&&批次汇总打印
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @JProfiler(jKey = "DMSWEB.SendPrintServiceImpl.batchSummaryPrintQuery", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SummaryPrintResultResponse batchSummaryPrintQuery(PrintQueryCriteria criteria) {
        SummaryPrintResultResponse tSummaryPrintResultResponse = new SummaryPrintResultResponse();
        List<SummaryPrintResult> results = new ArrayList<SummaryPrintResult>();
        SendM nSendM = tosendM(criteria);
        Date startDate = new Date();
        logger.info("打印交接清单-批次汇总开始" + DateHelper.formatDate(startDate));
        //满足条件的所有箱号
        List<SendM> sendMs = this.selectUniquesSendMs(nSendM); //this.sendMDao.selectBySendSiteCode(nSendM);
        if (sendMs != null && !sendMs.isEmpty()) {
            logger.info("打印交接清单-批次汇总数目" + sendMs.size());
            try {
                results = this.summaryPrintResultToList(sendMs, criteria);
            } catch (Exception e) {
                logger.error("批次汇总&&批次汇总打印异常", e);
                tSummaryPrintResultResponse.setCode(JdResponse.CODE_NOT_FOUND);
                tSummaryPrintResultResponse.setMessage("批次汇总打印异常");
                tSummaryPrintResultResponse.setData(results);
                return tSummaryPrintResultResponse;
            }
        }
        tSummaryPrintResultResponse.setCode(JdResponse.CODE_OK);
        tSummaryPrintResultResponse.setMessage(JdResponse.MESSAGE_OK);
        tSummaryPrintResultResponse.setData(results);
        Date endDate = new Date();
        logger.info("打印交接清单-批次汇总结束-" + (startDate.getTime() - endDate.getTime()));
        return tSummaryPrintResultResponse;
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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
        logger.info("打印交接清单-summaryPrintQuery开始" + DateHelper.formatDate(startDate));
        SummaryPrintResult result = new SummaryPrintResult();
        List<SummaryPrintBoxEntity> details = new ArrayList<SummaryPrintBoxEntity>();
        result.setSendCode(oriSendM.getSendCode());
        result.setReceiveSiteName(toSiteName(criteria.getReceiveSiteCode()));
        result.setSendSiteName(toSiteName(criteria.getSiteCode()));
        result.setSendTime(DateHelper.formatDateTime(oriSendM.getOperateTime()));
        logger.info("打印交接清单-批次汇总箱子数量" + sendMList.size());

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
            logger.info("打印交接清单-批次单独批次开始" + DateHelper.formatDate(startDate1));
            Set<String> packageBarcodeSet = new HashSet<String>();
            Set<String> waybillCodeSet = new HashSet<String>();
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setCreateSiteCode(sendM.getCreateSiteCode());
            tSendDatail.setBoxCode(sendM.getBoxCode());
            tSendDatail.setReceiveSiteCode(sendM.getReceiveSiteCode());
            tSendDatail.setIsCancel(0);
            logger.info("打印交接清单-批次汇总箱子信息" + sendM.getBoxCode());
            List<SendDetail> sendDetails = this.sendDatailDao.querySendDatailsBySelective(tSendDatail);
            sendDetails = selectUniquesSendDetails(sendDetails);//create by wuzuxiang 2016年11月24日 T单、原单去重
//		    if(sendDetails!=null && !sendDetails.isEmpty()){ 使打印交接汇总清单时带出空箱，之前不打印空箱
            for (SendDetail sendDatail : sendDetails) {
                String packageBarcode = sendDatail.getPackageBarcode();
                if (criteria.getPackageBarcode() != null && !"".equals(criteria.getPackageBarcode()) &&
                        !criteria.getPackageBarcode().equals(packageBarcode)) {
                    continue;
                }
                String waybillCode = sendDatail.getWaybillCode();
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
                    logger.error("打印交接清单获取箱号失败", e);
                    logger.error(JsonHelper.toJson(sendM));
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
                if (BusinessHelper.isPackageCode(sendM.getBoxCode())) {
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
                    if(weightAndVolume.getOperateType().equals(DmsOutWeightAndVolume.OPERATE_TYPE_STATIC)){
                        totalOutVolumeSt += weightAndVolume.getVolume();
                    }else{
                        totalOutVolumeDy += weightAndVolume.getVolume();
                    }
                }
            }

            details.add(detail);

//		    }
            Date endDate1 = new Date();
            logger.info("打印交接清单-批次单独批次结束-" + (startDate1.getTime() - endDate1.getTime()));
        }
        /**
         * 加载本批次的路区信息
         */
        if (cn > 0) {
            HashMap<String, BigWaybillDto> waybillInfos = new HashMap<String, BigWaybillDto>();
            sendToWaybill(waybillInfos, waybillCodeList);
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
        logger.info("打印交接清单-summaryPrintQuery结束-" + (startDate.getTime() - endDate.getTime()));
        return result;
    }

    private PackOpeFlowDto getOpeByPackageCode(String packageCode) {
        try {
            String waybillCode = BusinessHelper.getWaybillCode(packageCode);
            BaseEntity<List<PackOpeFlowDto>> packageOpe = waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
            for (PackOpeFlowDto packOpeFlowDto : packageOpe.getData()) {
                if (packOpeFlowDto.getPackageCode().equals(packageCode)) {
                    return packOpeFlowDto;
                }
            }
        } catch (Exception e) {
            logger.error("获取包裹量方信息接口失败，原因", e);
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
                            double volume = board.getVolume()/PARAM_CM3_M3;    //立方厘米转立方米
                            boardMap.put(board.getBoardCode(), NumberHelper.doubleFormat(volume));
                        }
                    }
                }
            }catch (Exception e){
                logger.error("发货清单打印查询托盘体积异常，不再打印托盘体积。板号数据：" + boardCodeList.toString(), e);
            }
        }
        return boardMap;
    }

    private Map<String, Double> getAllGoodVolume(List<String> waybillCodes) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.detailPrintQuery.getAllGoodVolume", false, true);
        Map<String, Double> goodVolumeMap = new HashMap<String, Double>();
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
                    logger.error("获取包裹量方信息接口失败，原因", e);
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
    private HashMap<String, BigWaybillDto> getBigWaybillDtoMap(List<String> waybillCodes) {
        CallerInfo callerInfo = Profiler.registerInfo("DMSWEB.SendPrintServiceImpl.detailPrintQuery.getBigWaybillDtoMap", false, true);
        HashMap<String, BigWaybillDto> deliveryPackageMap = new HashMap<String, BigWaybillDto>();
        if (waybillCodes != null && !waybillCodes.isEmpty()) {
            int n = waybillCodes.size() / QUERY_SIZE;
            int m = waybillCodes.size() % QUERY_SIZE;
            if (n > 0) {
                List<String> waybills = new ArrayList<String>();
                int num = 0;
                for (String code : waybillCodes) {
                    num++;
                    waybills.add(code);
                    if (num / QUERY_SIZE > 0 && num % QUERY_SIZE == 0 && n > 0) {
                        sendToWaybill(deliveryPackageMap, waybills);
                        waybills = new ArrayList<String>();
                        n--;
                    } else if (n == 0 && m > 0 && waybillCodes.size() == num) {
                        sendToWaybill(deliveryPackageMap, waybills);
                    }
                }
            } else {
                sendToWaybill(deliveryPackageMap, waybillCodes);
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
                        if (mPickupTask.getNewWaybillCode() != null)
                            dBasicQueryEntity.setIsnew(SendPrintConstants.TEXT_YES);
                    }
                } else {
                    message = "打印交接清单-取件单基础信息调用失败，运单号：" + dBasicQueryEntity.getWaybill() + "，" + tPickupTask == null ? "返回的BaseEntity对象为null" : "返回的状态码为" + tPickupTask.getResultCode();
                    logger.warn(message);
                }
            } catch (Exception e) {
                message = "打印交接清单-取件单基础信息调用发生异常" + dBasicQueryEntity.getWaybill();
                logger.error(message, e);
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
                DmsOutWeightAndVolume weightAndVolume = dmsOutWeightAndVolumeService.getOneByBarCodeAndDms(sendM.getBoardCode(), sendM.getCreateSiteCode());
                if (weightAndVolume != null) {
                    if (weightAndVolume.getOperateType().equals(DmsOutWeightAndVolume.OPERATE_TYPE_STATIC)) {
                        outVolumeStatic = weightAndVolume.getVolume();
                    } else {
                        outVolumeDynamic = weightAndVolume.getVolume();
                    }
                }
            }catch(Exception e){
                logger.error("发货交接清单打印-明细打印-获取出分拣中心体积异常.",e);
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

                    HashMap<String, BigWaybillDto> waybillDtoMap = this.getBigWaybillDtoMap(waybillCodes);

                    Map<String, Double> goodVolumeMap = this.getAllGoodVolume(waybillCodes);

                    for (BasicQueryEntity dBasicQueryEntity : tList) {
                        if (rSiteType != null && rSiteType.equals(ASM_TYPE)) {
                            String callbackMsg = this.buildPickUpParams(dBasicQueryEntity);
                            if (callbackMsg != null)
                                message = callbackMsg;
                        }
                        // 判断运单号是否为空
                        if (StringUtils.isEmpty(dBasicQueryEntity.getWaybill())) {
                            logger.info("打印交接清单-如果运单号为空直接加入list返回");
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
                    logger.error(message, e);
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


    /**
     * 基本查询
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @JProfiler(jKey = "DMSWEB.SendPrintServiceImpl.basicPrintQuery", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BasicQueryEntityResponse basicPrintQuery(PrintQueryCriteria criteria) {
        Date startDate = new Date();
        logger.info("打印交接清单-基本信息查询开始" + DateHelper.formatDate(startDate));
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        try {
            SendM qSendM = tosendM(criteria);
            List<SendM> sendMs = this.selectUniquesSendMs(qSendM);// this.sendMDao.selectBySendSiteCode(qSendM);
            if (sendMs != null && !sendMs.isEmpty()) {
                tBasicQueryEntityResponse = detailPrintQuery(sendMs, criteria);
            }
        } catch (Exception e) {
            logger.error("打印明细基本查询异常", e);
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        }
        Date endDate = new Date();
        logger.info("打印交接清单-基本信息查询结束-" + (startDate.getTime() - endDate.getTime()));
        return tBasicQueryEntityResponse;
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

    private HashMap<String, BigWaybillDto> sendToWaybill(HashMap<String, BigWaybillDto> deliveryPackageMap, List<String> waybillCodes) {
        BaseEntity<List<BigWaybillDto>> results = this.waybillQueryManager.getDatasByChoice(waybillCodes, true, true, true, true);
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
            logger.error("打印明细基本查询异常");
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
                String packageBarCodeNoT = packageBarCode.startsWith("T") ? packageBarCode.substring(1) : packageBarCode;//如果单号以T开始，则从index=1开始截取子字符串
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
        logger.info("SOP打印交接清单-detailPrintQuerySop开始");
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
        logger.info("获取发货批次下的原包及箱子信息-selectBoxBySendCode");
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
                        else if (BusinessHelper.isPackageCode(scanCode))
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
            logger.error("查询发货原包数量与箱子数量", e);
            batchSendInfoResponse.setCode(InvokeResult.SERVER_ERROR_CODE);
            batchSendInfoResponse.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return batchSendInfoResponse;
    }

    /**
     * 快生打印
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public BasicQueryEntityResponse basicPrintQueryOffline(PrintQueryCriteria criteria) {
        Date startDate = new Date();
        logger.info("打印交接清单-基本信息查询开始" + DateHelper.formatDate(startDate));
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        try {
            SendM qSendM = tosendM(criteria);
            List<SendM> sendMs = this.selectUniquesSendMs(qSendM);
            if (sendMs != null && !sendMs.isEmpty()) {
                tBasicQueryEntityResponse = detailPrintQueryOffline(sendMs, criteria);
            }
        } catch (Exception e) {
            logger.error("打印明细基本查询异常");
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        }
        Date endDate = new Date();
        logger.info("打印交接清单-基本信息查询结束-" + (startDate.getTime() - endDate.getTime()));
        return tBasicQueryEntityResponse;
    }

    /**
     * 快生明细打印
     */
    public BasicQueryEntityResponse detailPrintQueryOffline(List<SendM> sendMs, PrintQueryCriteria criteria) {
        Date startDate = new Date();
        logger.info("打印交接清单-detailPrintQuery开始" + DateHelper.formatDate(startDate));

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
                                    logger.info("打印交接清单-tQuickProduceWabill为空");
                                    tList.add(tBasicQueryEntity);
                                    continue;
                                }
                                JoinDetail tJoinDetail = tQuickProduceWabill.getJoinDetail();
                                if (tJoinDetail == null) {
                                    logger.info("打印交接清单-tJoinDetail为空");
                                    tList.add(tBasicQueryEntity);
                                    continue;
                                }
                                tBasicQueryEntity.setFcNo(tJoinDetail.getDistributeStoreId());
                                tBasicQueryEntity.setDeclaredValue(String.valueOf(tJoinDetail.getDeclaredValue()));
                                tBasicQueryEntity.setGoodValue(String.valueOf(tJoinDetail.getPrice()));
                                tBasicQueryEntity.setGoodWeight(tJoinDetail.getGoodWeight());
                                tBasicQueryEntity.setGoodWeight2(0.0);
                                tBasicQueryEntity.setPackageBarNum(BusinessHelper.getPackageNum(dSendDatail.getPackageBarcode()));
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
                    logger.error("同步运单基本信息异常错误原因为" + e.getMessage());
                }
            }
        }
        tBasicQueryEntityResponse.setCode(JdResponse.CODE_OK);
        tBasicQueryEntityResponse.setMessage(message);
        tBasicQueryEntityResponse.setData(tList);
        Date endDate = new Date();
        logger.info("打印交接清单-detailPrintQuery结束-" + (startDate.getTime() - endDate.getTime()));
        return tBasicQueryEntityResponse;
    }


    public BasicQueryEntityResponse newBasicPrintQuery(PrintQueryCriteria criteria){
        Date startDate = new Date();
        logger.info("打印交接清单-基本信息查询开始" + DateHelper.formatDate(startDate));
        BasicQueryEntityResponse tBasicQueryEntityResponse = new BasicQueryEntityResponse();
        try {
            List<BasicQueryEntity> data = new ArrayList<BasicQueryEntity>();//
            // TODO: 2018/9/18 查es接口获取值
            tBasicQueryEntityResponse.setData(data);
        } catch (Exception e) {
            logger.error("打印明细基本查询异常", e);
            tBasicQueryEntityResponse.setCode(JdResponse.CODE_NOT_FOUND);
            tBasicQueryEntityResponse.setMessage("打印明细基本查询异常");
            return tBasicQueryEntityResponse;
        }
        Date endDate = new Date();
        logger.info("打印交接清单-基本信息查询结束-" + (startDate.getTime() - endDate.getTime()));
        return tBasicQueryEntityResponse;
    }
    /**
     * 批次汇总&&批次汇总打印 -- 新接口
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @JProfiler(jKey = "DMSWEB.SendPrintServiceImpl.batchSummaryPrintQuery", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SummaryPrintResultResponse newBatchSummaryPrintQuery(PrintQueryCriteria criteria) {
        SummaryPrintResultResponse tSummaryPrintResultResponse = new SummaryPrintResultResponse();

        // TODO: 2018/9/17 查es接口
        List<BasicQueryEntity> basicQueryEntityList = new ArrayList<BasicQueryEntity>();

        //没有查到相关数据的情况
        if (basicQueryEntityList == null || basicQueryEntityList.size() < 1) {
            tSummaryPrintResultResponse.setCode(JdResponse.CODE_OK_NULL);
            tSummaryPrintResultResponse.setMessage(JdResponse.MESSAGE_OK_NULL);
            return tSummaryPrintResultResponse;
        }

        //按批次分类
        Map<String,List<BasicQueryEntity>> sendBaseMap = new HashMap<String, List<BasicQueryEntity>>();
        for (BasicQueryEntity basicQueryEntity : basicQueryEntityList) {
            String sendCode = basicQueryEntity.getSendCode();
            if(sendBaseMap.containsKey(sendCode)){
                sendBaseMap.get(sendCode).add(basicQueryEntity);
            }else {
                List<BasicQueryEntity> entities =  new ArrayList<BasicQueryEntity>();
                entities.add(basicQueryEntity);
                sendBaseMap.put(sendCode,entities);
            }
        }

        //遍历批次，分别组装
        List<SummaryPrintResult> summaryPrintResultList = new ArrayList<SummaryPrintResult>();
        for(String sendCode : sendBaseMap.keySet()){
            SummaryPrintResult summaryPrintResult = new SummaryPrintResult();
            summaryPrintResult.setSendCode(sendCode);
            summaryPrintResult.setSendSiteName(toSiteName(criteria.getSiteCode()));
            summaryPrintResult.setReceiveSiteName(toSiteName(criteria.getReceiveSiteCode()));

            summaryPrintResultList.add(singleSendSummary(summaryPrintResult,sendBaseMap.get(sendCode)));
        }

        //设置返回值的data
        tSummaryPrintResultResponse.setData(summaryPrintResultList);

        return tSummaryPrintResultResponse;

    }

    /**
     * 单批次数据汇总
     * @param summaryPrintResult
     * @param basicQueryEntityList
     * @return
     */
    private SummaryPrintResult singleSendSummary(SummaryPrintResult summaryPrintResult,List<BasicQueryEntity> basicQueryEntityList){
        Map<String, SummaryPrintBoxEntity> boxMap = new HashMap<String, SummaryPrintBoxEntity>();
        List<SummaryPrintBoxEntity> details = new ArrayList<SummaryPrintBoxEntity>();

        Integer totalBoxNum = 0 ; //单个批次内的箱子数量
        Integer totalPackageNum = 0; //单个批次内的包裹数量
        Double totalBoardVolume = 0.0;  //总的板体积
        Double totalOutVolumeDy = 0.0;  //总的应付自动测量体积
        Double totalOutVolumeSt = 0.0;  //总的应付人工测量体积
        Double totalInVolume = 0.0;     //总的应收体积

        String roadCode  = null; //路区号
        String sendTime = "";//发货时间

        /** 已经处理过的板号集合，每条记录的托盘体积都是整个托盘的体积，所以只需记一次 **/
        Set<String> boardVolumeSet = new HashSet<String>();
        /** 已经处理过的箱号集合，每条记录的箱体积都是整个箱的体积，所以只需记一次 **/
        Set<String> boxVolumeSet = new HashSet<String>();

        //循环处理批次内的每一条记录完成统计功能
        for(BasicQueryEntity basicQueryEntity : basicQueryEntityList) {
            //取每个批次中第一单的路区号
            if(StringUtils.isBlank(roadCode) && StringUtils.isNotBlank(basicQueryEntity.getRoadCode())){
                roadCode = basicQueryEntity.getRoadCode();
            }
            //发货时间
            if(StringUtils.isBlank(sendTime) && StringUtils.isNotBlank(basicQueryEntity.getOperateTime())){
                sendTime = basicQueryEntity.getOperateTime();
            }

            SummaryPrintBoxEntity summaryEntity = null;
            //如果是按箱处理的，把箱里的包裹进行组装
            if (BusinessHelper.isBoxcode(basicQueryEntity.getBoxCode())) {
                if (boxMap.containsKey(basicQueryEntity.getBoxCode())) {
                    summaryEntity = boxMap.get(basicQueryEntity.getBoxCode());
                    summaryEntity.setWaybillNum(summaryEntity.getWaybillNum() + 1);
                    summaryEntity.setPackageBarNum(summaryEntity.getPackageBarNum() + 1);
                    summaryEntity.setPackageBarRecNum(summaryEntity.getPackageBarRecNum() + 1);
                } else {
                    summaryEntity = new SummaryPrintBoxEntity();
                    summaryEntity.setBoxCode(basicQueryEntity.getBoxCode());
                    summaryEntity.setWaybillNum(1);
                    summaryEntity.setPackageBarNum(1);
                    summaryEntity.setPackageBarRecNum(1);
                    //设置封签号、封车时间和箱的体积
                    summaryEntity.setSealNo1(basicQueryEntity.getSealNo());
                    summaryEntity.setSealNo2("");
                    summaryEntity.setLockTime(basicQueryEntity.getSealTime());//封车时间
                    Box box = null;
                    try {
                        box = boxService.findBoxByCode(basicQueryEntity.getBoxCode());
                    } catch (Exception e) {
                        logger.error("打印交接清单获取箱号失败", e);
                    }
                    if (null != box && null != box.getLength() && null != box.getWidth() && null != box.getHeight()
                            && box.getLength() > 0 && box.getWidth() > 0 && box.getHeight() > 0) {
                        summaryEntity.setVolume(Double.valueOf(box.getLength() * box.getWidth() * box.getHeight()));
                    }
                    boxMap.put(basicQueryEntity.getBoxCode(),summaryEntity);
                }

                totalBoxNum ++;
            } else {
                //按包裹号处理的
                summaryEntity = new SummaryPrintBoxEntity();
                summaryEntity.setBoxCode(basicQueryEntity.getBoxCode());
                summaryEntity.setWaybillNum(1);
                summaryEntity.setPackageBarNum(1);
                summaryEntity.setPackageBarRecNum(1);
                summaryEntity.setSealNo1("");
                summaryEntity.setSealNo2("");
                summaryEntity.setVolume(basicQueryEntity.getGoodVolume());

                details.add(summaryEntity);

                totalPackageNum++;
            }

            /**
             * 体积汇总逻辑：
             * 有板体积以板的体积为主；
             * 没有板体积，有箱体积，则以箱体积为主；
             * 没有板体积，也没有箱体积，则以包裹体积为主；
             */

            //如果有板的体积，把已经计算过体积的板号写入boardVolumeSet，避免重复计算
            if(StringUtils.isNotBlank(basicQueryEntity.getBoardCode()) && NumberHelper.gt0(basicQueryEntity.getBoardVolume())){
                if(!boardVolumeSet.contains(basicQueryEntity.getBoardCode())){
                    boardVolumeSet.add(basicQueryEntity.getBoardCode());
                    totalBoardVolume += basicQueryEntity.getBoardVolume();
                    totalOutVolumeSt += basicQueryEntity.getBoardVolume(); //板的体积算作静态测量体积
                }
            }else if(StringUtils.isNotBlank(basicQueryEntity.getBoxCode()) && BusinessHelper.isBoxcode(basicQueryEntity.getBoxCode())){
                //没有板号，或者板的体积为空，但是有箱号（box_code字段为箱号）
                if(boxVolumeSet.contains(basicQueryEntity.getBoxCode())){
                    continue;
                }
                boxVolumeSet.add(basicQueryEntity.getBoxCode());

                totalOutVolumeDy += basicQueryEntity.getDmsOutVolumeStatic();
                totalOutVolumeSt += basicQueryEntity.getDmsOutVolumeStatic();

            }else{
                //按包裹测量
                totalOutVolumeDy += basicQueryEntity.getDmsOutVolumeStatic();
                totalOutVolumeSt += basicQueryEntity.getDmsOutVolumeStatic();
            }

            //应收体积
            totalInVolume += basicQueryEntity.getGoodVolume();
        }

        //map转换成list
        details.addAll(boxMap.values());

        summaryPrintResult.setSendTime(sendTime);
        summaryPrintResult.setRoadCode(roadCode);
        summaryPrintResult.setTotalBoxNum(totalBoxNum);
        summaryPrintResult.setTotalPackageNum(totalPackageNum);
        summaryPrintResult.setTotalBoxAndPackageNum(totalBoxNum+totalPackageNum);
        summaryPrintResult.setTotalShouldSendPackageNum(basicQueryEntityList.size());
        summaryPrintResult.setTotalRealSendPackageNum(basicQueryEntityList.size());
        summaryPrintResult.setTotalBoardVolume(totalBoardVolume);
        summaryPrintResult.setTotalOutVolumeDynamic(totalOutVolumeDy);
        summaryPrintResult.setTotalOutVolumeStatic(totalOutVolumeSt);
        summaryPrintResult.setTotalInVolume(totalInVolume);

        summaryPrintResult.setDetails(details);

        return summaryPrintResult;
    }
}
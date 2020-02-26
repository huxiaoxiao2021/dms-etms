package com.jd.bluedragon.distribution.reverse.service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.DtcDataReceiverManager;
import com.jd.bluedragon.core.base.EclpItemManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.MessageConstant;
import com.jd.bluedragon.distribution.api.request.SpareRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.log.BizOperateTypeConstants;
import com.jd.bluedragon.distribution.log.BizTypeConstants;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.log.OperateTypeConstants;
import com.jd.bluedragon.distribution.printOnline.service.IPrintOnlineService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.reverse.domain.*;
import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetail;
import com.jd.bluedragon.distribution.reverse.part.service.ReversePartDetailService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.spare.dao.SpareSortingRecordDao;
import com.jd.bluedragon.distribution.spare.domain.Spare;
import com.jd.bluedragon.distribution.spare.domain.SpareSortingRecord;
import com.jd.bluedragon.distribution.spare.service.SpareService;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.service.LossServiceManager;
import com.jd.bluedragon.utils.*;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.eclp.spare.ext.api.inbound.OrderResponse;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrder;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundSourceEnum;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.fastjson.JSONObject;
import com.jd.jmq.common.message.Message;
import com.jd.loss.client.LossProduct;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.trace.api.domain.BillBusinessTraceAndExtendDTO;
import com.jd.rd.unpack.jsf.distributionReceive.in.InOrderDto;
import com.jd.rd.unpack.jsf.distributionReceive.in.OrderDetailDto;
import com.jd.rd.unpack.jsf.distributionReceive.result.MessageResult;
import com.jd.rd.unpack.jsf.distributionReceive.service.DistributionReceiveJsfService;
import com.jd.staig.receiver.rpc.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.Base64Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Service("reverseSendService")
public class ReverseSendServiceImpl implements ReverseSendService {

    private final Logger log = LoggerFactory.getLogger(ReverseSendServiceImpl.class);

    @Autowired
    private LogEngine logEngine;

    @Autowired
    @Qualifier("bdDmsReverseSendMQ")
    private DefaultJMQProducer bdDmsReverseSendMQ;

    @Autowired
    WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private DtcDataReceiverManager dtcDataReceiverManager;

    @Autowired
    ReverseSpareService reverseSpareService;

    @Autowired
    SpareService spareService;

    @Autowired
    @Qualifier("reverseSendSpareEclpProducer")
    private DefaultJMQProducer reverseSendSpareEclpProducer;

    @Autowired
    @Qualifier("reverseSpareEclp")
    private ReverseSpareEclp reverseSpareEclp;

    @Autowired
    @Qualifier("dmsSendLossMQ")
    private DefaultJMQProducer dmsSendLossMQ;
    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private ReversePartDetailService reversePartDetailService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseService tBaseService;

    @Autowired
    private BaseWmsService baseWmsService;

    @Autowired
    private LossServiceManager lossServiceManager;

    @Qualifier("bdDmsReverseSendEclp")
    @Autowired
    private DefaultJMQProducer bdDmsReverseSendEclp;

    @Qualifier("bdDmsReverseSendCLPS")
    @Autowired
    private DefaultJMQProducer bdDmsReverseSendCLPS;

    @Autowired
    private DistributionReceiveJsfService distributionReceiveJsfService;//备件库配送入

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private WaybillService waybillService;

    @Resource
    @Qualifier("workerProducer")
    private com.jd.jmq.client.producer.MessageProducer workerProducer;

    @Autowired
    @Qualifier("eclpItemManager")
    private EclpItemManager eclpItemManager;

    @Qualifier("printOnlineProducer")
    @Autowired
    private DefaultJMQProducer printOnlineProducer;

    @Autowired
    private SpareSortingRecordDao spareSortingRecordDao;

    @Autowired
    private IPrintOnlineService printOnlineService;

    @Autowired
    private ReverseStockInDetailService reverseStockInDetailService;

    // 自营
    public static final Integer businessTypeONE = 10;
    // 退货
    public static final Integer businessTypeTWO = 20;
    // 第三方
    public static final Integer businessTypeTHR = 30;

    // 退仓储
    public static final Integer RECEIVE_TYPE_WMS = 1;
    // 退售后
    public static final Integer RECEIVE_TYPE_AMS = 2;
    // 退备件库
    public static final Integer RECEIVE_TYPE_SPARE = 3;


    public static final Map<String, String> tempMap = new ConcurrentHashMap<String, String>();

    public static final List<Integer> ASION_NO_ONE_SITE_CODE_LIST = new ArrayList<Integer>();

    //责任主体：终端编号151
    public static final String DUTY_ZD = "151";

    //责任主体：集约组编号152
    public static final String DUTY_JY = "152";

    static {
        ReverseSendServiceImpl.tempMap.put("101", "1");
        ReverseSendServiceImpl.tempMap.put("102", "16");
        ReverseSendServiceImpl.tempMap.put("103", "256");
        ReverseSendServiceImpl.tempMap.put("201", "2");
        ReverseSendServiceImpl.tempMap.put("202", "32");
        ReverseSendServiceImpl.tempMap.put("203", "512");
        ReverseSendServiceImpl.tempMap.put("301", "64");
        ReverseSendServiceImpl.tempMap.put("302", "1024");
        ReverseSendServiceImpl.tempMap.put("303", "4");
        ReverseSendServiceImpl.tempMap.put("401", "8");
        ReverseSendServiceImpl.tempMap.put("402", "128");
        initAsionNoOneSiteCodeList();
    }

    /**
     * 初始化亚一中件仓站点集合
     */
    private static void initAsionNoOneSiteCodeList() {
        String asionNoOneSiteCodesRaw = PropertiesHelper.newInstance()
                .getValue(Constants.ASION_NO_ONE_SITE_CODES_KEY);
        if (asionNoOneSiteCodesRaw != null) {
            String[] asionNoOneSiteCodes = asionNoOneSiteCodesRaw
                    .split(Constants.SEPARATOR_COMMA);
            for (String siteCodeRaw : asionNoOneSiteCodes) {
                if (siteCodeRaw != null) {
                    Integer siteCode = null;
                    try {
                        siteCode = Integer.valueOf(siteCodeRaw.trim());
                    } catch (Exception e) {
                        // do nothing
                    }
                    if (siteCode != null) {
                        ASION_NO_ONE_SITE_CODE_LIST.add(siteCode);
                    }
                }
            }
        }
        System.out.println("initAsionNoOneSiteCodeList result:"
                + ASION_NO_ONE_SITE_CODE_LIST);
    }

    @JProfiler(jKey = "DMSWORKER.ReverseSendService.findSendwaybillMessage", mState = {JProEnum.TP})
    public boolean findSendwaybillMessage(Task task) throws Exception {
        if (task == null || task.getBoxCode() == null || task.getCreateSiteCode() == null || task.getKeyword2() == null) {
            return true;
        }
        this.log.info("task处理的批次号为:{}", task.getBoxCode());
        try {
            String taskId = String.valueOf(task.getId());
            List<SendM> allsendList = null;
            List<SendM> sendList = new ArrayList<SendM>();

            SendM tSendM = null;
            Boolean bl = false;

            allsendList = this.sendMDao.selectBySiteAndSendCode(task.getCreateSiteCode(), task.getBoxCode());

            for (int i = 0; i < allsendList.size(); i++) {
                tSendM = allsendList.get(i);
                if (tSendM.getSendType() == 20) {
                    sendList.add(tSendM);
                }
            }

            this.log.debug("处理退货数据开始");
            this.log.debug("处理SendM数量为:{}", sendList.size());
            if (sendList == null || sendList.isEmpty()) {
                return true;
            }
            SendM sendM = new SendM();
            Integer siteType = 0;
            Integer baseOrgId = 0;
            String baseStoreId = "";

            sendM = sendList.get(0);
            BaseStaffSiteOrgDto bDto = null;
            try {
                bDto = this.baseMajorManager.getBaseSiteBySiteId(sendM.getReceiveSiteCode());
            } catch (Exception e) {
                log.error("查询获取目的地信息失败，siteCode：{}", sendM.getReceiveSiteCode(), e);
            }
            if (null != bDto) {
                siteType = bDto.getSiteType();
                baseOrgId = bDto.getOrgId();
                baseStoreId = bDto.getCustomCode();
                this.log.debug("站点类型为:{}", siteType);
                this.log.debug("baseOrgId:{}", baseOrgId);//区域号
                this.log.debug("baseStoreId:{}", baseStoreId);//仓储号
            }

            String asm_type = PropertiesHelper.newInstance().getValue("asm_type");//售后
            String wms_type = PropertiesHelper.newInstance().getValue("wms_type");//仓储
            String spwms_type = PropertiesHelper.newInstance().getValue("spwms_type");//备件库退货
            if (siteType == Integer.parseInt(asm_type)) {
                bl = this.sendReverseMessageToAms(sendM);
            } else if (siteType == Integer.parseInt(wms_type)) {
                // 采用批次号和目的地组合的方式来判断退货方向是否是亚一仓站点
                // 原因为批次号更加可靠，目的地判断方式需要依赖配置文件，而且目的地站点编码有变更的可能性
                // 组合方式可以保障在目的地变更或者判断失效的情况下，保障库内返的流程继续正常运行
                if (sendM.getSendCode().startsWith(Box.BOX_TYPE_WEARHOUSE)
                        || ((sendM != null) && isAsionNoOneSiteCode(sendM
                        .getReceiveSiteCode())))
                    bl = this.sendReverseMessageToAsiaWms(sendM, bDto);
                else
                    bl = this.sendReverseMessageToWms(sendM, bDto, taskId);
            } else if (siteType == Integer.parseInt(spwms_type)) {
                bl = this.sendReverseMessageToSpwms(sendM, baseOrgId, baseStoreId);
            } else {
                StringBuilder sb = new StringBuilder().append(asm_type).append(",").append(wms_type).append(",").append(spwms_type).append(",");
                this.log.warn("站点类型不在逆向处理范围({})内, 默认处理成功!siteCode:{}", sb, sendM.getReceiveSiteCode());
                bl = true;
            }
            if (bl) {
                //推送逆向发货汇总清单数据
                printOnlineProducer.sendOnFailPersistent(sendM.getSendCode(), sendM.getSendCode());
            }
            //直接以bl的值做返回值
            return bl;
        } catch (Exception e) {
            this.log.error("青龙逆向发货异常", e);
            return false;
        }
    }

    public boolean sendReverseMessageToAms(SendM sendM) throws Exception {
        this.log.debug("处理售后退货数据开始");
        if (sendM == null) {
            return true;
        }

        List<SendDetail> allsendList = null;
        SendDetail query = this.paramSendDetail(sendM);
        allsendList = this.sendDatailDao.queryBySiteCodeAndSendCode(query);

        dealWithWaybillCode(allsendList);
        for (SendDetail tSendDetail : allsendList) {
            ReverseSend send = new ReverseSend();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            send.setDispatchTime(df.format(tSendDetail.getOperateTime()));
            send.setOperatorId(tSendDetail.getCreateUserCode().toString());
            send.setOperatorName(tSendDetail.getCreateUser());
            send.setPackageCode(tSendDetail.getPackageBarcode());
            send.setSendCode(tSendDetail.getSendCode());
            send.setPickWareCode(tSendDetail.getPickupCode());

            try {
                /*wangtingweiDEBUGthis.messageClient.sendCustomMessage("dms_send", "VirtualTopic.bd_dms_reverse_send",
                        "java.util.String", JsonHelper.toJson(send), MessageConstant.ReverseSend.getName()
								+ tSendDetail.getPackageBarcode());*/
                String body = generateCustomerBody("dms_send", "VirtualTopic.bd_dms_reverse_send",
                        "java.util.String", JsonHelper.toJson(send), MessageConstant.ReverseSend.getName()
                                + tSendDetail.getPackageBarcode());
                log.info("推送售后MQ：{}", tSendDetail.getPackageBarcode());
                bdDmsReverseSendMQ.send(tSendDetail.getPackageBarcode(), body);
                try {
                    //业务流程监控, 售后埋点
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("packageCode", tSendDetail.getPackageBarcode());
                    Profiler.bizNode("Reverse_ws_dms2ams", data);
                } catch (Exception e) {
                    this.log.error("推送UMP发生异常.", e);
                }
                this.log.info("青龙发货至售后MQ消息成功，批次号为:{}", sendM.getSendCode());
            } catch (Exception e) {
                throw new Exception("青龙发货至售后MQ消息异常", e);
            }
        }

        return true;
    }

    private String generateCustomerBody(String executorKey,
                                        String statementId,
                                        String dataType,
                                        String data,
                                        String businessId) {
        TriggerMessage triggerMessage = new TriggerMessage();
        triggerMessage.setParameter(data);
        triggerMessage.setStatement(statementId);
        triggerMessage.setParamClassName(dataType);
        List<TriggerMessage> dataList = new ArrayList<TriggerMessage>(1);
        dataList.add(triggerMessage);
        return JsonHelper.toJson(dataList);
    }

    private class TriggerMessage {
        private String crud;
        private String statement;
        private Object parameter;
        private String paramClassName;

        public TriggerMessage() {
        }

        public String getParamClassName() {
            return this.paramClassName;
        }

        public void setParamClassName(String paramClassName) {
            this.paramClassName = paramClassName;
        }

        public String getStatement() {
            return this.statement;
        }

        public void setStatement(String statement) {
            this.statement = statement;
        }

        public Object getParameter() {
            return this.parameter;
        }

        public void setParameter(Object parameter) {
            this.parameter = parameter;
        }

        public String getCrud() {
            return this.crud;
        }

        public void setCrud(String crud) {
            this.crud = crud;
        }
    }

    private void removeDuplicatedProduct(ReverseSendAsiaWms send) {
        List<com.jd.bluedragon.distribution.reverse.domain.Product> products = send
                .getProList();
        Map<String, com.jd.bluedragon.distribution.reverse.domain.Product> productIds = new HashMap<String, com.jd.bluedragon.distribution.reverse.domain.Product>();
        if (products != null) {
            for (com.jd.bluedragon.distribution.reverse.domain.Product product : products) {
                String productId = product.getProductId();
                if (productIds.containsKey(productId)) {
                    productIds.get(productId).setProductNum(
                            productIds.get(productId).getProductNum()
                                    + product.getProductNum());
                } else {
                    productIds.put(productId, product);
                }
            }
        }
        send.setProList(new ArrayList<com.jd.bluedragon.distribution.reverse.domain.Product>(productIds.values()));
    }

    /**
     * 根据SEND_M的send_code查询sendd明细，通过箱号关联
     *
     * @return
     */
    private List<SendDetail> findSendDetailsBySendMSendCodeAndYn1AndIsCancel0(String sendCodeForSendM) {
        List<String> boxCodeList = sendMDao.selectBoxCodeBySendCodeAndCreateSiteCode(sendCodeForSendM);
        List<SendDetail> details = new ArrayList<SendDetail>();
        if (null != boxCodeList && boxCodeList.size() > 0) {
            SendDetail detail = new SendDetail();
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCodeForSendM);
            for (String item : boxCodeList) {
                if (org.apache.commons.lang.StringUtils.isBlank(item)) {
                    continue;
                }
                detail.setBoxCode(item.trim());
                detail.setCreateSiteCode(createSiteCode);
                List<SendDetail> tempList = sendDatailDao.querySendDatailsByBoxCode(detail);
                if (null != tempList && tempList.size() > 0) {
                    details.addAll(tempList);
                }
            }
        }
        return details;
    }

    @SuppressWarnings("rawtypes")
    public boolean sendReverseMessageToAsiaWms(SendM sendM, BaseStaffSiteOrgDto bDto)
            throws Exception {
        long startTime = new Date().getTime();


        if (sendM == null) {
            return true;
        }

        this.log.info("处理亚一仓储退货数据开始,目的站点:{} 批次号:{}", sendM.getReceiveSiteCode(), sendM.getSendCode());

        try {
            Map<String, String> orderpackMap = new ConcurrentHashMap<String, String>();
            Map<String, String> orderpackMapLoss = new ConcurrentHashMap<String, String>();
            Set<String> packSet = new HashSet<String>();

            List<SendDetail> allsendList = findSendDetailsBySendMSendCodeAndYn1AndIsCancel0(sendM.getSendCode());// this.sendDatailDao.findSendDetails(this.paramSendDetail(sendM));
            Map<String, WaybillOrderCodeDto> operCodeMap = dealWithWaybillCode(allsendList);//处理T单,并将操作单号存入operCodeMap中去

            int allsendListSize = allsendList != null ? allsendList.size() : 0;
            this.log.debug("获得发货明细数量:{}", allsendListSize);

            int index = 0;
            for (SendDetail tSendDetail : allsendList) {
                packSet.add(tSendDetail.getPackageBarcode());
                if (tSendDetail.getSendType() == 20
                        && null != tSendDetail.getIsLoss()
                        && tSendDetail.getIsLoss() == 1) {
                    addMapWms(orderpackMapLoss, tSendDetail.getWaybillCode(),
                            tSendDetail.getPackageBarcode());
                } else {
                    addMapWms(orderpackMap, tSendDetail.getWaybillCode(),
                            tSendDetail.getPackageBarcode());
                }
            }
            int orderSum = orderpackMapLoss.size() + orderpackMap.size();
            int packSum = packSet.size();
            this.log.debug("orderpackMapLoss数量:{}", orderpackMapLoss.size());
            this.log.debug("orderpackMap数量:{}", orderpackMap.size());
            this.log.debug("总包裹数量:{}", packSum);

            //记录一个针对任务的日志到日志表中
            SystemLog sLogAll = new SystemLog();
            sLogAll.setKeyword2(sendM.getSendCode());
            sLogAll.setKeyword3(bDto.getSiteType().toString());
            sLogAll.setKeyword4(Long.valueOf(0));
            sLogAll.setType(Long.valueOf(12004));
            sLogAll.setContent("获得发货明细数量:" + allsendListSize + ",orderpackMapLoss数量:" + orderpackMapLoss.size() + ",orderpackMap数量:" + orderpackMap.size() + ",总包裹数量:" + packSum);

            long endTime = System.currentTimeMillis();

            JSONObject request = new JSONObject();
            request.put("sendCode", sendM.getSendCode());
            request.put("waybillCode", sLogAll.getKeyword2());
            request.put("packageCode", sLogAll.getKeyword3());
            request.put("boxCode", sLogAll.getKeyword4());

            JSONObject response = new JSONObject();
            response.put("keyword1", sLogAll.getKeyword1());
            response.put("keyword2", sLogAll.getKeyword2());
            response.put("keyword3", sLogAll.getKeyword3());
            response.put("keyword4", sLogAll.getKeyword4());
            response.put("content", sLogAll.getContent());


            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_GETSENDDETIAL.getBizTypeCode())
                    .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_GETSENDDETIAL.getOperateTypeCode())
                    .processTime(endTime,startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("ReverseSendServiceImpl#sendReverseMessageToAsiaWms")
                    .build();
           logEngine.addLog(businessLogProfiler);

           SystemLogUtil.log(sLogAll);

            // 获取站点信息
            WmsSite site = new WmsSite();
            Integer orgId = bDto.getOrgId();
            String storeCode = bDto.getStoreCode();
            Integer cky2 = Integer.parseInt(storeCode.split("-")[1]);
            Integer storeId = Integer.parseInt(bDto.getCustomCode());
            site.setOrgId(orgId);
            site.setCky2(cky2);
            site.setStoreId(storeId);

            Iterator<Entry<String, String>> iter = orderpackMap.entrySet()
                    .iterator();
            boolean ifSendSuccess = true;
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String wallBillCode = (String) entry.getKey();

                ReverseSendAsiaWms newsend = null;
                String packcodes = (String) entry.getValue();
                newsend = baseWmsService.getWaybillByOrderCode(wallBillCode, packcodes, site, false);
                removeDuplicatedProduct(newsend);
                newsend.setOrderSum(orderSum);//加入总订单数及总的包裹数
                newsend.setPackSum(packSum);

                //获得send对象,方便下方判断
                ReverseSendWms send = null;
                send = tBaseService.getWaybillByOrderCode(wallBillCode);
                if (send == null) {
                    this.log.warn("调用运单接口获得数据为空,运单号:{}", wallBillCode);
                    continue;
                }
                newsend.setOrderId(send.getOrderId());
                send.setSendCode(sendM.getSendCode());//设置批次号否则无法在ispecial的报文里添加批次号
                //迷你仓、 ECLP单独处理
                if (!isSpecial(send, wallBillCode, sendM, orderpackMap.get(wallBillCode))) {
                    newsend.setBusiOrderCode(operCodeMap.get(wallBillCode).getNewWaybillCode());
                    ifSendSuccess &= sendAsiaWMS(newsend, wallBillCode, sendM, entry, 0, bDto, orderpackMap);
                }
            }

            // 报丢订单发货
            Iterator iterator = orderpackMapLoss.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String wallBillCode = (String) entry.getKey();
                String packcodes = (String) entry.getValue();
                String orderId = operCodeMap.get(wallBillCode).getOrderId();
                // 报损总数
                int lossCount = 0;
                try {
                    lossCount = this.lossServiceManager.getLossProductCountOrderId(orderId);
                } catch (Exception e1) {
                    this.log.error("调用报损订单接口失败, 运单号为:{}", wallBillCode, e1);
                    throw new Exception("调用报损订单接口失败, 运单号为" + wallBillCode);
                }

                ReverseSendAsiaWms newsend = null;
                newsend = baseWmsService.getWaybillByOrderCode(wallBillCode, packcodes, site, false);
                removeDuplicatedProduct(newsend);
                if (lossCount != 0) {
                    // 运单系统拿出的商品明细
                    List<com.jd.bluedragon.distribution.reverse.domain.Product> sendProducts = newsend.getProList();
                    List<com.jd.bluedragon.distribution.reverse.domain.Product> sendLossProducts = new ArrayList<com.jd.bluedragon.distribution.reverse.domain.Product>();
                    // 报损系统拿出的报损明细
                    List<LossProduct> lossProducts = this.lossServiceManager.getLossProductByOrderId(orderId);

                    int loss_Count = 0;
                    if (sendProducts != null && !sendProducts.isEmpty()) {
                        for (com.jd.bluedragon.distribution.reverse.domain.Product sendProduct : sendProducts) {
                            for (LossProduct lossProduct : lossProducts) {
                                if (lossProduct.getLossCount() == 0 || sendProduct.getProductNum() == 0)
                                    continue;//说明已经计算完了，或者不符合计算条件，理论上无<0数据

                                if (lossProduct.getSku().equals(sendProduct.getProductId())) {
                                    if (null == sendProduct.getProductLoss() || "".equals(sendProduct.getProductLoss())) {
                                        loss_Count = 0;
                                    } else {
                                        loss_Count = Integer.parseInt(sendProduct.getProductLoss());
                                    }

                                    if (sendProduct.getProductNum() - lossProduct.getLossCount() < 0) {//说明产品全部报丢,产品num清0.报损产品项减去产品num
                                        lossProduct.setLossCount(lossProduct.getLossCount() - sendProduct.getProductNum());
                                        sendProduct.setProductLoss(String.valueOf(loss_Count + sendProduct.getProductNum()));
                                        sendProduct.setProductNum(0);
                                    } else {//说明部分报丢
                                        sendProduct.setProductLoss(String.valueOf(loss_Count + lossProduct.getLossCount()));
                                        sendProduct.setProductNum(sendProduct.getProductNum() - lossProduct.getLossCount());
                                        lossProduct.setLossCount(0);
                                    }
                                }
                            }
                            sendProduct.setProductPrice("0");
                            sendLossProducts.add(sendProduct);
                        }
                    }
                    newsend.setProList(sendLossProducts);
                }
                newsend.setOrderSum(orderSum);//加入总订单数及总的包裹数
                newsend.setPackSum(packSum);
                newsend.setBusiOrderCode(operCodeMap.get(wallBillCode).getNewWaybillCode());
                //获得send对象,方便下方判断
                ReverseSendWms send = null;
                send = tBaseService.getWaybillByOrderCode(wallBillCode);
                if (send == null) {
                    this.log.info("调用运单接口获得数据为空,运单号:{}", wallBillCode);
                    continue;
                }
                newsend.setOrderId(send.getOrderId());
                ifSendSuccess &= sendAsiaWMS(newsend, wallBillCode, sendM, entry, lossCount, bDto, orderpackMap);
            }
            return ifSendSuccess;
        } catch (Exception e) {
            this.log.error("{}:wms发货库房失败", sendM.getSendCode(), e);
            return false;
        }

    }

    @SuppressWarnings("rawtypes")
    public boolean sendReverseMessageToWms(SendM sendM, BaseStaffSiteOrgDto bDto, String taskId)
            throws Exception {
        long startTime = new Date().getTime();

        this.log.debug("处理仓储退货数据开始");
        if (sendM == null) {
            return true;
        }
        this.log.info("处理仓储退货数据开始,目的站点:{} 批次号:{}", sendM.getReceiveSiteCode(), sendM.getSendCode());

        try {
            Map<String, String> orderpackMap = new ConcurrentHashMap<String, String>();
            Map<String, String> orderpackMapLoss = new ConcurrentHashMap<String, String>();
            List<SendDetail> allsendList = findSendDetailsBySendMSendCodeAndYn1AndIsCancel0(sendM.getSendCode());// this.sendDatailDao.findSendDetails(this.paramSendDetail(sendM));
            Map<String, WaybillOrderCodeDto> operCodeMap = dealWithWaybillCode(allsendList);//处理T单,并将操作单号存入operCodeMap中去

            int allsendListSize = allsendList != null ? allsendList.size() : 0;
            this.log.debug("获得发货明细数量:{}", allsendListSize);
            /*** 如果该批次是否是移动仓内配的批次调DTC给wms发一新的消息 ***/
            if (allsendList != null && allsendList.size() > 0 &&
                    waybillService.isMovingWareHouseInnerWaybill(allsendList.get(0).getWaybillCode())) {
                log.info("移动仓内配单批次发消息给wms，批次号:{}", sendM.getSendCode());
                return sendMoveWarehouseInnerWaybillToWMS(allsendList, bDto);
            } else {
                int index = 0;
                for (SendDetail tSendDetail : allsendList) {
                    if (tSendDetail.getSendType() == 20
                            && null != tSendDetail.getIsLoss()
                            && tSendDetail.getIsLoss() == 1) {
                        addMapWms(orderpackMapLoss, tSendDetail.getWaybillCode(),
                                tSendDetail.getPackageBarcode());
                    } else {
                        addMapWms(orderpackMap, tSendDetail.getWaybillCode(),
                                tSendDetail.getPackageBarcode());
                    }
                }
                this.log.debug("orderpackMapLoss数量:{}", orderpackMapLoss.size());
                this.log.debug("orderpackMap数量:{}", orderpackMap.size());

                //记录一个针对任务的日志到日志表中
                SystemLog sLogAll = new SystemLog();
                sLogAll.setKeyword2(sendM.getSendCode());
                sLogAll.setKeyword3(bDto.getSiteType().toString());
                sLogAll.setKeyword4(Long.valueOf(0));
                sLogAll.setType(Long.valueOf(12004));

                sLogAll.setContent("获得发货明细数量:" + allsendListSize + ",orderpackMapLoss数量:" + orderpackMapLoss.size() + ",orderpackMap数量:" + orderpackMap.size());

                long endTime = new Date().getTime();

                JSONObject request = new JSONObject();
                request.put("sendCode", sendM.getSendCode());

                JSONObject response = new JSONObject();
                response.put("keyword1", sLogAll.getKeyword1());
                response.put("keyword2", sLogAll.getKeyword2());
                response.put("keyword3", sLogAll.getKeyword3());
                response.put("keyword4", sLogAll.getKeyword4());
                response.put("type", sLogAll.getType());
                response.put("content", sLogAll.getContent());

                BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                        .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_WMS_GETSENDDETIAL.getBizTypeCode())
                        .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_WMS_GETSENDDETIAL.getOperateTypeCode())
                        .processTime(endTime,startTime)
                        .operateRequest(request)
                        .operateResponse(response)
                        .methodName("ReverseSendServiceImpl#sendReverseMessageToWms")
                        .build();

                logEngine.addLog(businessLogProfiler);

                SystemLogUtil.log(sLogAll);

                Iterator<Entry<String, String>> iter = orderpackMap.entrySet()
                        .iterator();
                boolean ifSendSuccess = true;
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String wayBillCode = (String) entry.getKey();

                    ReverseSendWms send = makeReverseSendWms(operCodeMap.get(wayBillCode).getOldWaybillCode(), operCodeMap.get(wayBillCode).getNewWaybillCode());
                    if (send == null) {
                        continue;
                    }

                    send.setSendCode(sendM.getSendCode());//设置批次号否则无法在ispecial的报文里添加批次号
                    //迷你仓、 ECLP单独处理
                    if (!isSpecial(send, wayBillCode, sendM, orderpackMap.get(wayBillCode))) {
                        send.setBusiOrderCode(operCodeMap.get(wayBillCode).getNewWaybillCode());
                        ifSendSuccess &= sendWMSByType(send, operCodeMap.get(wayBillCode).getOldWaybillCode(), wayBillCode, sendM, entry, 0, bDto, taskId);
                    }
                }

                // 报丢订单发货
                Iterator iterator = orderpackMapLoss.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String wayBillCode = (String) entry.getKey();
                    String orderId = operCodeMap.get(wayBillCode).getOrderId();
                    // 报损总数
                    int lossCount = 0;
                    try {
                        lossCount = this.lossServiceManager
                                .getLossProductCountOrderId(orderId);
                    } catch (Exception e1) {
                        this.log.error("调用报损订单接口失败, 订单号为:{}", orderId);
                        throw new Exception("调用报损订单接口失败, 订单号为" + orderId);
                    }

                    ReverseSendWms send = makeReverseSendWms(operCodeMap.get(wayBillCode).getOldWaybillCode(), operCodeMap.get(wayBillCode).getNewWaybillCode());
                    if (log.isInfoEnabled()) {
                        log.info("3:报丢订单构建ReverseSendWms对象结果:{}", JSON.toJSONString(send));
                    }
                    if (send == null) {
                        continue;
                    }

                    if (lossCount != 0) {
                        // 运单系统拿出的商品明细
                        List<com.jd.bluedragon.distribution.reverse.domain.Product> sendProducts = null;
                        sendProducts = send.getProList();
                        List<com.jd.bluedragon.distribution.reverse.domain.Product> sendLossProducts = new ArrayList<com.jd.bluedragon.distribution.reverse.domain.Product>();

                        // 报损系统拿出的报损明细
                        List<LossProduct> lossProducts = this.lossServiceManager.getLossProductByOrderId(orderId);

                        int loss_Count = 0;
                        if (sendProducts != null && !sendProducts.isEmpty()) {
                            for (com.jd.bluedragon.distribution.reverse.domain.Product sendProduct : sendProducts) {
                                for (LossProduct lossProduct : lossProducts) {
                                    if (lossProduct.getLossCount() == 0 || sendProduct.getProductNum() == 0)
                                        continue;//说明已经计算完了，或者不符合计算条件，理论上无<0数据

                                    if (lossProduct.getSku().equals(sendProduct.getProductId()) &&
                                            (lossProduct.getPrice().compareTo(new BigDecimal(sendProduct.getProductPrice())) == 0)) {
                                        if (null == sendProduct.getProductLoss() || "".equals(sendProduct.getProductLoss())) {
                                            loss_Count = 0;
                                        } else {
                                            loss_Count = Integer.parseInt(sendProduct.getProductLoss());
                                        }

                                        if (sendProduct.getProductNum() - lossProduct.getLossCount() < 0) {//说明产品全部报丢,产品num清0.报损产品项减去产品num
                                            lossProduct.setLossCount(lossProduct.getLossCount() - sendProduct.getProductNum());
                                            sendProduct.setProductLoss(String.valueOf(loss_Count + sendProduct.getProductNum()));
                                            sendProduct.setProductNum(0);
                                        } else {//说明部分报丢
                                            sendProduct.setProductLoss(String.valueOf(loss_Count + lossProduct.getLossCount()));
                                            sendProduct.setProductNum(sendProduct.getProductNum() - lossProduct.getLossCount());
                                            lossProduct.setLossCount(0);
                                        }
                                    }
                                }
                                sendLossProducts.add(sendProduct);
                            }
                        }
                        send.setProList(sendLossProducts);
                    }
                    send.setBusiOrderCode(operCodeMap.get(wayBillCode).getNewWaybillCode());
                    ifSendSuccess &= sendWMSByType(send, operCodeMap.get(wayBillCode).getOldWaybillCode(), wayBillCode, sendM, entry, lossCount, bDto, taskId);
                }

                return ifSendSuccess;
            }
        } catch (Exception e) {
            this.log.error("{}：wms发货库房失败", sendM.getSendCode(), e);
            return false;
        }

    }

    /**
     * 获取回传运单信息
     * 并初始化病单标识
     * 初始化加履中心订单标识
     *
     * @param wayBillCode  原单号
     * @param tWayBillCode 逆向单号
     * @return
     */
    public ReverseSendWms makeReverseSendWms(String wayBillCode, String tWayBillCode) {

        ReverseSendWms send = null;//原单信息
        ReverseSendWms sendTwaybill = null;//T单信息
        boolean isSickWaybill = false;
        send = tBaseService.getWaybillByOrderCode(wayBillCode);
        if (log.isInfoEnabled()) {
            log.info("1:构建ReverseSendWms对象结果:{}", JSON.toJSONString(send));
        }

        if (send == null) {
            this.log.info("调用运单接口获得数据为空,运单号:{}", wayBillCode);
            return null;
        }

        if (wayBillCode != null && tWayBillCode != null && !wayBillCode.equals(tWayBillCode)) {
            sendTwaybill = tBaseService.getWaybillByOrderCode(tWayBillCode);//根据T单号获取运单信息 operCodeMap.get(wayBillCode)
        }

        if (sendTwaybill != null) {
            //发生换单
            isSickWaybill = BusinessUtil.isSick(sendTwaybill.getWaybillSign());//waybillSign第34位为2则视为病单
        } else {
            //未发生换单
            isSickWaybill = BusinessUtil.isSick(send.getWaybillSign());
        }

        if (!isSickWaybill) {
            //原单不是病单   再去通过JSF服务返回的featureType=30判定病单标识  这样做可以避免 现场为操作异常处理
            Integer featureType = jsfSortingResourceService.getWaybillCancelByWaybillCode(wayBillCode);
            if (featureType != null) {
                isSickWaybill = Constants.FEATURE_TYPCANCEE_SICKL.equals(featureType);
            }
        }

        send.setSickWaybill(isSickWaybill);

        //一盘货变更订单号获取来源 从新单获取
        if (sendTwaybill != null && BusinessUtil.isYiPanHuoOrder(sendTwaybill.getWaybillSign())) {
            send.setOrderId(sendTwaybill.getSpareColumn3());
        }


        //初始化加履中心订单
        //金鹏退仓修改字段 OrderId 初始化商品信息（原商品信息已被初始化） OrderSource
        if (BusinessUtil.isPerformanceOrder(send.getWaybillSign())) {
            send.setOrderSource(ReverseSendWms.ORDER_SOURCE_JLZX);
            send.setOrderId(send.getBusiOrderCode());
        }
        if (log.isInfoEnabled()) {
            log.info("2:构建ReverseSendWms对象结果:{}", JSON.toJSONString(send));
        }
        return send;
    }

    /**
     * 移动仓内配单发货消息推送到WMS
     *
     * @param sendDetailList
     * @param bDto
     * @return
     */
    private boolean sendMoveWarehouseInnerWaybillToWMS(List<SendDetail> sendDetailList, BaseStaffSiteOrgDto bDto) {
        long startTime = new Date().getTime();

        //获取cky2、库房号信息
        Integer orgId = bDto.getOrgId();
        String dmdStoreId = bDto.getStoreCode();

        String[] cky2AndStoreId = dmdStoreId.split("-");
        String cky2 = cky2AndStoreId[1];
        String storeId = cky2AndStoreId[2];

        //获取批次内所有运单号
        Set<String> waybillCodeSet = new HashSet<String>();
        for (SendDetail sendDetail : sendDetailList) {
            waybillCodeSet.add(sendDetail.getWaybillCode());
        }
        SendDetail detail = sendDetailList.get(0);

        //组装传给WMS的报文
        MovingWarehouseInnerWaybill waybill = new MovingWarehouseInnerWaybill();
        waybill.setBusiOrderCode(detail.getSendCode());
        waybill.setDeliveryCenterId(cky2);
        waybill.setWarehouseId(storeId);
        waybill.setCaseNos(new ArrayList<String>(waybillCodeSet));

        //调DTC接口给WMS发报文
        String target = orgId + "," + cky2 + "," + storeId;
        com.jd.staig.receiver.rpc.Result result = new Result();
        String outboundType = "transBoxFromDMS_parce";
        String messageValue = JSON.toJSONString(waybill);
        String source = "DMS";
        try {
            log.info("移动仓内配单发货信息推送给WMS.参数：target:{},outboundType:{},messageValue:{},source:{}，outboundNo:{}"
                    , target, outboundType, messageValue, source, detail.getSendCode());
            result = this.dtcDataReceiverManager.downStreamHandle(target, outboundType, messageValue, source, detail.getSendCode());
            if (result == null) {
                log.warn("移动仓内配单发货信息推送给WMS失败.返回值为空");
                return false;
            }
            log.info("移动仓内配单发货信息推送给WMS.推送结果为：" + JSON.toJSONString(result));
            if (result.getResultCode() != 1) {
                log.warn("移动仓内配单发货信息推送给WMS失败.推送结果为:{};推送报文:{}", JSON.toJSONString(result), messageValue);
                return false;
            }
        } catch (Exception e) {
            log.error("移动仓内配单发货信息推送给WMS异常.推送报文:{}", messageValue, e);
            return false;
        } finally {
            //写系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword2(detail.getSendCode());
            sLogDetail.setKeyword3(target);
            if (result != null) {
                sLogDetail.setKeyword4(Long.valueOf(result.getResultCode()));
            }
            sLogDetail.setType(Long.valueOf(12005));
            sLogDetail.setContent(messageValue);

            long endTime = System.currentTimeMillis();

            JSONObject request = new JSONObject();
            request.put("waybillCode", detail.getSendCode());
            request.put("sendCode", detail.getSendCode());

            JSONObject response = new JSONObject();
            response.put("keyword1", sLogDetail.getKeyword1());
            response.put("keyword2", sLogDetail.getKeyword2());
            response.put("keyword3", sLogDetail.getKeyword3());
            response.put("keyword4", sLogDetail.getKeyword4());
            response.put("type", sLogDetail.getType());
            response.put("content", sLogDetail.getContent());


            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_MOBILE_DELIVERY.getBizTypeCode())
                    .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_MOBILE_DELIVERY.getOperateTypeCode())
                    .processTime(endTime,startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("ReverseSendServiceImpl#sendMoveWarehouseInnerWaybillToWMS")
                    .build();

            logEngine.addLog(businessLogProfiler);

            SystemLogUtil.log(sLogDetail);
        }

        return true;
    }


    @SuppressWarnings("rawtypes")
    public boolean sendWMS(ReverseSendWms send, String wallBillCode, SendM sendM, Map.Entry entry, int lossCount,
                           BaseStaffSiteOrgDto bDto, String taskId) throws Exception {
        long startTime = new Date().getTime();


        if (log.isInfoEnabled()) {
            log.info("5:sendWMS send参数:{}", JSON.toJSONString(send));
        }
        Integer orgId = bDto.getOrgId();
        String dmdStoreId = bDto.getStoreCode();

        String[] cky2AndStoreId = dmdStoreId.split("-");
        String cky2 = cky2AndStoreId[1];
        String storeId = cky2AndStoreId[2];

        String target = orgId + "," + cky2 + "," + storeId;

        String messageValue = "";
        String outboundNo = wallBillCode;
        String outboundType = "OrderBackDl"; // OrderBackDl
        String source = "DMS";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.log.info("仓储逆向发货信息为：{}-{}-{}", send.getCky2(), send.getOrgId(), send.getStoreId());
        send.setOperateTime(df.format(sendM.getOperateTime()));
        send.setUserName(sendM.getCreateUser());
        send.setLossQuantity(lossCount);
        send.setSendCode(sendM.getSendCode());

        send.setIsInStore(0);
        send.setToken(send.isSickWaybill() ? taskId : "");//病单加token标识（仓储只关注是否为空，任务号方便我方根据报文核查）
        try {
            //按收货仓进行赋值，覆盖运单中发货仓值，支持异仓退货
            send.setOrgId(orgId);
            send.setCky2(Integer.valueOf(cky2.trim()));
            send.setStoreId(Integer.valueOf(storeId.trim()));
        } catch (Exception e) {
            this.log.error("青龙发货至仓储WS消息send收货仓信息赋值出错", e);
        }

        messageValue = XmlHelper.toXml(send, ReverseSendWms.class);
        this.log.info("仓储逆向发货XML为：{}", messageValue);
        this.log.info("仓储逆向发货target为：{}", target);
        com.jd.staig.receiver.rpc.Result result = null;
        try {
            result = this.dtcDataReceiverManager.downStreamHandle(target, outboundType, messageValue, source, outboundNo);
            try {
                //业务流程监控, 仓储埋点
                Map<String, String> data = new HashMap<String, String>();
                data.put("outboundNo", outboundNo);
                Profiler.bizNode(outboundType, data);
            } catch (Exception e) {
                this.log.error("推送UMP发生异常.", e);
            }
            if (log.isInfoEnabled()) {
                this.log.info(JsonHelper.toJson(result));
            }
        } catch (Exception e) {
            this.log.error("青龙发货至仓储WS消息失败，运单号为:{}", wallBillCode, e);
            return false;
        } finally {
            //增加系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword1(wallBillCode);
            sLogDetail.setKeyword2(sendM.getSendCode());
            sLogDetail.setKeyword3(target);
            if (result == null) {
                sLogDetail.setKeyword4(Long.valueOf(Constants.RESULT_ERROR));
            } else {
                sLogDetail.setKeyword4(Long.valueOf(result.getResultCode()));
            }
            sLogDetail.setType(Long.valueOf(12004));
            sLogDetail.setContent(messageValue);

            long endTime = System.currentTimeMillis();

            JSONObject request = new JSONObject();
            request.put("waybillCode", wallBillCode);
            request.put("sendCode", sendM.getSendCode());

            JSONObject response = new JSONObject();
            response.put("keyword1", sLogDetail.getKeyword1());
            response.put("keyword2", sLogDetail.getKeyword2());
            response.put("keyword3", sLogDetail.getKeyword3());
            response.put("keyword4", sLogDetail.getKeyword4());
            response.put("type", sLogDetail.getType());
            response.put("content", sLogDetail.getContent());

            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_DELIVERY.getBizTypeCode())
                    .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_DELIVERY.getOperateTypeCode())
                    .processTime(endTime,startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .reMark(target)
                    .methodName("ReverseSendServiceImpl#sendWMS")
                    .build();

            logEngine.addLog(businessLogProfiler);
            SystemLogUtil.log(sLogDetail);
        }

        if (result == null) {
            return false;
        }

        this.log.debug("青龙发货至仓储WS接口访问成功，result.getResultCode()={}", result.getResultCode());
        this.log.debug("青龙发货至仓储WS接口访问成功，result.getResultMessage()={}", result.getResultMessage());
        this.log.debug("青龙发货至仓储WS接口访问成功，result.getResultValue()={}", result.getResultValue());

        if (result.getResultCode() == 1) {
            this.log.info("青龙发货至仓储WS消息成功，运单号为:{}", wallBillCode);
            if (!send.isSickWaybill()) {//病单屏蔽报丢报损MQ
                //向报丢系统发送订单消息，锁定报丢，不再允许报丢，直至被驳回
                this.log.info("回传MQ消息给报损系统，锁定定单不让再提报损，运单号为:{}", wallBillCode);
                sendReportLoss(wallBillCode, RECEIVE_TYPE_WMS, sendM.getCreateSiteCode(), sendM.getReceiveSiteCode());
            }
        } else {
            this.log.warn("青龙发货至仓储WS消息失败，result.getResultCode()={}", result.getResultCode());
            this.log.warn("青龙发货至仓储WS消息失败，result.getResultMessage()={}", result.getResultMessage());
            this.log.warn("青龙发货至仓储WS消息失败，result.getResultValue()={}", result.getResultValue());
            this.log.warn("青龙发货至仓储WS消息失败，运单号为:{}", wallBillCode);
            return false;
        }

        return true;
    }


    @SuppressWarnings("rawtypes")
    public boolean sendAsiaWMS(ReverseSendAsiaWms send, String wallBillCode, SendM sendM, Map.Entry entry, int lossCount,
                               BaseStaffSiteOrgDto bDto, Map<String, String> isPackageFullMap) throws Exception {
        long startTime = new Date().getTime();

        Integer orgId = bDto.getOrgId();
        String dmdStoreId = bDto.getStoreCode();

        String[] cky2AndStoreId = dmdStoreId.split("-");
        String cky2 = cky2AndStoreId[1];
        String storeId = cky2AndStoreId[2];

        String target = orgId + "," + cky2 + "," + storeId;

        String messageValue = "";
        String outboundNo = wallBillCode;
        String outboundType = "OrderBackDl"; // OrderBackDl
        String source = "DMS";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.log.info("仓储逆向发货信息为：{}-{}-{}", send.getCky2(), send.getOrgId(), send.getStoreId());
        send.setOperateTime(df.format(sendM.getOperateTime()));
        send.setUserName(sendM.getCreateUser());
        send.setLossQuantity(lossCount);
        send.setSendCode(sendM.getSendCode());
        if (sendM.getSendCode().startsWith(Box.BOX_TYPE_WEARHOUSE)) {
            // 库内返
            send.setIsInStore(1);
        } else {
            // 库外返
            send.setIsInStore(0);
        }
        send.setPackageCodes((String) entry.getValue());
        //已经在获得运单时将收货仓信息赋值，不用重复赋值
//		send.setOrgId(orgId);
//		send.setCky2(Integer.valueOf(cky2.trim()));
//		send.setStoreId(Integer.valueOf(storeId.trim()));

        messageValue = XmlHelper.toXml(send, ReverseSendAsiaWms.class);

        this.log.info("仓储逆向发货XML为：{}", messageValue);
        this.log.info("仓储逆向发货target为：{}", target);
        com.jd.staig.receiver.rpc.Result result = null;
        try {
            result = this.dtcDataReceiverManager.downStreamHandle(target, outboundType, messageValue, source, outboundNo);
            try {
                //业务流程监控, 仓储埋点
                Map<String, String> data = new HashMap<String, String>();
                data.put("outboundNo", outboundNo);
                Profiler.bizNode(outboundType, data);
            } catch (Exception e) {
                this.log.error("推送UMP发生异常.", e);
            }
            if (log.isInfoEnabled()) {
                this.log.info(JsonHelper.toJson(result));
            }
        } catch (Exception e) {
            this.log.error("青龙发货至仓储WS消息失败，运单号为:{}", wallBillCode, e);
            return false;
        } finally {
            //增加系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword1(wallBillCode);
            sLogDetail.setKeyword2(sendM.getSendCode());
            sLogDetail.setKeyword3(target);
            if (result == null) {
                sLogDetail.setKeyword4(Long.valueOf(Constants.RESULT_ERROR));
            } else {
                sLogDetail.setKeyword4(Long.valueOf(result.getResultCode()));
            }
            sLogDetail.setType(Long.valueOf(12004));
            sLogDetail.setContent(messageValue);

            long endTime = System.currentTimeMillis();

            JSONObject request = new JSONObject();
            request.put("waybillCode", sLogDetail.getKeyword1());
            request.put("sendCode", sendM.getSendCode());


            JSONObject response = new JSONObject();
            response.put("keyword1", sLogDetail.getKeyword1());
            response.put("keyword2", sLogDetail.getKeyword2());
            response.put("keyword3", sLogDetail.getKeyword3());
            response.put("keyword4", sLogDetail.getKeyword4());
            response.put("type", sLogDetail.getType());
            response.put("content", sLogDetail.getContent());

            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_DELIVERY.getBizTypeCode())
                    .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_DELIVERY.getOperateTypeCode())
                    .processTime(endTime,startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .reMark(target)
                    .methodName("ReverseSendServiceImpl#sendAsiaWMS")
                    .build();

            logEngine.addLog(businessLogProfiler);

            SystemLogUtil.log(sLogDetail);
        }
        if (result == null) {
            return false;
        }

        this.log.debug("青龙发货访问仓储WS接口成功，result.getResultCode()={}", result.getResultCode());
        this.log.debug("青龙发货访问仓储WS接口成功，result.getResultMessage()={}", result.getResultMessage());
        this.log.debug("青龙发货访问仓储WS接口成功，result.getResultValue()={}", result.getResultValue());
        if (result.getResultCode() == 1) {
            this.log.info("青龙发货至仓储WS消息成功，运单号为:{}", wallBillCode);
            //向报丢系统发送订单消息，锁定报丢，不再允许报丢，直至被驳回
            sendReportLoss(wallBillCode, RECEIVE_TYPE_WMS, sendM.getCreateSiteCode(), sendM.getReceiveSiteCode());
        } else {
            this.log.warn("青龙发货至仓储WS消息失败，result.getResultCode()={}", result.getResultCode());
            this.log.warn("青龙发货至仓储WS消息失败，result.getResultMessage()={}", result.getResultMessage());
            this.log.warn("青龙发货至仓储WS消息失败，result.getResultValue()={}", result.getResultValue());
            this.log.warn("青龙发货至仓储WS消息失败，运单号为:{}", wallBillCode);
            return false;
        }

        return true;
    }

    public static void addMapWms(Map<String, String> m, String a, String b) {
        if (m.containsKey(a)) {
            //如果有重复包裹去重、
            if (!m.get(a).contains(b)) {
                m.put(a, m.get(a) + Constants.SEPARATOR_COMMA + b);
            }
        } else {
            m.put(a, b);
        }
    }

    public static void addMapSpwms(Map<String, SendDetail> map, String waybillCode, SendDetail sendDetail) {
        if (sendDetail.getSendType() == 20 && !map.containsKey(waybillCode)) {
            map.put(waybillCode, sendDetail);
        }
    }

    @SuppressWarnings("rawtypes")
    public boolean sendReverseMessageToSpwms(SendM sendM, Integer baseOrgId, String baseStoreId) throws Exception {
        if (sendM == null) {
            this.log.warn("reverse_spwms:sendM数据为空");
            return true;
        }

        List<SendDetail> sendDetails = this.sendDatailDao.queryBySiteCodeAndSendCode(this.paramSendDetail(sendM));

        if (sendDetails == null || sendDetails.size() == 0) {
            this.log.warn("reverse_spwms:sendD数据为空:{}", sendM.getSendCode());
            return true;
        }

        //------------------------维修外单---start--------------------------
        List<SendDetail> vySendDetails = new ArrayList<SendDetail>();
        List<SendDetail> nomarlSendDetails = new ArrayList<SendDetail>();
        List<SendDetail> eclpSendDetails = new ArrayList<SendDetail>(); //ECLP订单集合
        for (SendDetail sd : sendDetails) {//剔除维修外单
            if (WaybillUtil.isMCSCode(sd.getWaybillCode())) {
                vySendDetails.add(sd);
            } else if (!WaybillUtil.isReverseSpareCode(sd.getWaybillCode())) {
                Waybill waybill = waybillCommonService.findByWaybillCode(sd.getWaybillCode());
                if (waybill != null && checkIsPureMatchOrWarehouse(waybill)) {
                    eclpSendDetails.add(sd);
                } else {
                    nomarlSendDetails.add(sd);
                }
            } else {
                nomarlSendDetails.add(sd);
            }
        }
        sendDetails = nomarlSendDetails;//非维修外单集合        
        pushMCSMessageToSpwms(vySendDetails);//维修外单发送
//        pushECLPMessageToSpwms(eclpSendDetails);//ECLP
        //退备件库给ECLP发消息改成jsf接口的形式
        pushInboundOrderToSpwms(eclpSendDetails);


        //------------------------维修外单---end----------------------------

        // 增加判断d表中数据为逆向数据
        Map<String, SendDetail> sendDetailMap = new ConcurrentHashMap<String, SendDetail>();
        for (SendDetail aSendDetail : sendDetails) {
            ReverseSendServiceImpl.addMapSpwms(sendDetailMap, aSendDetail.getWaybillCode(), aSendDetail);
        }

        Iterator<Entry<String, SendDetail>> iterator = sendDetailMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String waybillCode = (String) entry.getKey();
            SendDetail sendDetail = (SendDetail) entry.getValue();
            try {
                Waybill waybill = this.getWaybill(waybillCode);
                if (null == waybill) {
                    this.log.warn("{}||ReverseSendServiceImpl -- > sendReverseMessageToSpwms 获取订单数据失败", waybillCode);
                    continue;
                }

                List<ReverseSpare> sendReverseSpare = new ArrayList<ReverseSpare>();
                // 如果D表的包裹号不为备件库条码时，需要新增spare表数据并关联备件库条码
                if (!WaybillUtil.isReverseSpareCode(sendDetail.getPackageBarcode())) {
                    List<ReverseSpare> reverseSpare = this.reverseSpareService.queryByWayBillCode(waybillCode,
                            sendDetail.getSendCode());
                    List<Product> products = waybill.getProList();
                    if (products == null || products.size() == 0) {
                        this.log.warn("{}||ReverseSendServiceImpl -- > sendReverseMessageToSpwms 获取商品明细为空", waybillCode);
                        /**
                         * products在查运单信息时默认给力空对象，这里避免上游逻辑更改，判断为null时赋一个空List，避免报空指针，此处更改不影响后续逻辑
                         */
                        if (products == null) {
                            products = new ArrayList<Product>();
                        }
                    }
                    List<Spare> spares = this.getSpare(baseOrgId, Integer.parseInt(baseStoreId), sendDetail, products);
                    if (spares == null || spares.isEmpty()) {
                        this.log.warn("{}||ReverseSendServiceImpl -- > sendReverseMessageToSpwms,获取备件条码为空", waybillCode);
                        continue;
                    }
                    int spare_num = 0;

                    List<ReverseSendSpwmsOrder> spwmsOrders = new ArrayList<ReverseSendSpwmsOrder>();
                    for (int i = 0; i < products.size(); i++) {
                        for (int j = 0; j < products.get(i).getQuantity(); j++) {
                            ReverseSendSpwmsOrder spwmsOrder = new ReverseSendSpwmsOrder();
                            spwmsOrder.setWaybillCode(waybillCode);
                            spwmsOrder.setSendCode(sendDetail.getSendCode());
                            spwmsOrder.setSpareCode(spares.get(spare_num++).getCode());
                            spwmsOrder.setProductId(products.get(i).getProductId());
                            spwmsOrder.setProductName(products.get(i).getName());
                            spwmsOrder.setProductPrice(products.get(i).getPrice().doubleValue());
                            spwmsOrders.add(spwmsOrder);
                        }
                    }

                    List<ReverseSpare> reverseSpares = this.setReverseSpares(sendDetail, spwmsOrders);
                    if (null == reverseSpare || reverseSpare.size() == 0) {
                        sendReverseSpare = reverseSpares;
                    } else {
                        for (ReverseSpare rs1 : reverseSpare) {
                            for (ReverseSpare rs2 : reverseSpares) {
                                if (rs1.getWaybillCode().equals(rs2.getWaybillCode())
                                        && rs1.getProductId().equals(rs2.getProductId())) {
                                    if (null != rs1.getSpareTranCode()) {
                                        reverseSpares.remove(rs2);
                                        break;
                                    } else {
                                        String temp = rs2.getSpareCode();
                                        org.apache.commons.beanutils.BeanUtils.copyProperties(rs2, rs1);
                                        rs2.setSpareCode(temp);
                                        break;
                                    }
                                }
                            }
                        }

                        sendReverseSpare = reverseSpares;
                        this.log.info("{}-处理备件库退货,List<ReverseSpare>={}", waybillCode, sendReverseSpare.size());
                    }
                } else {
                    sendReverseSpare = this.reverseSpareService.queryByWayBillCode(waybillCode,
                            sendDetail.getSendCode());
                    if (null == sendReverseSpare || sendReverseSpare.size() == 0) {
                        this.log.warn("spwms处理备件库退货出错waybillCode={}", waybillCode);
                        continue;
                    }
                }
                dealWithWaybillCode(sendDetails);

                String waybillSendCode = sendM.getSendCode() + "-" + waybillCode;
                // 开始包装发货对象 (改用unpack包下的实体类)
                InOrderDto order = new InOrderDto();
                order.setSourceId(sendDetail.getCreateSiteCode());
                order.setFromPin(sendDetail.getCreateUserCode().toString());
                order.setFromName(sendDetail.getCreateUser());
                order.setCreateReason(sendDetail.getSpareReason());
                order.setOrderId(Long.parseLong(waybill.getOrderId()));
                order.setAimOrgId(baseOrgId);
                // 逆向退备件库报文兼容性更改，唯一标识，用于备件库新老系统兼容并行
                order.setWaybillSendCode(waybillSendCode);
                order.setWaybillSign(waybill.getWaybillSign());//SOP逆向单据拒收外呼需求,需要传waybillsign

                try {
                    order.setAimStoreId(Integer.parseInt(baseStoreId));
                } catch (Exception e1) {
                    this.log.error("处理备件库退货,baseStoreId转换出错:{}", baseStoreId);
                    continue;
                }

                if (waybill.getType() >= 21 && waybill.getType() <= 25) {
                    // 200pop订单
                    order.setOrderType(200);
                } else if (waybill.getType() == 2) {
                    // 100夺宝岛订单
                    order.setOrderType(100);
                } else {
                    // 其他值其他订单
                    order.setOrderType(300);
                }

                // 判断是否奢侈品订单
                // 还需要再次确认运单返回的结果
                List<OrderDetailDto> de = new ArrayList<OrderDetailDto>();
                for (ReverseSpare reverseSpare : sendReverseSpare) {
                    OrderDetailDto orderDetail = new OrderDetailDto();
                    orderDetail.setWareId(Long.parseLong(reverseSpare.getProductId()));
                    orderDetail.setWareName(reverseSpare.getProductName());
                    orderDetail.setLossType(this.getLossType(waybill, sendDetail));

                    if (reverseSpare.getArrtCode3() == null || reverseSpare.getArrtCode3().intValue() == 0) {
                        orderDetail.setMainWareFunctionType(1024);
                    } else {
                        orderDetail.setMainWareFunctionType(Integer.parseInt(ReverseSendServiceImpl.tempMap.get(reverseSpare
                                .getArrtCode3().toString())));
                    }

                    if (reverseSpare.getArrtCode2() == null || reverseSpare.getArrtCode2().intValue() == 0) {
                        orderDetail.setMainWareAppearance(2);
                    } else {
                        orderDetail.setMainWareAppearance(Integer.parseInt(ReverseSendServiceImpl.tempMap.get(reverseSpare
                                .getArrtCode2().toString())));
                    }

                    if (reverseSpare.getArrtCode1() == null || reverseSpare.getArrtCode1().intValue() == 0) {
                        orderDetail.setMainWarePackage(1);
                    } else {
                        orderDetail.setMainWarePackage(Integer.parseInt(ReverseSendServiceImpl.tempMap.get(reverseSpare.getArrtCode1()
                                .toString())));
                    }

                    if (reverseSpare.getArrtCode4() == null || reverseSpare.getArrtCode4().intValue() == 0) {
                        orderDetail.setAttachments(8);
                    } else {
                        orderDetail.setAttachments(Integer.parseInt(ReverseSendServiceImpl.tempMap.get(reverseSpare.getArrtCode4()
                                .toString())));
                    }

                    WChoice wChoice = new WChoice();
                    wChoice.setQueryWaybillC(true);
                    wChoice.setQueryWaybillE(true);
                    wChoice.setQueryWaybillM(true);
                    try {
                        Integer consignerId = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice).getData()
                                .getWaybill().getConsignerId();

                        if (null != consignerId) {
                            orderDetail.setSupplierId(consignerId.toString());
                        } else {
                            orderDetail.setSupplierId("0");
                        }
                    } catch (Exception e) {
                        this.log.error("{}-处理备件库退货,waybillQueryWSProxy出错", waybillCode, e);
                        orderDetail.setSupplierId("0");
                    }
                    orderDetail.setPartCode(reverseSpare.getSpareCode());

                    this.log.info("{}-处理备件库退货,reverseSpare.getSpareCode={}", waybillCode, reverseSpare.getSpareCode());
                    orderDetail.setIsLuxury(this.isLuxury(waybill.getSendPay()));
                    orderDetail.setSerialNo(null);
                    de.add(orderDetail);
                }
                order.setOrderDetail(de);

                order.setFlashOrgId(waybill.getOrgId());
                order.setFlashStoreId(waybill.getStoreId());

                String jsonString = JsonHelper.toJson(order);

                this.log.warn("处理备件库退货,生成的json串为：{}", jsonString);

                //调用备货仓 jsf 接口 distributionReceiveJsfService
                MessageResult msgResult = distributionReceiveJsfService.createIn(order);
                //记录日志
                pushOrderToSpwmsLog(sendDetail, order, msgResult);
                if (null == msgResult) {
                    this.log.warn("distributionReceiveJsfService接口返回 msgResult 为空");
                    continue;
                }
                //getReturnFlag ：100 - 成功 ，200 - 异常
                if (null != msgResult.getReturnFlag() && msgResult.getReturnFlag() != 100) {
                    this.log.warn("msgResult.getReturnFlag() != 100 [{}]", msgResult.getReturnFlag());
                    continue;
                }
                String transferId = StringHelper.getStringValue(msgResult.getTransferId());
                this.log.info("{}-返回 transferId 结果:{}", waybillCode, transferId);
                if (StringHelper.isEmpty(transferId)) {
                    this.log.warn("{}-spwms发货备件库失败，返回 ErrorMessage 结果:{}", waybillCode, msgResult.getErrorMessage());
                } else {
                    //向报丢系统发送订单消息，锁定报丢，不再允许报丢，直至被驳回
                    sendReportLoss(order.getOrderId().toString(), RECEIVE_TYPE_SPARE, sendM.getCreateSiteCode(), sendM.getReceiveSiteCode());
                }

                if (StringHelper.isNotEmpty(transferId)) {
                    List<ReverseSpare> reverseSpares = new ArrayList<ReverseSpare>();
                    for (ReverseSpare aReverseSpare : sendReverseSpare) {
                        if (null == aReverseSpare.getSpareTranCode()) {
                            aReverseSpare.setSpareTranCode(transferId);
                            aReverseSpare.setWaybillSendCode(waybillSendCode);
                            reverseSpares.add(aReverseSpare);
                        }
                    }
                    this.reverseSpareService.batchAddOrUpdate(reverseSpares);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception ex) {
                this.log.error("运单号=[{}]send_d_id=[{}][spwms发货备件库失败]",
                        waybillCode, sendDetail.getSendDId(), ex);
            }
        }
        return true;
    }

    private List<ReverseSpare> setReverseSpares(SendDetail sendDetail, List<ReverseSendSpwmsOrder> spwmsOrders) {
        List<ReverseSpare> reverseSpares = new ArrayList<ReverseSpare>();
        for (ReverseSendSpwmsOrder reverseSendSpwmsOrder : spwmsOrders) {
            ReverseSpare aReverseSpare = new ReverseSpare();
            aReverseSpare.setSpareCode(reverseSendSpwmsOrder.getSpareCode());
            aReverseSpare.setSendCode(reverseSendSpwmsOrder.getSendCode());
            aReverseSpare.setWaybillCode(reverseSendSpwmsOrder.getWaybillCode());
            aReverseSpare.setProductId(reverseSendSpwmsOrder.getProductId());
            aReverseSpare.setProductCode(null);
            aReverseSpare.setProductPrice(reverseSendSpwmsOrder.getProductPrice());
            aReverseSpare.setProductName(reverseSendSpwmsOrder.getProductName());

            // 三方七折 根据send_d 新增字段判断
            if (sendDetail.getFeatureType() != null && 2 == sendDetail.getFeatureType().intValue()) {
                aReverseSpare.setArrtCode1(102);
                aReverseSpare.setArrtDesc1("商品外包装：有/非新");
                aReverseSpare.setArrtCode2(201);
                aReverseSpare.setArrtDesc2("主商品外观：好");
                aReverseSpare.setArrtCode3(303);
                aReverseSpare.setArrtDesc3("主商品功能：好");
                aReverseSpare.setArrtCode4(401);
                aReverseSpare.setArrtDesc4("附件情况：完整");
            } else {
                // 按订单分拣商品属性置为默认值
                aReverseSpare.setArrtCode1(101);
                aReverseSpare.setArrtDesc1("商品外包装：新");
                aReverseSpare.setArrtCode2(201);
                aReverseSpare.setArrtDesc2("主商品外观：新");
                aReverseSpare.setArrtCode3(302);
                aReverseSpare.setArrtDesc3("未检测");
                aReverseSpare.setArrtCode4(401);
                aReverseSpare.setArrtDesc4("完整");
            }
            reverseSpares.add(aReverseSpare);
        }
        return reverseSpares;
    }

    /**
     * 判断是否是退备件库外单(纯配/仓配)
     *
     * @param waybill
     * @return
     */
    private boolean checkIsPureMatchOrWarehouse(Waybill waybill) {
        BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill1 = waybillQueryManager.getWaybillByReturnWaybillCode(waybill.getWaybillCode());
        if (oldWaybill1 != null && oldWaybill1.getData() != null &&
                StringUtils.isNotEmpty(oldWaybill1.getData().getWaybillSign()) &&
                StringUtils.isNotEmpty(oldWaybill1.getData().getWaybillCode())) {
            if (WaybillUtil.isECLPByBusiOrderCode(oldWaybill1.getData().getBusiOrderCode())
                    && BusinessUtil.isTwiceExchageWaybillSpare(waybill.getWaybillSign())) {
                //仓配二次换单
                return true;
            }
            String oldWaybillCode1 = oldWaybill1.getData().getWaybillCode();
            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill2 = waybillQueryManager.getWaybillByReturnWaybillCode(oldWaybillCode1);
            if (oldWaybill2 != null && oldWaybill2.getData() != null &&
                    StringUtils.isNotEmpty(oldWaybill2.getData().getWaybillCode())) {
                //纯配二次换单
                String waybillSign = oldWaybill2.getData().getWaybillSign();
                if (BusinessUtil.isPurematch(waybillSign)) {
                    return true;
                }
            } else if (oldWaybill2 == null || oldWaybill2.getData() == null) {
                //纯配一次换单
                String waybillSign = oldWaybill1.getData().getWaybillSign();
                if (BusinessUtil.isPurematch(waybillSign)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private SendDetail paramSendDetail(SendM sendM) {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setSendCode(sendM.getSendCode());
        sendDetail.setCreateSiteCode(sendM.getCreateSiteCode());
        return sendDetail;
    }

    private Waybill getWaybill(String waybillCode) {
        Waybill waybill = this.waybillCommonService.findWaybillAndGoods(waybillCode);
        if (null == waybill) {
            this.log.warn("{}-ReverseSendServiceImpl --> getWaybill 获取运单数据失败", waybillCode);
        }
        return waybill;
    }

    /**
     * 为每个商品生成备件条码
     *
     * @param baseOrgId
     * @param baseStoreId
     * @param tSendDetail
     * @param products
     * @return
     */
    private List<Spare> getSpare(Integer baseOrgId, Integer baseStoreId, SendDetail tSendDetail, List<Product> products) {
        SpareRequest spareRequest = new SpareRequest();
        spareRequest.setOrgId(baseOrgId);
        spareRequest.setStoreId(baseStoreId);
        spareRequest.setUserCode(tSendDetail.getCreateUserCode());
        spareRequest.setUserName(tSendDetail.getCreateUser());
        spareRequest.setQuantity(this.getProductQuantity(products));

        // 获取备件库条码信息
        InvokeResult<List<Spare>> sparesResult = this.spareService.genCodes(spareRequest);
        if (InvokeResult.RESULT_SUCCESS_CODE == sparesResult.getCode()) {
            return sparesResult.getData();
        } else {
            log.warn("生成备件库条码失败，msg:{}", sparesResult.getMessage());
        }
        return null;
    }

    private int getProductQuantity(List<Product> products) {
        int num = 0;
        for (Product product : products) {
            num = num + product.getQuantity();
        }
        return num;
    }

    private Boolean isLuxury(String sendPay) {
        if (StringHelper.isEmpty(sendPay)) {
            return false;
        }

        String identifier = sendPay.substring(19, 20);
        return "1".equals(identifier) ? true : false;
    }

    private static void setMethodHeaders(HttpMethod httpMethod, String name, String password) {
        if (httpMethod instanceof PostMethod || httpMethod instanceof PutMethod) {
            httpMethod.setRequestHeader("Content-Type", "application/json");
        }

        httpMethod.setDoAuthentication(false);
        httpMethod.setRequestHeader("Accept", "application/json");
        httpMethod.setRequestHeader("Authorization",
                "Basic " + ReverseSendServiceImpl.base64Encode(name + ":" + password));
    }

    private static String base64Encode(String value) {
        return Base64Utility.encode(value.getBytes(Charset.defaultCharset()));
    }

    public Integer getLossType(Waybill waybill, SendDetail sendDetail) {
        Integer lossType = 4; // 默认配送损
        try {
            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(sendDetail.getCreateSiteCode());
            if (siteOrgDto != null) {
                if (BusinessHelper.isBSite(siteOrgDto.getSubType())) {
                    lossType = 15; //转运损
                }
            }
        } catch (Exception e) {
            log.error("获取转运损标识异常:{}", JsonHelper.toJson(sendDetail), e);
        }

        if (waybill != null && sendDetail != null) {

            SpareSortingRecord spareSortingRecord = null;

            try {
                spareSortingRecord = spareSortingRecordDao.getLastRecord(sendDetail.getCreateSiteCode(), waybill.getWaybillCode());
            } catch (Exception e) {
                log.error("{}-获取备件库分拣记录信息失败！", waybill.getWaybillCode(), e);
            }

            if (spareSortingRecord != null && StringHelper.isNotEmpty(spareSortingRecord.getDutyCode())) {
                if (DUTY_ZD.equals(spareSortingRecord.getDutyCode())) {
                    //终端损标识
                    lossType = 16;
                } else if (DUTY_JY.equals(spareSortingRecord.getDutyCode())) {
                    //集约损标识
                    lossType = 17;
                }
            }

            if (sendDetail.getFeatureType() != null) {
                if (2 == NumberHelper.getIntegerValue(sendDetail.getFeatureType())) {
                    lossType = 13; // 三方打折
                } else if (16 == NumberHelper.getIntegerValue(waybill.getShipmentType())) {
                    lossType = 9; // 三方包裹破损
                }
            }
            log.warn("运单号：{}退备件库损别编码为：{}", waybill.getWaybillCode(), lossType);
        }

        return lossType;
    }


    /**
     * 判断一个目的地站点是否是亚一中件仓
     *
     * @param reseiveSiteCode
     * @return
     */
    private boolean isAsionNoOneSiteCode(Integer reseiveSiteCode) {
        if (reseiveSiteCode == null) {
            return false;
        }
        return ASION_NO_ONE_SITE_CODE_LIST.contains(reseiveSiteCode);
    }

    /**
     * 备件库和仓库发货后回传MQ消息给报损系统，锁定定单不让再提报损
     *
     * @param orderId         订单id
     * @param receiveType     收货类型，区分退大库、备件库
     * @param createSiteCode  创建站点
     * @param receiveSiteCode 收货站点，在这里指的是仓库、备件库
     */
    private void sendReportLoss(String orderId, Integer receiveType, Integer createSiteCode, Integer receiveSiteCode) {

        //判断收货类型，非大库、备件库直接返回
        if (receiveType != RECEIVE_TYPE_WMS && receiveType != RECEIVE_TYPE_SPARE) return;

        ReverseReceiveLoss reverseReceiveLoss = new ReverseReceiveLoss();
        try {
            String dmsId = null;
            String dmsName = null;
            String storeId = null;
            String storeName = null;

            //仓储收货回传
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(createSiteCode);
            dmsId = dto.getSiteCode().toString();
            dmsName = dto.getSiteName();
            BaseStaffSiteOrgDto dto1 = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
            storeId = dto1.getSiteCode().toString();
            storeName = dto1.getSiteName();

            reverseReceiveLoss.setOrderId(orderId);
            reverseReceiveLoss.setReceiveType(receiveType); //发货时没有这两个字段内容
            reverseReceiveLoss.setUpdateDate(null); //因为没有这个时间

            reverseReceiveLoss.setDmsId(dmsId);
            reverseReceiveLoss.setDmsName(dmsName);
            reverseReceiveLoss.setStoreId(storeId);
            reverseReceiveLoss.setStoreName(storeName);

            reverseReceiveLoss.setIsLock(ReverseReceiveLoss.LOCK);
            String jsonStr = JsonHelper.toJson(reverseReceiveLoss);
            log.warn("青龙逆向发货后回传报损系统锁定MQ orderid为:{}", orderId);
            log.warn("青龙逆向发货后回传报损系统锁定MQ json为:{}", jsonStr);


            //this.messageClient.sendMessage("dms_send_loss", jsonStr, orderId);
            dmsSendLossMQ.send(orderId, jsonStr);
            log.info("青龙逆向发货后回传报损系统锁定MQ消息成功，订单号为:{}", orderId);
        } catch (Exception e) {
            log.error("青龙逆向发货后回传报损系统锁定MQ消息失败，订单号为:{}", orderId, e);
        }

    }


    /**
     * <p>是不是特殊处理的单子, 不需要发大库</p>
     *
     * @param send
     * @return <code>true</code> 如果是迷你仓、eclp订单
     */
    private Boolean isSpecial(ReverseSendWms send, String wayBillCode, SendM sendM, String sendPackages) {
        long startTime = new Date().getTime();

        if (StringHelper.isNotEmpty(send.getWaybillSign())) {
            //迷你仓新需求，waybillsign第一位=8的 不推送库房， 因为不属于逆向 guoyongzhi
            if ('8' == send.getWaybillSign().charAt(0)) {
                log.info("运单号： {} 的 waybillsign 【{}】 第一位是8 ,不掉用库房webservice", wayBillCode, send.getWaybillSign());
                //增加系统日志
                SystemLog sLogDetail = new SystemLog();
                sLogDetail.setKeyword1(wayBillCode);
                sLogDetail.setKeyword2(send.getSendCode());
                sLogDetail.setKeyword3("迷你仓");
                sLogDetail.setKeyword4(Long.valueOf(1));//表示处理成功
                sLogDetail.setType(Long.valueOf(12004));
                sLogDetail.setContent("不发送逆向报文!");

                long endTime = new Date().getTime();

                JSONObject request = new JSONObject();
                request.put("waybillCode", sLogDetail.getKeyword1());
                request.put("sendCode", send.getSendCode());

                JSONObject response = new JSONObject();
                response.put("keyword1", sLogDetail.getKeyword1());
                response.put("keyword2", sLogDetail.getKeyword2());
                response.put("keyword3", sLogDetail.getKeyword3());
                response.put("keyword4", sLogDetail.getKeyword4());
                response.put("type", sLogDetail.getType());
                response.put("content", sLogDetail.getContent());

                BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                        .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_SPECIAL_DELIVERY.getBizTypeCode())
                        .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_SPECIAL_DELIVERY.getOperateTypeCode())
                        .processTime(endTime,startTime)
                        .operateRequest(request)
                        .operateResponse(response)
                        .methodName("ReverseSendServiceImpl#isSpecial")
                        .build();

                logEngine.addLog(businessLogProfiler);

                SystemLogUtil.log(sLogDetail);

                return Boolean.TRUE;
            }
        }

        if (WaybillUtil.isECLPByBusiOrderCode(send.getBusiOrderCode())) {
            // 仓配ECLP的运单，若为病单，则直接发给WMS系统，不发送ECLP系统
            if (send.isSickWaybill()) {
                send.setOrderId(send.getBusiOrderCode());
                return Boolean.FALSE;
            }
            // ECLP订单 不推送wms ， 发mq
            // 发MQ-->开发平台
            log.info("运单号： {} 的 waybillsign 【{}】 =ECLP ,不掉用库房webservice", wayBillCode, send.getSourceCode());

            // 给eclp发送mq, eclp然后自己组装逆向报文
            ReverseSendMQToECLP sendmodel = new ReverseSendMQToECLP();
            sendmodel.setJdOrderCode(send.getBusiOrderCode());
            sendmodel.setSendCode(send.getSendCode());
            sendmodel.setSourceCode("ECLP");
            sendmodel.setWaybillCode(wayBillCode);
            sendmodel.setRejType(3);
            sendmodel.setRejRemark("分拣中心逆向分拣ECLP");
            if (sendM.getOperateTime() != null) {
                sendmodel.setOperateTime(sendM.getOperateTime().getTime());
            } else {
                sendmodel.setOperateTime(System.currentTimeMillis());
            }
            sendmodel.setOperator(sendM.getCreateUser());
            //组装拒收原因
            makeRefuseReason(sendmodel);

            //added by hanjiaxing3 2019.04.08
            //reason:天音项目增加两个字段，从运单接口中获取cky2和storeId
            BigWaybillDto bigWaybillDto = waybillService.getWaybillState(wayBillCode);

            if (bigWaybillDto != null && bigWaybillDto.getWaybillState() != null) {
                Integer distributeNo = bigWaybillDto.getWaybillState().getCky2();
                Integer warehouseNo = bigWaybillDto.getWaybillState().getStoreId();
                sendmodel.setDistributeNo(distributeNo);
                sendmodel.setWarehouseNo(warehouseNo);
                log.info("通过运单getWaybillState接口获取到的信息为，配送中心编号：{}，库房编号：{}", distributeNo, warehouseNo);
            } else {
                log.warn("通过运单getWaybillState接口获取到的信息为空！");
            }
            //end

            String jsonStr = JsonHelper.toJson(sendmodel);
            log.info("推送ECLP的 MQ消息体 :{}", jsonStr);

            // 增加系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword1(wayBillCode);
            sLogDetail.setKeyword2(send.getSendCode());
            sLogDetail.setKeyword3("ECLP");
            sLogDetail.setType(Long.valueOf(12004));
            sLogDetail.setContent(jsonStr);

            try {
                bdDmsReverseSendEclp.send(wayBillCode, jsonStr);
                //存入半退明细
                reversePartWmsOfEclp(wayBillCode, sendM, sendPackages);

                sLogDetail.setKeyword4(Long.valueOf(1));// 表示发送成功
            } catch (Exception e) {
                log.error("推送ECLP MQ 发生异常.", e);
                sLogDetail.setKeyword4(Long.valueOf(-1));// 表示发送失败
            }

            long endTime = new Date().getTime();

            JSONObject request = new JSONObject();
            request.put("waybillCode", sLogDetail.getKeyword1());
            request.put("sendCode",send.getSendCode());

            JSONObject response = new JSONObject();
            response.put("keyword1", sLogDetail.getKeyword1());
            response.put("keyword2", sLogDetail.getKeyword2());
            response.put("keyword3", sLogDetail.getKeyword3());
            response.put("keyword4", sLogDetail.getKeyword4());
            response.put("content", sLogDetail.getContent());

            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_ECLP_DELIVERY.getBizTypeCode())
                    .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_ECLP_DELIVERY.getOperateTypeCode())
                    .processTime(endTime,startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("ReverseSendServiceImpl#isSpecial")
                    .build();

            logEngine.addLog(businessLogProfiler);

			SystemLogUtil.log(sLogDetail);

            return Boolean.TRUE;
        }

        if (BusinessUtil.isCLPSByBusiOrderCode(send.getBusiOrderCode())) {
            // CLPS订单 不推送wms ， 发mq
            log.info("运单号：{} 的 sourceCode 【{}】 =CLPS ,不掉用库房webservice", wayBillCode, send.getSourceCode());
            ReverseSendMQToCLPS sendmodel = new ReverseSendMQToCLPS();
            sendmodel.setJdOrderCode(send.getBusiOrderCode());
            sendmodel.setSendCode(send.getSendCode());
            sendmodel.setSourceCode("CLPS");
            sendmodel.setWaybillCode(wayBillCode);
            sendmodel.setRejType(3);
            sendmodel.setRejRemark("分拣中心逆向分拣CLPS");
            String jsonStr = JsonHelper.toJson(sendmodel);
            log.info("推送CLPS的 MQ消息体:{}", jsonStr);

            // 增加系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword1(wayBillCode);
            sLogDetail.setKeyword2(send.getSendCode());
            sLogDetail.setKeyword3("CLPS");
            sLogDetail.setType(Long.valueOf(12004));
            sLogDetail.setContent(jsonStr);
            try {
                bdDmsReverseSendCLPS.send(wayBillCode, jsonStr);
                sLogDetail.setKeyword4(Long.valueOf(1));// 表示发送成功
            } catch (Exception e) {
                log.error("推送CLPS MQ 发生异常.", e);
                sLogDetail.setKeyword4(Long.valueOf(-1));// 表示发送失败
            }
            long endTime = new Date().getTime();

            JSONObject request = new JSONObject();
            request.put("waybillCode", sLogDetail.getKeyword1());
            request.put("sendCode",send.getSendCode());

            JSONObject response = new JSONObject();
            response.put("keyword1", sLogDetail.getKeyword1());
            response.put("keyword2", sLogDetail.getKeyword2());
            response.put("keyword3", sLogDetail.getKeyword3());
            response.put("keyword4", sLogDetail.getKeyword4());
            response.put("content", sLogDetail.getContent());

            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_CLPS_DELIVERY.getBizTypeCode())
                    .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_CLPS_DELIVERY.getOperateTypeCode())
                    .processTime(endTime,startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("ReverseSendServiceImpl#isSpecial")
                    .build();

            logEngine.addLog(businessLogProfiler);

            SystemLogUtil.log(sLogDetail);

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * 处理T F订单信息
     * <p>
     * 快递统一单号改造 刘铎 20181018
     * 新业务场景时不覆盖send_d对象中的数据
     *
     * @param
     * @return Map key = 入参sendList的waybill
     * value = WaybillOrderCodeDto 新老运单号以及订单号
     */
    private Map<String, WaybillOrderCodeDto> dealWithWaybillCode(List<SendDetail> sendList) {
        Map<String, WaybillOrderCodeDto> operCodeMap = new HashMap<String, WaybillOrderCodeDto>();

        for (SendDetail sendDetail : sendList) {
            String operCode = sendDetail.getWaybillCode();
            WaybillOrderCodeDto waybillOrderCodeDto = new WaybillOrderCodeDto();
            //兼容T单
            if (sendDetail.getWaybillCode().startsWith(Constants.T_WAYBILL)) {
                sendDetail.setWaybillCode(sendDetail.getWaybillCode().replaceFirst(Constants.T_WAYBILL, ""));
                waybillOrderCodeDto.setOrderId(sendDetail.getWaybillCode());
                waybillOrderCodeDto.setNewWaybillCode(operCode);
                waybillOrderCodeDto.setOldWaybillCode(sendDetail.getWaybillCode());
                operCodeMap.put(sendDetail.getWaybillCode(), waybillOrderCodeDto);//保存原操作单号

                if (sendDetail.getPackageBarcode().startsWith(Constants.T_WAYBILL))
                    sendDetail.setPackageBarcode(sendDetail.getPackageBarcode().replaceFirst(Constants.T_WAYBILL, ""));
            } else if (SerialRuleUtil.isMatchNumeric(sendDetail.getWaybillCode())) {
                //兼容 原自营订单返仓场景
                waybillOrderCodeDto.setNewWaybillCode(operCode);
                waybillOrderCodeDto.setOrderId(operCode);
                waybillOrderCodeDto.setOldWaybillCode(operCode);
                operCodeMap.put(sendDetail.getWaybillCode(), waybillOrderCodeDto);//保存原操作单号
            } else {
                //获取老运单号信息
                BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(operCode);
                if (oldWaybill != null && oldWaybill.getData() != null && StringUtils.isNotBlank(oldWaybill.getData().getWaybillCode())) {
                    //存入老运单号
                    String oldWaybillCode = oldWaybill.getData().getWaybillCode();
                    waybillOrderCodeDto.setOldWaybillCode(oldWaybillCode);
                    waybillOrderCodeDto.setNewWaybillCode(operCode);
                    waybillOrderCodeDto.setOrderId(oldWaybill.getData().getVendorId());
                    operCodeMap.put(operCode, waybillOrderCodeDto);//保存原操作单号
                } else {
                    waybillOrderCodeDto.setOldWaybillCode(operCode);
                    waybillOrderCodeDto.setNewWaybillCode(operCode);
                    Waybill waybill = waybillCommonService.findByWaybillCode(operCode);
                    if (waybill != null && StringUtils.isNotBlank(waybill.getOrderId())) {
                        waybillOrderCodeDto.setOrderId(waybill.getOrderId());
                    }
                    operCodeMap.put(operCode, waybillOrderCodeDto);//如果现场使用老单返仓场景则直接存入即可
                }
            }


        }
        return operCodeMap;
    }

    /**
     * 将维修外单的报文,推送给备件库
     * MCS : 维修外单的缩写,由备件库定义
     *
     * @param sendDetailList
     */
    private void pushMCSMessageToSpwms(List<SendDetail> sendDetailList) {
        List<Message> messageList = new ArrayList<Message>();
        final String topic = "bd_dms_reverse_send_mcs";
        try {
            for (SendDetail sendDetail : sendDetailList) {
                WChoice wChoice = new WChoice();
                wChoice.setQueryWaybillC(true);
                String pickupCode = "";
                com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> bigWaybillDto = waybillQueryManager.getDataByChoice(sendDetail.getWaybillCode(), wChoice);
                if (bigWaybillDto != null) {
                    pickupCode = bigWaybillDto.getData().getWaybill().getRelWaybillCode();
                    if (StringUtils.isBlank(pickupCode)) {
                        pickupCode = bigWaybillDto.getData().getWaybill().getBusiOrderCode();
                    }
                }

                ReverseSendMCS messageBody = new ReverseSendMCS();
                messageBody.setPickWareCode(pickupCode);
                messageBody.setSendCode(sendDetail.getSendCode());
                messageBody.setPackageCode(sendDetail.getWaybillCode());
                messageBody.setOperateTime(sendDetail.getOperateTime());
                messageBody.setOperatorId(sendDetail.getCreateUserCode());
                messageBody.setOperatorName(sendDetail.getCreateUser());
                final String jsonStr = JsonHelper.toJson(messageBody);
                messageList.add(new Message(topic, jsonStr, sendDetail.getWaybillCode()));
                if (log.isInfoEnabled()) {
                    log.info("推送MQ数据为topic:{}, text:{}", topic, jsonStr);
                }
            }
            workerProducer.send(messageList);
        } catch (Exception e) {
            log.error("推送维修外单MQ失败, 发货明细 : {}", JsonHelper.toJson(sendDetailList), e);
        }

    }


    /**
     * 退备件库给ECLP发消息改成jsf接口的形式
     *
     * @param sendDetailList
     */
    private void pushInboundOrderToSpwms(List<SendDetail> sendDetailList) {
        List<String> doneWaybill = new ArrayList<String>();
        StringBuilder failWaybillCodes = new StringBuilder();
        try {
            for (SendDetail sendDetail : sendDetailList) {
                String waybillCode = sendDetail.getWaybillCode();
                //过滤重复运单。
                if (doneWaybill.contains(waybillCode)) {
                    continue;
                }
                doneWaybill.add(waybillCode);
                InboundOrder inboundOrder =  reverseSpareEclp.makeInboundOrder(waybillCode,sendDetail);
                if(inboundOrder==null){
                    log.error("ECLP退备件库失败：{}|{}",waybillCode,sendDetail.getSendCode());
                    failWaybillCodes.append(waybillCode).append("|").append(sendDetail.getSendCode()).append(",");
                    continue;
                }
                if(log.isInfoEnabled()){
                    this.log.info("eclp退备件库报文：{}", JsonHelper.toJson(inboundOrder));
                }
                if(InboundSourceEnum.SORTING_C2C.getCode().equals(inboundOrder.getSource().getCode())){

                    if(!checkAlreadyDeal(inboundOrder)){
                        log.error("ECLP退备件库失败,检查入库单数据失败：{}|{}",waybillCode,sendDetail.getSendCode());
                        continue;
                    }
                    //记录入库单数据
                    if(!saveReverseStockInDetail(sendDetail,inboundOrder)){
                        log.error("ECLP退备件库失败,记录入库单数据失败：{}|{}",waybillCode,sendDetail.getSendCode());
                        continue;
                    }
                }
                //创建入库单
                OrderResponse orderResponse = eclpItemManager.createInboundOrder(inboundOrder);
                pushInboundOrderToSpwmsLog(sendDetail,inboundOrder,orderResponse);
                if(orderResponse != null && orderResponse.getResCode() != EclpItemManager.ORDER_RESPONSE_SUCCESS){
                    this.log.warn("ECLP退备件库失败,运单号:{},原因：{}",waybillCode, orderResponse.getMessage());
                }
                //更新入库单状态
                if(InboundSourceEnum.SORTING_C2C.getCode().equals(inboundOrder.getSource().getCode())){
                    if(!updateReverseStockInDetailStatus(sendDetail,inboundOrder,orderResponse)){
                        log.error("ECLP退备件库更新入库单记录失败：{}|{}",waybillCode,sendDetail.getSendCode());
                    }
                }

            }
            if(!StringHelper.isEmpty(failWaybillCodes.toString())){
                log.warn("eclp退备件库失败订单有:{}",failWaybillCodes.toString());
            }
        }catch (Exception e){
            log.error("ECLP退备件库异常",e);
        }
    }

    /**
     * 存储入库单数据
     *
     * @param sendDetail
     * @param inboundOrder
     * @return
     */
    private boolean saveReverseStockInDetail(SendDetail sendDetail,InboundOrder inboundOrder){

        ReverseStockInDetail reverseStockInDetail = new ReverseStockInDetail();

        reverseStockInDetail.setExternalCode(inboundOrder.getOrderNo());
        reverseStockInDetail.setWaybillCode(sendDetail.getWaybillCode());
        reverseStockInDetail.setSendCode(sendDetail.getSendCode());
        reverseStockInDetail.setCreateUser(sendDetail.getCreateUser());
        reverseStockInDetail.setCreateSiteCode(sendDetail.getCreateSiteCode());
        reverseStockInDetail.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
        reverseStockInDetail.setBusiType(ReverseStockInDetailTypeEnum.C2C_REVERSE_SPWMS.getCode());

        return reverseStockInDetailService.initReverseStockInDetail(reverseStockInDetail);
    }

    /**
     * 检查是否已经处理过，
     * 如果处理过状态是非收货情况 则操作取消
     *
     *
     * 先检查此运单是否已创建过入库单，
     * 如果创建过在成功状态时需要 触发取消 取消成功继续，取消失败拦截
     * 发货状态 暂停本次创建过程，交由其他任务创建
     * 收货状态 拦截
     * 其他情况继续
     * @param inboundOrder
     * @return
     */
    private boolean checkAlreadyDeal(InboundOrder inboundOrder){
        ReverseStockInDetail queryParam = new ReverseStockInDetail();
        queryParam.setWaybillCode(inboundOrder.getWaybillNo());
        //现在业务阶段只有C2C
        queryParam.setBusiType(ReverseStockInDetailTypeEnum.C2C_REVERSE_SPWMS.getCode());

        List<ReverseStockInDetail> reverseStockInDetails = reverseStockInDetailService.findByWaybillCodeAndType(queryParam);
        if(reverseStockInDetails.isEmpty()){
            return true;
        }
        //理论上 非终止状态的数据只会有一条
        for(ReverseStockInDetail reverseStockInDetail : reverseStockInDetails){

            if(ReverseStockInDetailStatusEnum.SUCCESS.getCode().equals(reverseStockInDetail.getStatus())){
                //此时需要调用取消接口

                if(eclpItemManager.cancelInboundOrder(inboundOrder.getWaybillNo(),inboundOrder.getTargetDeptNo(),reverseStockInDetail.getExternalCode(),inboundOrder.getSource().getCode())){
                    //更新上次记录状态 更新至取消成功状态
                    ReverseStockInDetail updateReverseStockInDetail = new ReverseStockInDetail();
                    updateReverseStockInDetail.setExternalCode(reverseStockInDetail.getExternalCode());
                    reverseStockInDetail.setWaybillCode(reverseStockInDetail.getWaybillCode());
                    reverseStockInDetail.setSendCode(reverseStockInDetail.getSendCode());
                    reverseStockInDetail.setBusiType(ReverseStockInDetailTypeEnum.C2C_REVERSE_SPWMS.getCode());
                    if(!reverseStockInDetailService.updateStatus(reverseStockInDetail,ReverseStockInDetailStatusEnum.CANCEL)){
                        log.error("ECLP退备件库更新入库单记录失败-更新上次取消状态：{}|{}",reverseStockInDetail.getWaybillCode(),reverseStockInDetail.getSendCode());
                        return false;
                    }
                    return true;
                }else{
                    log.warn("此入库单操作取消时失败，无法继续推送！，运单号{}",inboundOrder.getWaybillNo());
                    return false;
                }
            }else if(ReverseStockInDetailStatusEnum.REVERSE.getCode().equals(reverseStockInDetail.getStatus())){
                log.warn("此入库单已收货，无法继续推送！，运单号{}",inboundOrder.getWaybillNo());
                return false;
            }else if(ReverseStockInDetailStatusEnum.SEND.getCode().equals(reverseStockInDetail.getStatus())){
                log.warn("此入库单其他任务在执行，无法继续推送！，运单号{}",inboundOrder.getWaybillNo());
                return false;
            }
        }

        return true;
    }


    /**
     * 存储入库单数据
     * @param sendDetail
     * @param inboundOrder
     * @return
     */
    private boolean updateReverseStockInDetailStatus(SendDetail sendDetail,InboundOrder inboundOrder,OrderResponse orderResponse){

        ReverseStockInDetail reverseStockInDetail = new ReverseStockInDetail();

        reverseStockInDetail.setExternalCode(inboundOrder.getOrderNo());
        reverseStockInDetail.setWaybillCode(sendDetail.getWaybillCode());
        reverseStockInDetail.setSendCode(sendDetail.getSendCode());
        reverseStockInDetail.setBusiType(ReverseStockInDetailTypeEnum.C2C_REVERSE_SPWMS.getCode());
        //这种成功状态对方系统不提供常量 我们也没办法
        if(orderResponse != null && orderResponse.getResCode() != EclpItemManager.ORDER_RESPONSE_SUCCESS){
            return reverseStockInDetailService.updateStatus(reverseStockInDetail,ReverseStockInDetailStatusEnum.ERROR);
        }else{
            return reverseStockInDetailService.updateStatus(reverseStockInDetail,ReverseStockInDetailStatusEnum.SUCCESS);
        }
    }


    private void pushInboundOrderToSpwmsLog(SendDetail sendDetail,InboundOrder inboundOrder,OrderResponse orderResponse){
        long startTime = new Date().getTime();
        try{
            //增加系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword1(sendDetail.getWaybillCode());
            sLogDetail.setKeyword2(sendDetail.getSendCode());
            sLogDetail.setKeyword3("ECLPSpwms");
            if(orderResponse == null){
                sLogDetail.setKeyword4(Long.valueOf(Constants.RESULT_ERROR));
            }else{
                sLogDetail.setKeyword4(Long.valueOf(orderResponse.getResCode()));
            }
            sLogDetail.setType(Long.valueOf(12004));
            sLogDetail.setContent(JsonHelper.toJson(inboundOrder));
            long endTime = System.currentTimeMillis();

            JSONObject request = new JSONObject();
            request.put("waybillCode", sLogDetail.getKeyword1());
            request.put("sendCode", sendDetail.getSendCode());

            JSONObject response = new JSONObject();
            response.put("keyword1", sLogDetail.getKeyword1());
            response.put("keyword2", sLogDetail.getKeyword2());
            response.put("keyword3", sLogDetail.getKeyword3());
            response.put("keyword4", sLogDetail.getKeyword4());
            response.put("content", sLogDetail.getContent());

            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_ECLP_SPWMS_DELIVERY.getBizTypeCode())
                    .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_ECLP_SPWMS_DELIVERY.getOperateTypeCode())
                    .processTime(endTime,startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("ReverseSendServiceImpl#pushInboundOrderToSpwmsLog")
                    .build();

            logEngine.addLog(businessLogProfiler);

            SystemLogUtil.log(sLogDetail);
        }catch (Exception e){
            log.error("pushInboundOrderToSpwmsLogError",e);
        }
    }

    private void pushOrderToSpwmsLog(SendDetail sendDetail, InOrderDto order, MessageResult msgResult) {
        long startTime = new Date().getTime();

        try {
            //增加系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword1(sendDetail.getWaybillCode());
            sLogDetail.setKeyword2(sendDetail.getSendCode());
            sLogDetail.setKeyword3("Spwms");
            if(msgResult == null){
                sLogDetail.setKeyword4(Long.valueOf(Constants.RESULT_ERROR));
            }else{
                sLogDetail.setKeyword4(Long.valueOf(msgResult.getReturnFlag()));
            }
            sLogDetail.setType(Long.valueOf(12004));
            sLogDetail.setContent(JsonHelper.toJson(order));
            long endTime = new Date().getTime();

            JSONObject request = new JSONObject();
            request.put("waybillCode", sLogDetail.getKeyword1());
            request.put("sendCode", sendDetail.getSendCode());

            JSONObject response = new JSONObject();
            response.put("keyword1", sLogDetail.getKeyword1());
            response.put("keyword2", sLogDetail.getKeyword2());
            response.put("keyword3", sLogDetail.getKeyword3());
            response.put("keyword4", sLogDetail.getKeyword4());
            response.put("content", sLogDetail.getContent());

            BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                    .bizType(BizOperateTypeConstants.DELIVERY_REVERSE_SPWMS_DELIVERY.getBizTypeCode())
                    .operateType(BizOperateTypeConstants.DELIVERY_REVERSE_SPWMS_DELIVERY.getOperateTypeCode())
                    .processTime(endTime,startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("ReverseSendServiceImpl#pushOrderToSpwmsLog")
                    .build();

            logEngine.addLog(businessLogProfiler);

            SystemLogUtil.log(sLogDetail);
        }catch (Exception e){
            log.error("pushOrderToSpwmsLogLogError",e);
        }
    }

    public boolean sendWMSByType(ReverseSendWms send, String oldWaybillCode,String waybillCode, SendM sendM, Entry entry, int lossCount,
                                 BaseStaffSiteOrgDto bDto, String taskId) throws Exception{
        if(log.isInfoEnabled()){
            log.info("4:sendWMSByType send参数:" + JSON.toJSONString(send));
        }
        boolean isBatchSendSuccess = true ;
        String packageCodes =  (String)entry.getValue();//从map中，按原单号获取包裹号串
        if (send.isSickWaybill()) {
            String[] packageArray = packageCodes.split(Constants.SEPARATOR_COMMA);
            for (String packageCode : packageArray) {
                // 病单包裹号（一个）
                if(oldWaybillCode.equals(waybillCode)){
                    send.setPackageCodes(packageCode);
                }else{
                    //病单需要回传原包裹号
                    send.setPackageCodes(packageCode.replace(waybillCode,oldWaybillCode));
                }
                //病单增加面单实操包裹字段供WMS使用
                send.setBillPackageCode(packageCode);
                isBatchSendSuccess &= sendWMS(send, waybillCode, sendM, entry, lossCount, bDto, taskId);
            }
        } else {
            // 正常逆向单的包裹号串(至少一个)
            send.setPackageCodes(packageCodes);
            isBatchSendSuccess &= sendWMS(send, waybillCode, sendM, entry, lossCount, bDto, taskId);
        }
        return isBatchSendSuccess;
    }

    /**
     * 组装拒收原因字段
     * <p>
     * 第一步 根据新单号获取旧单号
     * 第二步 通过旧单号获取拒收原因ID
     * 第三步 根据拒收原因ID获取拒收原因名称
     *
     * @param reverseSendMQToECLP
     */
    private void makeRefuseReason(ReverseSendMQToECLP reverseSendMQToECLP) {
        String waybillCode = reverseSendMQToECLP.getWaybillCode();
        //拒收编码
        Integer refuseReasonId = null;
        //旧运单号
        String oldWaybillCode = null;
        //拒收原因
        String refuseReasonName = null;
        try {

            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);


            if (oldWaybill != null && oldWaybill.getData() != null) {
                //获取旧运单号
                oldWaybillCode = oldWaybill.getData().getWaybillCode();
            } else {
                log.warn("退ECLP增加拒收原因处理时，获取运单数据失败，sendCode = {} waybillCode={}"
                        , reverseSendMQToECLP.getSendCode(), waybillCode);
                return;
            }
            if (com.jd.common.util.StringUtils.isEmpty(oldWaybillCode)) {
                log.warn("退ECLP增加拒收原因处理时，旧运单号为空，sendCode = {} waybillCode={}"
                        , reverseSendMQToECLP.getSendCode(), waybillCode);
                return;
            }


            List<BillBusinessTraceAndExtendDTO> BillBusinessTraceAndExtendDTOs = waybillQueryManager.queryBillBTraceAndExtendByOperatorCode(oldWaybillCode, WaybillStatus.WAYBILL_TRACK_RCD.toString());
            if (BillBusinessTraceAndExtendDTOs != null && BillBusinessTraceAndExtendDTOs.size() > 0) {
                String extendProperties = BillBusinessTraceAndExtendDTOs.get(BillBusinessTraceAndExtendDTOs.size() - 1).getExtendProperties();
                //防止存在拒收无拒收原因异常
                if (StringUtils.isNotBlank(extendProperties) && ((Map) JSON.parse(extendProperties)).get("reasonId") != null) {
                    refuseReasonId = Integer.parseInt(((Map) JSON.parse(extendProperties)).get("reasonId").toString());
                }


            } else {
                log.warn("退ECLP增加拒收原因处理时，获取旧运单数据拒收原因为空，sendCode = {} oldWaybillCode={}"
                        , reverseSendMQToECLP.getSendCode(), oldWaybillCode);
                return;
            }
            if (refuseReasonId == null) {
                log.warn("退ECLP增加拒收原因处理时，拒收原因编码为空，sendCode = {} oldWaybillCode={}", reverseSendMQToECLP.getSendCode(), oldWaybillCode);
                return;
            }


            //获取拒收编码描述
            BaseDataDict refuseReason = baseMajorManager.getValidBaseDataDictListToMap(
                    13, 2, 13).get(refuseReasonId);
            if (refuseReason != null) {
                refuseReasonName = refuseReason.getTypeName();
            } else {
                log.warn("退ECLP增加拒收原因处理时，从基础资料获取拒收原因名称为空，sendCode = {} refuseReasonId={}"
                        , reverseSendMQToECLP.getSendCode(), refuseReasonId);
                return;
            }


            reverseSendMQToECLP.setRefuseReasonId(refuseReasonId);
            reverseSendMQToECLP.setRefuseReasonName(refuseReasonName);

        } catch (Exception e) {
            log.error("退ECLP增加拒收原因处理时失败，sendCode ={} waybillCode={}"
                    , reverseSendMQToECLP.getSendCode(), waybillCode, e);
        }

    }


    private void reversePartWmsOfEclp(String wayBillCode, SendM sendM, String sendPackages) {
        //判断是否为半退
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(wayBillCode, true, false, false, true);
        if (baseEntity == null || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null || baseEntity.getData().getPackageList() == null) {
            log.warn("插入半退明细时运单数据不完整");
            return;
        }
        //不支持半退 直接返回
        if (!BusinessUtil.isPartReverse(baseEntity.getData().getWaybill().getWaybillSign())) {
            return;
        }
        if (StringUtils.isNotBlank(sendPackages) && sendPackages.split(Constants.SEPARATOR_COMMA).length < baseEntity.getData().getPackageList().size()) {
            //本次发货的包裹数 小于 总计包裹数 则为半退
            //组装半退明细数据
            List<ReversePartDetail> rpds = new ArrayList<ReversePartDetail>();

            String createSiteName = baseMajorManager.getBaseSiteBySiteId(sendM.getCreateSiteCode()).getSiteName();

            String receiveSiteName = baseMajorManager.getBaseSiteBySiteId(sendM.getReceiveSiteCode()).getSiteName();

            for (String sendPackNo : sendPackages.split(Constants.SEPARATOR_COMMA)) {
                ReversePartDetail rpd = new ReversePartDetail();
                rpd.setWaybillCode(wayBillCode);
                rpd.setPackNo(sendPackNo);
                rpd.setSendCode(sendM.getSendCode());
                rpd.setAllPackSum(baseEntity.getData().getPackageList().size());
                rpd.setCreateSiteCode(sendM.getCreateSiteCode());
                rpd.setCreateSiteName(createSiteName);
                rpd.setReceiveSiteCode(sendM.getReceiveSiteCode());
                rpd.setReceiveSiteName(receiveSiteName);
                rpd.setSendTime(sendM.getOperateTime());
                rpd.setCreateUser(sendM.getCreateUser());
                rpd.setType(1);
                rpd.setStatus(1);
                rpds.add(rpd);
            }

            //批量保存 半退操作明细
            List<ReversePartDetail> bufferList = new ArrayList<ReversePartDetail>();
            for (ReversePartDetail reversePartDetail : rpds) {
                bufferList.add(reversePartDetail);
                if (bufferList.size() == 100) {
                    if (reversePartDetailService.batchInsert(bufferList)) {
                        bufferList.clear();
                    }
                }
            }
            if (bufferList.size() > 0) {
                reversePartDetailService.batchInsert(bufferList);
            }

            log.info("半退插入明细成功：{} size：{】", wayBillCode, rpds.size());

        }


    }

}

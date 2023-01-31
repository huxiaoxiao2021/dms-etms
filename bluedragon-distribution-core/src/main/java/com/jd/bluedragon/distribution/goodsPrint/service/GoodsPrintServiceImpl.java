package com.jd.bluedragon.distribution.goodsPrint.service;

import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.GoodsPrintEsManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.goodsPrint.service.domain.GoodsPrintExportDto;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月16日 16时:31分
 */
@Service("goodsPrintService")
public class GoodsPrintServiceImpl implements GoodsPrintService {
    private static final Logger log = LoggerFactory.getLogger(GoodsPrintServiceImpl.class);

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;
    @Autowired
    private GoodsPrintEsManager goodsPrintEsManager;

    @Autowired
    private SendDetailService sendDetailService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    private ExecutorService executorService = new ThreadPoolExecutor(8, 10,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue(100000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"托寄物查询打印");
        }
    });

    private static final String BARCODE_SPLITER_QUERY = "\n";
    private static final String BARCODE_SPLITER_EXPORT = "\r\n";
    private static final int MAX_NUM = 50000;
    private static final int PAGE_SIZE = 1000;

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsPrintServiceImpl.query", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<List<GoodsPrintDto>> query(GoodsPrintDto goodsPrintDto) {
        JdResponse<List<GoodsPrintDto>> jdResponse = new JdResponse();
        if (goodsPrintDto == null || goodsPrintDto.getSendCode() == null) {
            jdResponse.setCode(3001);
            jdResponse.setMessage("请输入参数");
            return jdResponse;
        }
        String[] sendCodes = goodsPrintDto.getSendCode().replace(BARCODE_SPLITER_EXPORT, BARCODE_SPLITER_QUERY).split(BARCODE_SPLITER_QUERY);
        BigDecimal count = BigDecimal.ZERO;
        Map<String, Long> realCodes = Maps.newHashMap();
        for (String sendCode : sendCodes) {
            if (BusinessHelper.isSendCode(sendCode)) {
                Long num = findGoodsPrintBySendCodeAndStatusCount(sendCode);
                if (num > 0L) {
                    count=count.add(new BigDecimal(num));
                    realCodes.put(sendCode, num);
                }
            } else {
                jdResponse.setCode(3002);
                jdResponse.setMessage("非法批次号：" + sendCode);
                return jdResponse;
            }
        }
        if (count.intValue() > MAX_NUM) {
            jdResponse.setCode(3003);
            jdResponse.setMessage("查询数量为[" + count.intValue() + "]，超过上限5万，请分批次查询");
            return jdResponse;
        }
        List<GoodsPrintDto> result = new ArrayList<GoodsPrintDto>(count.intValue());
        String sendCodeString="";
        for (String sendCode : realCodes.keySet()) {
            result.addAll(findGoodsPrintBySendCodeAndStatus(sendCode));
            sendCodeString += Constants.SEPARATOR_COMMA+sendCode;
        }
        jdResponse.setData(result);
        if (sendCodeString.length()>1){
            jdResponse.setMessage(sendCodeString.substring(1));
        }else{
            jdResponse.setMessage("");
        }
        return jdResponse;
    }

    private long findGoodsPrintBySendCodeAndStatusCount(String sendCode){
        SendDetailDto params = new SendDetailDto();
        params.setSendCode(sendCode);
        params.setCreateSiteCode(BusinessUtil.getCreateSiteCodeFromSendCode(sendCode));
        params.setOffset(1);
        params.setLimit(1);
        long num = sendDetailService.queryWaybillCountBybatchCode(params);
        log.warn("批次下数量num[{}]SendCode[{}]CreateSiteCode[{}]",num,params.getSendCode(),params.getCreateSiteCode());
        return num;
    }

    private List<GoodsPrintDto> findGoodsPrintBySendCodeAndStatus(String sendCode){
        CallerInfo info = Profiler.registerInfo("DMS.BASE.GoodsPrintServiceImpl.findGoodsPrintBySendCodeAndStatus",Constants.UMP_APP_NAME_DMSWEB, false, true);
        SendDetailDto params = new SendDetailDto();
        params.setSendCode(sendCode);
        params.setCreateSiteCode(BusinessUtil.getCreateSiteCodeFromSendCode(sendCode));
        params.setReceiveSiteCode(BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode));
        List<SendDetail> sendDetailList = findSendPageByParams(params);
        log.warn("查询总数num[{}]",sendDetailList.size());
        final BaseStaffSiteOrgDto createSite = this.baseMajorManager.getBaseSiteBySiteId(params.getCreateSiteCode());
        final BaseStaffSiteOrgDto receiveSite = this.baseMajorManager.getBaseSiteBySiteId(params.getReceiveSiteCode());
        List<GoodsPrintDto> goodsPrintDtoList = new ArrayList<>();
        Set<String> waybillCodeSet = new HashSet<>();
        List<Future<GoodsPrintDto>> futureList = new ArrayList<>();
        for (final SendDetail item:sendDetailList) {
            if(waybillCodeSet.contains(item.getWaybillCode())) {
                continue;
            }
            waybillCodeSet.add(item.getWaybillCode());
            futureList.add(executorService.submit(new Callable<GoodsPrintDto>() {
                @Override
                public GoodsPrintDto call() throws Exception {
                    return extracted(createSite, receiveSite, item);
                }
            }));
        }
        for(Future<GoodsPrintDto> future : futureList){
            try {
                GoodsPrintDto goodsPrintDto = future.get(5,TimeUnit.SECONDS);
                goodsPrintDtoList.add(goodsPrintDto);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        Profiler.registerInfoEnd(info);
        return goodsPrintDtoList;
    }

    private List<SendDetail> findSendPageByParams(SendDetailDto params){
        if(StringUtils.isEmpty(params.getSendCode())){
            return null;
        }
        Integer limit = 2000;
        Integer offset = 0;
        params.setOffset(offset);
        params.setLimit(limit);
        List<SendDetail>  result = new ArrayList<>();
        List<SendDetail>  sendDetailList = sendDetailService.findSendPageByParams(params);
        log.warn("批次下数据num[{}]params[{}]",sendDetailList.size(),JsonHelper.toJson(params));
        result.addAll(sendDetailList);
        while (sendDetailList.size() >= limit && offset < 80000){
            offset+=limit;
            params.setOffset(offset);
            sendDetailList = sendDetailService.findSendPageByParams(params);
            log.warn("批次下数据num[{}]params[{}]",sendDetailList.size(),JsonHelper.toJson(params));
            result.addAll(sendDetailList);
        }
        return result;
    }

    private GoodsPrintDto extracted(BaseStaffSiteOrgDto createSite, BaseStaffSiteOrgDto receiveSite, SendDetail item) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.GoodsPrintServiceImpl.extracted",Constants.UMP_APP_NAME_DMSWEB, false, true);
        GoodsPrintDto goodsPrintDto = new GoodsPrintDto();
        try {
            goodsPrintDto.setBoxCode(item.getBoxCode());
            goodsPrintDto.setSendCode(item.getSendCode());
            goodsPrintDto.setCreateSiteCode(item.getCreateSiteCode());
            goodsPrintDto.setWaybillCode(item.getWaybillCode());
            Waybill waybill = waybillQueryManager.getWaybillByWayCode(item.getWaybillCode());
            goodsPrintDto.setVendorId(waybill.getVendorId());
            goodsPrintDto.setWaybillCode(waybill.getWaybillCode());
            if (createSite != null) {
                goodsPrintDto.setCreateSiteCode(createSite.getSiteCode());
                goodsPrintDto.setCreateSiteName(createSite.getSiteName());
            }
            if (receiveSite != null) {
                goodsPrintDto.setReceiveSiteCode(receiveSite.getSiteCode());
                goodsPrintDto.setReceiveSiteName(receiveSite.getSiteName());
            }
            goodsPrintDto.setOperateTime(item.getOperateTime());
            if (BusinessUtil.isBoxcode(item.getBoxCode())) {
                goodsPrintDto.setBoxCode(item.getBoxCode());
            }
            if (waybill.getWaybillExt() != null && waybill.getWaybillExt().getConsignWare() != null) {
                goodsPrintDto.setConsignWare(waybill.getWaybillExt().getConsignWare());
            }
            return goodsPrintDto;
        } catch (Exception e) {
            log.error("托寄物打印封装item[{}]",JsonHelper.toJson(item),e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return goodsPrintDto;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsPrintServiceImpl.export", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public void export(GoodsPrintDto goodsPrintDto, BufferedWriter bufferedWriter) {
        try {
            long start = System.currentTimeMillis();
            // 写入表头
            Map<String, String> headerMap = getHeaderMap();
            CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());
            JdResponse<List<GoodsPrintDto>> jdResponse = query(goodsPrintDto);

            if (!jdResponse.isSucceed()){
                long end = System.currentTimeMillis();
                exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(goodsPrintDto), ExportConcurrencyLimitEnum.GOODS_PRINT_REPORT.getName(),end-start,0);
                return;
            }

            List<GoodsPrintExportDto> data = transForm(jdResponse.getData());

            // 输出至excel
            CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, data);
            long end = System.currentTimeMillis();
            exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(goodsPrintDto), ExportConcurrencyLimitEnum.GOODS_PRINT_REPORT.getName(),end-start,data.size());
        }catch (Exception e){
            log.error("跨箱号中转维护导出结果异常",e);
        }
    }

    private List<GoodsPrintExportDto> transForm(List<GoodsPrintDto> data) {
        List<GoodsPrintExportDto> list = new ArrayList<>();
        for (GoodsPrintDto goodsPrintDto1 : data) {
            GoodsPrintExportDto body  = new GoodsPrintExportDto();
            body.setSendCode(goodsPrintDto1.getSendCode());
            body.setOperateTime(goodsPrintDto1.getOperateTime() == null ? null : DateHelper.formatDate(goodsPrintDto1.getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDD));
            body.setCreateSiteName(goodsPrintDto1.getCreateSiteName());
            body.setReceiveSiteName(goodsPrintDto1.getReceiveSiteName());
            body.setBoxCode(goodsPrintDto1.getBoxCode());
            body.setVendorId(goodsPrintDto1.getVendorId());
            body.setWaybillCode(goodsPrintDto1.getWaybillCode());
            body.setConsignWare(goodsPrintDto1.getConsignWare());
           list.add(body);
        }
        return list;
    }

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        //添加表头
        headerMap.put("sendCode","发货批次号");
        headerMap.put("operateTime","发货日期");
        headerMap.put("createSiteName","始发网点");
        headerMap.put("receiveSiteName","目的网点");
        headerMap.put("boxCode","箱号");
        headerMap.put("vendorId","订单号");
        headerMap.put("waybillCode","运单号");
        headerMap.put("consignWare","托寄物品名");
        return  headerMap;
    }




    /**
     * 如果redis中读到了 key 说明已经往es写过了  后面就不重复写了
     *
     * @param key
     * @return
     */
    @Override
    public boolean getWaybillFromEsOperator(String key) {
        try {
            byte[] valueBytes = redisClientCache.get(key.getBytes());
            if (valueBytes == null) {
                return false;
            }
            log.info("[getWaybillFromEsOperator]从Redis中命中：{}",key);
            return true;
        } catch (Exception e) {
            log.error("[getWaybillFromEsOperator]从Redis中获取信息出错,key = {} 错误信息为:{}" ,key, e.getMessage());
            return false;
        }
    }

    /**
     * 写缓存 如果往es里写过，就不重复写了 同一批发货，同一批运单可能很多包裹，所以减少重复操作
     *
     * @param key
     * @return
     */
    public boolean setWaybillFromEsOperator(String key) {
        try {
            redisClientCache.setEx(key, "1", 600L, TimeUnit.SECONDS);
            log.info("[getWaybillFromEsOperator]缓存数据key:{}",key);
            return true;
        } catch (Exception e) {
            log.error("[getWaybillFromEsOperator]缓存数据出错,key = {} 错误信息为：{}" ,key, e.getMessage());
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    @Override
    public void deleteWaybillFromEsOperator(String key) {
        try {
            redisClientCache.del(key);
        } catch (Exception e) {
            log.error("[getWaybillFromEsOperator]删除数据出错,key = {} 错误信息为：{}", key, e.getMessage());
        }
    }
}

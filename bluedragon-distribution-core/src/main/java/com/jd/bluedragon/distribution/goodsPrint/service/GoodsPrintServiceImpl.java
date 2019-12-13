package com.jd.bluedragon.distribution.goodsPrint.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.GoodsPrintEsManager;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
                Long num = goodsPrintEsManager.findGoodsPrintBySendCodeAndStatusCount(sendCode);
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
//            int num = realCodes.get(sendCode).intValue();
//            int page = num / PAGE_SIZE + (num % PAGE_SIZE > 0 ? 1 : 0);
//            if (page > 1) {
//                for (int i = 1; i <= page; i++) {
//                    result.addAll(goodsPrintEsManager.findGoodsPrintBySendCodeAndStatusOfPage(sendCode,i,PAGE_SIZE));
//                }
//            } else {
                result.addAll(goodsPrintEsManager.findGoodsPrintBySendCodeAndStatus(sendCode));
//            }
            sendCodeString+=Constants.SEPARATOR_COMMA+sendCode;
        }
        jdResponse.setData(result);
        if (sendCodeString.length()>1){
            jdResponse.setMessage(sendCodeString.substring(1));
        }else{
            jdResponse.setMessage("");
        }
        return jdResponse;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.GoodsPrintServiceImpl.export", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public List<List<Object>> export(GoodsPrintDto goodsPrintDto) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();

        //添加表头
        heads.add("发货批次号");
        heads.add("发货日期");
        heads.add("始发网点");
        heads.add("目的网点");
        heads.add("箱号");
        heads.add("订单号");
        heads.add("运单号");
        heads.add("托寄物品名");
        resList.add(heads);

        JdResponse<List<GoodsPrintDto>> jdResponse = query(goodsPrintDto);
        if (!jdResponse.isSucceed()){
            return resList;
        }
        List<GoodsPrintDto> data=jdResponse.getData();
        if (data != null && data.size() > 0) {
            for (GoodsPrintDto goodsPrintDto1 : data) {
                List<Object> body = Lists.newArrayList();
                body.add(goodsPrintDto1.getSendCode());
                body.add(goodsPrintDto1.getOperateTime() == null ? null : DateHelper.formatDate(goodsPrintDto1.getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDD));
                body.add(goodsPrintDto1.getCreateSiteName());
                body.add(goodsPrintDto1.getReceiveSiteName());
                body.add(goodsPrintDto1.getBoxCode());
                body.add(goodsPrintDto1.getVendorId());
                body.add(goodsPrintDto1.getWaybillCode());
                body.add(goodsPrintDto1.getConsignWare());
                resList.add(body);
            }
        }
        return resList;
    }


    /**
     * 如果redis中读到了 key 说明已经往es写过了  后面就不重复写了
     *
     * @param key
     * @return
     */
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
    public void deleteWaybillFromEsOperator(String key) {
        try {
            redisClientCache.del(key);
        } catch (Exception e) {
            log.error("[getWaybillFromEsOperator]删除数据出错,key = {} 错误信息为：{}", key, e.getMessage());
        }
    }
}

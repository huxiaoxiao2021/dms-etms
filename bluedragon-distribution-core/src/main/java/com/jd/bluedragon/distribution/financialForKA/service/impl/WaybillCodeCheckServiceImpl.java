package com.jd.bluedragon.distribution.financialForKA.service.impl;

import com.google.common.collect.Lists;
import com.jd.bk.common.util.string.StringUtils;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.exportlog.dao.ExportLogDao;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import com.jd.bluedragon.distribution.financialForKA.dao.WaybillCodeCheckDao;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import com.jd.bluedragon.distribution.financialForKA.service.WaybillCodeCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.fastjson.JSON;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.tools.ant.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 单号校验服务实现
 *
 * @author: hujiping
 * @date: 2020/2/26 21:58
 */
@Service("waybillCodeCheckService")
public class WaybillCodeCheckServiceImpl implements WaybillCodeCheckService {

    private Logger log = LoggerFactory.getLogger(WaybillCodeCheckServiceImpl.class);

    /**
     * 单号校验结果
     */
    private static Integer SUCCESS_RESULT_NUM = 1;
    private static Integer FAIL_RESULT_NUM = 0;
    private static String SUCCESS_RESULT = "通过";
    private static String FAIL_RESULT = "失败";

    @Value("${merchant.whiteList.export.maxNum:5000}")
    private Integer exportMaxNum;
    private static final Integer EXPORT_MAX_NUM = 5000;

    @Qualifier("waybillCodeCheckMQ")
    @Autowired(required=false)
    private DefaultJMQProducer waybillCodeCheckMq;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private WaybillCodeCheckDao waybillCodeCheckDao;
    @Autowired
    private ExportLogDao exportLogDao;



    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;


    /**
     * 根据条件查询
     *
     * @param condition
     * @return
     */
    @Override
    public PagerResult<WaybillCodeCheckDto> listData(KaCodeCheckCondition condition) {
        PagerResult<WaybillCodeCheckDto> pagerResult = new PagerResult<WaybillCodeCheckDto>();
        try {
            List<WaybillCodeCheckDto> list = waybillCodeCheckDao.queryByCondition(condition);
            Integer count = waybillCodeCheckDao.queryCountByCondition(condition);
            pagerResult.setRows(list);
            pagerResult.setTotal(count);
        } catch (Exception e) {
            log.error("查询失败", e);
        }
        return pagerResult;
    }

    /**
     * 导出申请
     *
     * @param loginUser
     * @param condition
     * @return
     */
    @Override
    public String exportApply(LoginUser loginUser, KaCodeCheckCondition condition) {
        String exportCode = loginUser.getUserErp() + DateHelper.formatDate(new Date(), "yyyyMMddHHmm")+".zip";
        String repeatExport = jimdbCacheService.get(exportCode);
        if(!StringUtils.isBlank(repeatExport)) {
            return "一分钟内只能导出一次，请稍后再试";
        }
        jimdbCacheService.setEx(exportCode, DateUtils.format(new Date(), "yyyyMMddHHmm"),1,TimeUnit.MINUTES);
        ExportLog exportLog = new ExportLog();
        exportLog.setExportCode(exportCode);
        exportLog.setCreateUser(loginUser.getUserErp());
        exportLog.setQueryWhere(JSON.toJSONString(condition));
        exportLog.setType(1);
        exportLogDao.add(exportLog);
        String body = exportLog.getQueryWhere();
        try {
            waybillCodeCheckMq.send(exportCode, body);
        } catch (JMQException e) {
            jimdbCacheService.del(exportCode);
            log.error("发送mq失败,exportCode:{}",exportCode, e);
            return "导出失败，发送mq异常";
        }
        return "";
    }

    /**
     * 获取导出数据
     *
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(KaCodeCheckCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        condition.setLimit(50000);
        Integer count = waybillCodeCheckDao.queryCountByCondition(condition);
        Integer pageCount = count / condition.getLimit() + (count % condition.getLimit() == 0 ? 0 : 1);
        long beginTime = System.currentTimeMillis();
        log.info("读取数据库开始，执行时间：{}", beginTime);
        for(Integer i = 0; i < pageCount; i++) {
            condition.setOffset(condition.getLimit() * i);
            getWaybillCodeCheckDTOList(condition, resList);
        }
        log.info("读取数据库结束，用时：{}", System.currentTimeMillis() - beginTime);
        return resList;
    }

    @Override
    @JProfiler(jKey = "dmsWeb.waybillCodeCheckService.getWaybillCodeCheckDTOList",jAppName=Constants.UMP_APP_NAME_DMSWORKER,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public void getWaybillCodeCheckDTOList(KaCodeCheckCondition condition, List<List<Object>> resList) {
        List<WaybillCodeCheckDto> dataList = waybillCodeCheckDao.exportByCondition(condition);
        if(dataList != null && dataList.size() > 0) {
            //表格信息
            for(WaybillCodeCheckDto detail : dataList) {
                List<Object> body = Lists.newArrayList();
                body.add(detail.getWaybillCode());
                body.add(detail.getCompareCode());
                body.add(detail.getBusiCode());
                body.add(detail.getBusiName());
                body.add(detail.getOperateSiteCode());
                body.add(detail.getOperateSiteName());
                body.add(detail.getCheckResult() == null ? FAIL_RESULT : (detail.getCheckResult() == SUCCESS_RESULT_NUM ? SUCCESS_RESULT : FAIL_RESULT));
                body.add(detail.getOperateErp());
                body.add(detail.getOperateTime() == null ? null : DateHelper.formatDate(detail.getOperateTime(), Constants.DATE_TIME_FORMAT));
                resList.add(body);
            }
        }
    }

    /**
     * 单号校验
     *
     * @param condition
     * @return
     */
    @Override
    public InvokeResult waybillCodeCheck(WaybillCodeCheckCondition condition) {
        InvokeResult result = new InvokeResult();
        if(StringUtils.isEmpty(condition.getBarCodeOfOne()) || StringUtils.isEmpty(condition.getBarCodeOfTwo())) {
            result.setCode(InvokeResult.RESULT_NULL_CODE);
            result.setMessage("条码为空，请重新输入!");
            return result;
        }
        if((!WaybillUtil.isWaybillCode(condition.getBarCodeOfOne()) && !WaybillUtil.isPackageCode(condition.getBarCodeOfOne())) ||
                (!WaybillUtil.isWaybillCode(condition.getBarCodeOfTwo()) && !WaybillUtil.isPackageCode(condition.getBarCodeOfTwo()))) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("输入的条码不符合规则!");
            return result;
        }
        String waybillCodeOfOne = WaybillUtil.getWaybillCode(condition.getBarCodeOfOne());
        String waybillCodeOfTwo = WaybillUtil.getWaybillCode(condition.getBarCodeOfTwo());
        //单号校验结果
        Integer checkResult = null;
        if(!waybillCodeOfOne.equals(waybillCodeOfTwo)) {
            checkResult = FAIL_RESULT_NUM;
            result.setCode(600);
            result.setMessage("匹配失败，号码不一致！请检查面单是否贴错");
        } else {
            checkResult = SUCCESS_RESULT_NUM;
            result.setMessage("匹配成功，号码一致!");
        }
        //单号校验记录落库
        waybillCodeCheckDao.add(trans2WaybillCodeCheckDto(condition, checkResult));
        return result;
    }


    /**
     * 组装入库对象
     *
     * @param condition
     * @param checkResult
     * @return
     */
    private WaybillCodeCheckDto trans2WaybillCodeCheckDto(WaybillCodeCheckCondition condition, Integer checkResult) {
        WaybillCodeCheckDto dto = new WaybillCodeCheckDto();
        String waybillCode = condition.getBarCodeOfOne();
        try {
            Integer busiId = waybillQueryManager.getBusiId(waybillCode);
            BasicTraderInfoDTO baseTraderInfoDTO = baseMinorManager.getBaseTraderById(busiId);
            if(baseTraderInfoDTO != null) {
                dto.setBusiCode(baseTraderInfoDTO.getTraderCode());
                dto.setBusiName(baseTraderInfoDTO.getTraderName());
            }
        } catch (Exception e) {
            log.error(InvokeResult.SERVER_ERROR_MESSAGE, e);
        }
        dto.setWaybillCode(waybillCode);
        dto.setCompareCode(condition.getBarCodeOfTwo());
        dto.setCheckResult(checkResult);
        dto.setOperateSiteCode(condition.getOperateSiteCode());
        dto.setOperateSiteName(condition.getOperateSiteName());
        dto.setOperateErp(condition.getOperateErp());
        Date operateTime = new Date();
        dto.setCreateTime(operateTime);
        dto.setOperateTime(operateTime);
        dto.setUpdateTime(operateTime);
        return dto;
    }
    /**
     * 查询数量
     * @param condition
     * @return
     */
    @Override
    public Integer queryCountByCondition(KaCodeCheckCondition condition) {
        Integer count = waybillCodeCheckDao.queryCountByCondition(condition);
        return count;
    }
}

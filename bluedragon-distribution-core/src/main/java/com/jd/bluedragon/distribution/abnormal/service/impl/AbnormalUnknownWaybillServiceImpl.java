package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.abnormal.dao.AbnormalUnknownWaybillDao;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wuyoude
 * @ClassName: AbnormalUnknownWaybillServiceImpl
 * @Description: 三无订单申请--Service接口实现
 * @date 2018年05月08日 15:16:15
 */
@Service("abnormalUnknownWaybillService")
public class AbnormalUnknownWaybillServiceImpl extends BaseService<AbnormalUnknownWaybill> implements AbnormalUnknownWaybillService {

    @Autowired
    @Qualifier("abnormalUnknownWaybillDao")
    private AbnormalUnknownWaybillDao abnormalUnknownWaybillDao;

    @Override
    public Dao<AbnormalUnknownWaybill> getDao() {
        return this.abnormalUnknownWaybillDao;
    }

    @Autowired
    private WaybillService waybillService;
    @Autowired
    private SysConfigService sysConfigService;


    /**
     * 查询并上报
     *
     * @return
     */
    public JdResponse<Boolean> queryAndReport(AbnormalUnknownWaybill abnormalUnknownWaybill) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        if (abnormalUnknownWaybill.getWaybillCode() == null || StringUtils.isBlank(abnormalUnknownWaybill.getWaybillCode())) {
            rest.toFail("运单号不能为空");
            return rest;
        }
        //传入的运单号处理
        String[] waybillcodes = StringUtils.trim(abnormalUnknownWaybill.getWaybillCode()).split("\\n");
        List<String> waybillList = Lists.newArrayList();
        StringBuilder errorMessage = new StringBuilder();//不合法的运单号
        for (String waybillCodeInput : waybillcodes) {
            String waybillCode = waybillCodeInput.trim();
            if (BusinessHelper.isWaybillCode(waybillCode)) {
                waybillList.add(waybillCode);
            } else {
                errorMessage.append(waybillCode).append(",");
            }
        }
        if (errorMessage.length() > 0) {
            rest.toFail("以下运单号不合法" + errorMessage + "请检查！");
            return rest;
        }
        if (waybillList.size() == 0) {
            rest.toFail("无可用运单号");
            return rest;
        }
        List<String> hasDetails = Lists.newArrayList();//查到明细的运单号
        List<String> noDetails = Lists.newArrayList();//运单和eclp没查到明细的运单号
        List<String> noWaybills = Lists.newArrayList();//不存在的运单号

        for (String waybillCode : waybillList) {
            List<AbnormalUnknownWaybill> abnormalUnknownWaybills = abnormalUnknownWaybillDao.queryByWaybillCode(waybillCode);
            AbnormalUnknownWaybill abnormalUnknownWaybill1WE = null;//系统回复
            AbnormalUnknownWaybill abnormalUnknownWaybill1BLast = null;//商家回复
            if (abnormalUnknownWaybills != null && abnormalUnknownWaybills.size() > 0) {
                for (AbnormalUnknownWaybill unknownWaybill : abnormalUnknownWaybills) {
                    if (AbnormalUnknownWaybill.RECEIPT_FROM_B.equals(unknownWaybill.getReceiptFrom())) {
                        //商家回复 只记录最后一次
                        if (abnormalUnknownWaybill1BLast == null) {
                            abnormalUnknownWaybill1BLast = unknownWaybill;
                        } else {
                            if (abnormalUnknownWaybill1BLast.getOrderNumber() < unknownWaybill.getOrderNumber()) {
                                //取最后一次上报
                                abnormalUnknownWaybill1BLast = unknownWaybill;
                            }
                        }
                    } else {
                        //系统回复
                        abnormalUnknownWaybill1WE = unknownWaybill;
                    }
                }
            }
            if (abnormalUnknownWaybill1WE != null) {
                hasDetails.add(abnormalUnknownWaybill1WE.getWaybillCode());//之前查过，并且运单或eclp就已经查到了
            }
            if (abnormalUnknownWaybill1BLast != null&&abnormalUnknownWaybill1BLast.getIsReceipt()) {

            }

            BigWaybillDto waybillDto = this.waybillService.getWaybill(waybillCode);
            if (waybillDto == null) {
                errorMessage.append(waybillCode).append(",");
                continue;
            }

        }


        return rest;
    }


    /**
     * 从sysconfig表里查出来 上报次数限制
     *
     * @return
     */
    @Cache(key = "AbnormalUnknownWaybillServiceImpl.getReportTimes", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, redisEnable = false)
    public int getReportTimes() {
        final int defaultTimes = 2;//默认是2次
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(Constants.SYS_ABNORMAL_UNKNOWN_REPORT_TIMES);
        if (sysConfigs != null && !sysConfigs.isEmpty()) {
            String contents = sysConfigs.get(0).getConfigContent();
            if (StringUtils.isEmpty(contents)) {
                return defaultTimes;
            }
            try {
                return Integer.parseInt(contents);
            } catch (Exception e) {
                logger.warn("系统配置abnormal.unknown.report.times内容不合法：" + contents);
                return defaultTimes;
            }
        }
        return defaultTimes;
    }
}

package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.abnormal.dao.AbnormalUnknownWaybillDao;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.common.web.LoginContext;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private BaseMajorManager baseMajorManager;


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
        List<String> noWaybills = Lists.newArrayList();//不存在的运单号

        //查出哪些已经有明细了或发过请求了 下面就不查了
        List<String> hasDetailWaybillCodes = abnormalUnknownWaybillDao.queryHasDetailWaybillCode(waybillList);
        //过滤出没查过的
        Set<String> noDetails = new HashSet<String>(waybillList);
        if (hasDetailWaybillCodes != null && hasDetailWaybillCodes.size() > 0) {
            for (String hasDetailWaybillCode : hasDetailWaybillCodes) {
                noDetails.remove(hasDetailWaybillCode);
            }

        }
        if (noDetails.size() > 0) {
            LoginContext loginContext = LoginContext.getLoginContext();
            BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
            for (String waybillCode : noDetails) {
                //第一步 查运单
                BigWaybillDto waybillDto = this.waybillService.getWaybill(waybillCode);
                if (waybillDto == null) {
                    noWaybills.add(waybillCode);//运单号不存在
                    continue;
                }
                AbnormalUnknownWaybill abnormalUnknownWaybill1Report = null;
                try {
                    abnormalUnknownWaybill1Report = buildAbnormalUnknownWaybill(userDto, waybillCode);
                } catch (Exception e) {
                    rest.toFail("站点所在省份获取失败：" + e.getMessage());
                    logger.warn("站点所在省份获取失败：" + e.getMessage());
                    return rest;
                }
                StringBuilder waybillDetail = new StringBuilder();
                List<Goods> goods = waybillDto.getGoodsList();
                if (goods != null && goods.size() > 0) {
                    for (int i = 0; i < goods.size(); i++) {
                        waybillDetail.append(goods.get(i).getGoodName() + "*" + goods.get(i).getGoodCount());
                        if (i != goods.size() - 1) {
                            waybillDetail.append("\\n");
                        }
                    }
                    abnormalUnknownWaybill1Report.setReceiptFrom(AbnormalUnknownWaybill.RECEIPT_FROM_WAYBILL);

                } else {

                }
                Waybill waybill = waybillDto.getWaybill();
                String busiOrderCode = waybillDto.getWaybill().getBusiOrderCode();


                if (BusinessHelper.isECLPByBusiOrderCode(busiOrderCode)) {

                }
            }
        }


        return rest;
    }

    public AbnormalUnknownWaybill buildAbnormalUnknownWaybill(BaseStaffSiteOrgDto userDto, String waybillCode) throws Exception {
        AbnormalUnknownWaybill abnormalUnknownWaybillReport = new AbnormalUnknownWaybill();
        abnormalUnknownWaybillReport.setWaybillCode(waybillCode);
        abnormalUnknownWaybillReport.setCreateUser(userDto.getAccountNumber());
        abnormalUnknownWaybillReport.setCreateUserCode(userDto.getStaffNo());
        abnormalUnknownWaybillReport.setCreateUserName(userDto.getStaffName());
        abnormalUnknownWaybillReport.setDmsSiteCode(userDto.getSiteCode());
        abnormalUnknownWaybillReport.setDmsSiteName(userDto.getSiteName());
        abnormalUnknownWaybillReport.setCreateTime(new Date());
        //站点区域查出来
        BaseStaffSiteOrgDto org = baseMajorManager.getBaseSiteBySiteId(userDto.getSiteCode());
        if (org == null) {
            throw new Exception("所在站点未找到：" + userDto.getSiteName());
        }
        ProvinceNode province = AreaHelper.getProvince(Integer.parseInt(Long.valueOf(org.getProvinceId()).toString()));
        if (province == null) {
            throw new Exception("站点所在省份获取失败：" + org.getProvinceId());
        }
        AreaNode areaNode = AreaHelper.getAreaByProvinceId(province.getId());
        if (areaNode == null) {
            throw new Exception("站点所在区域获取失败：" + province.getId());
        }
        abnormalUnknownWaybillReport.setAreaId(areaNode.getId());
        abnormalUnknownWaybillReport.setAreaName(areaNode.getName());
        abnormalUnknownWaybillReport.setIsDelete(0);
        return abnormalUnknownWaybillReport;
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

package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.dao.DmsAbnormalEclpDao;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpCondition;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpRequest;
import com.jd.bluedragon.distribution.abnormal.service.DmsAbnormalEclpService;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.domain.ProvinceNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.web.LoginContext;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wuyoude
 * @ClassName: DmsAbnormalEclpServiceImpl
 * @Description: ECLP外呼申请表--Service接口实现
 * @date 2018年03月14日 16:31:20
 */
@Service("dmsAbnormalEclpService")
public class DmsAbnormalEclpServiceImpl extends BaseService<DmsAbnormalEclp> implements DmsAbnormalEclpService {

    private final Logger log = LoggerFactory.getLogger(DmsAbnormalEclpServiceImpl.class);

    @Autowired
    @Qualifier("dmsAbnormalEclpDao")
    private DmsAbnormalEclpDao dmsAbnormalEclpDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public Dao<DmsAbnormalEclp> getDao() {
        return this.dmsAbnormalEclpDao;
    }

    @Autowired
    @Qualifier("abnormalEclpSendProducer")
    private DefaultJMQProducer abnormalEclpSendProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    BaseMinorManager baseMinorManager;

    @Override
    public JdResponse<Boolean> save(DmsAbnormalEclp dmsAbnormalEclp) {
        JdResponse<Boolean> rest = new JdResponse<Boolean>();
        //有未回复过的申请，也不让再次申请
        DmsAbnormalEclpCondition condition = new DmsAbnormalEclpCondition();
        condition.setWaybillCode(dmsAbnormalEclp.getWaybillCode());
        condition.setIsReceipt(DmsAbnormalEclp.DMSABNORMALECLP_RECEIPT_NO);
        PagerResult result = queryByPagerCondition(condition);
        //判断当前运单是否有未进行完毕的外呼
        if (result.getTotal() > 0) {
            rest.toFail("运单已发起过库房拒收的外呼申请：" + dmsAbnormalEclp.getWaybillCode());
            return rest;
        }
        //获取操作人信息
        LoginContext loginContext = LoginContext.getLoginContext();
        BaseStaffSiteOrgDto userDto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
        dmsAbnormalEclp.setCreateUser(userDto.getAccountNumber());
        dmsAbnormalEclp.setCreateUserCode(userDto.getStaffNo());
        dmsAbnormalEclp.setCreateUserName(userDto.getStaffName());
        dmsAbnormalEclp.setDmsSiteCode(userDto.getSiteCode());
        dmsAbnormalEclp.setDmsSiteName(userDto.getSiteName());
        dmsAbnormalEclp.setCreateTime(new Date());
        //查询运单
        BaseEntity<Waybill> waybillRes = waybillQueryManager.getWaybillByWaybillCode(dmsAbnormalEclp.getWaybillCode());
        if (waybillRes == null || waybillRes.getResultCode() != 1 || waybillRes.getData() == null) {
            rest.toFail("运单不存在。");
            log.warn("运单不存在：{}" , JsonHelper.toJson(dmsAbnormalEclp));
            return rest;
        }
        Waybill waybill1 = waybillRes.getData();
        if (waybill1.getBusiId() == null) {
            rest.toFail("商家信息没找到");
            log.warn("商家信息没找到:{}" , JsonHelper.toJson(dmsAbnormalEclp));
            return rest;
        }
        //获取运单中的商家信息
        BasicTraderInfoDTO trader = this.baseMinorManager.getBaseTraderById(waybill1.getBusiId());
        if (trader == null) {
            rest.toFail("运单未找到商家");
            log.warn("运单 {} 未找到商家 {}：{}",waybill1.getWaybillCode(),waybill1.getBusiId(),JsonHelper.toJson(dmsAbnormalEclp));
            return rest;
        }
        //判断商家的联系方式
        if (StringHelper.isEmpty(trader.getTelephone()) && StringHelper.isEmpty(trader.getContactMobile())) {
            rest.toFail("未查到商家联系方式");
            log.warn("未查到商家联系方式：{}" , JsonHelper.toJson(trader));
            return rest;
        }
        //转换成mp的格式
        DmsAbnormalEclpRequest dmsAbnormalEclpRequest = convertDmsAbnormalEclpRequest(dmsAbnormalEclp, userDto, trader);
        //站点区域查出来
        BaseStaffSiteOrgDto org = baseMajorManager.getBaseSiteBySiteId(userDto.getSiteCode());
        if (org == null) {
            rest.toFail("所在站点未找到：" + userDto.getSiteName());
            log.warn("所在站点未找到：{}" , userDto.getSiteName());
            return rest;
        }
        try {
            ProvinceNode province = AreaHelper.getProvince(Integer.parseInt(Long.valueOf(org.getProvinceId()).toString()));
            if (province == null) {
                rest.toFail("站点所在省份获取失败：" + org.getProvinceId());
                log.warn("站点所在省份获取失败：{}" , org.getProvinceId());
                return rest;
            }
            AreaNode areaNode = AreaHelper.getAreaByProvinceId(province.getId());
            if (areaNode == null) {
                rest.toFail("站点所在区域获取失败：" + province.getId());
                log.warn("站点所在区域获取失败：{}" , province.getId());
                return rest;
            }
            dmsAbnormalEclpRequest.setOrgNo(areaNode.getId().toString());
        } catch (Exception e) {
            rest.toFail("站点所在区域获取失败：" + org.getAreaId());
            log.warn("站点所在区域获取失败：{}" , org.getAreaId(), e);
            return rest;
        }
        if (!saveOrUpdate(dmsAbnormalEclp)) {
            rest.toFail("保存外呼申请失败，请重试。");
            log.warn("保存外呼申请失败：{}" , JsonHelper.toJson(dmsAbnormalEclp));
            return rest;
        }
        //发mq 给异常系统
        abnormalEclpSendProducer.sendOnFailPersistent(dmsAbnormalEclp.getWaybillCode(), JsonHelper.toJson(dmsAbnormalEclpRequest));
        if(log.isDebugEnabled()){
            log.debug("库房拒收申请：{}" , JsonHelper.toJson(dmsAbnormalEclpRequest));
        }
        rest.toSucceed();
        return rest;
    }

    private DmsAbnormalEclpRequest convertDmsAbnormalEclpRequest(DmsAbnormalEclp dmsAbnormalEclp, BaseStaffSiteOrgDto userDto, BasicTraderInfoDTO trader) {
        DmsAbnormalEclpRequest dmsAbnormalEclpRequest = new DmsAbnormalEclpRequest();
        dmsAbnormalEclpRequest.setWaybillCode(dmsAbnormalEclp.getWaybillCode());
        dmsAbnormalEclpRequest.setDeptCode(userDto.getSiteCode().toString());
        dmsAbnormalEclpRequest.setDeptName(userDto.getSiteName());
        dmsAbnormalEclpRequest.setExptCreateTime(DateHelper.formatDate(dmsAbnormalEclp.getCreateTime(), Constants.DATE_TIME_FORMAT));
        dmsAbnormalEclpRequest.setExptTwoLevel(dmsAbnormalEclpRequest.DMSABNORMALECLP_EXPTTWOLEVEL_CODE);
        dmsAbnormalEclpRequest.setExptTwoLevelName(dmsAbnormalEclpRequest.DMSABNORMALECLP_EXPTTWOLEVEL_NAME);
        dmsAbnormalEclpRequest.setExptThreeLevel(convertExptThreeLevel(dmsAbnormalEclp.getConsultType()));
        dmsAbnormalEclpRequest.setExptThreeLevelName(dmsAbnormalEclp.getConsultReason());
        dmsAbnormalEclpRequest.setExpName(dmsAbnormalEclp.getConsultReason());
        dmsAbnormalEclpRequest.setCreateUserCode(dmsAbnormalEclp.getCreateUser());
        dmsAbnormalEclpRequest.setCreateUserName(dmsAbnormalEclp.getCreateUserName());
        dmsAbnormalEclpRequest.setBusiId(trader.getId().toString());
        dmsAbnormalEclpRequest.setBusiName(trader.getTraderName());
        dmsAbnormalEclpRequest.setTelephone(StringHelper.isEmpty(trader.getTelephone()) ? trader.getContactMobile() : trader.getTelephone());
        dmsAbnormalEclpRequest.setConsultMark(dmsAbnormalEclp.getConsultMark());
        return dmsAbnormalEclpRequest;
    }

    @Override
    public int updateResult(DmsAbnormalEclp dmsAbnormalEclp) {
        int i = dmsAbnormalEclpDao.updateResult(dmsAbnormalEclp);
        if (i > 0 && log.isDebugEnabled()) {
            log.debug("外呼结果写入完成：{}" , JsonHelper.toJson(dmsAbnormalEclp));
        }
        return i;
    }

    /**
     * 异常类型 转换为MQ编码
     *
     * @param consultType
     * @return
     */
    private String convertExptThreeLevel(Integer consultType) {
        if (consultType == null) {
            return DmsAbnormalEclpRequest.DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_OTHER_CODE;
        }
        switch (consultType) {
            case 1:
                return DmsAbnormalEclpRequest.DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_ABNORMAL_CODE;
            case 2:
                return DmsAbnormalEclpRequest.DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_DAMAGED_CODE;
            case 3:
                return DmsAbnormalEclpRequest.DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_NONE_CODE;
            case 4:
                return DmsAbnormalEclpRequest.DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_OTHER_CODE;
            default:
                return DmsAbnormalEclpRequest.DMSABNORMALECLP_EXPTTHREELEVEL_PACKAGE_OTHER_CODE;
        }

    }
}

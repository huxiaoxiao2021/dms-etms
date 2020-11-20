package com.jd.bluedragon.distribution.coldchain.service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.TmsTfcWSManager;
import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.constans.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.coldchain.dao.ColdChainSendDao;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.domain.TransPlanDetailResult;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.tfc.dto.ScheduleCargoSimpleDto;
import com.jd.tms.tfc.dto.TransPlanScheduleCargoDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainSendServiceImpl
 * @date 2019/3/28
 */
@Service
public class ColdChainSendServiceImpl implements ColdChainSendService {

    private final static Logger log = LoggerFactory.getLogger(ColdChainSendServiceImpl.class);

    @Autowired
    private ColdChainSendDao coldChainSendDao;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private TmsTfcWSManager tmsTfcWSManager;

    @Autowired
    private SendCodeService sendCodeService;

    @Override
    public boolean add(ColdChainSend coldChainSend) {
        if (coldChainSend != null) {
            if (coldChainSendDao.add(coldChainSend) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer batchAdd(List<ColdChainSend> coldChainSends) {
        if (coldChainSends != null && coldChainSends.size() > 0) {
            return coldChainSendDao.batchAdd(coldChainSends);
        }
        return 0;
    }

    @Override
    public Integer batchAdd(List<SendM> sendMList, String transPlanCode) {
        if (sendMList != null && !sendMList.isEmpty() && StringUtils.isNotEmpty(transPlanCode)) {
            List<ColdChainSend> coldChainSends = new ArrayList<>();
            Set<String> waybillCodes = new HashSet<>();
            for (SendM sendM : sendMList) {
                String boxCode = sendM.getBoxCode();
                if (WaybillUtil.isWaybillCode(boxCode)) {
                    this.buildColdChainSend(boxCode, transPlanCode, sendM, waybillCodes, coldChainSends);
                } else if (WaybillUtil.isPackageCode(boxCode)) {
                    this.buildColdChainSend(WaybillUtil.getWaybillCode(boxCode), transPlanCode, sendM, waybillCodes, coldChainSends);
                } else if (BusinessUtil.isBoxcode(boxCode)) {
                    List<String> boxWaybillCodeSet = this.sortingService.getWaybillCodeListByBoxCode(boxCode);
                    if (boxWaybillCodeSet != null && boxWaybillCodeSet.size() > 0) {
                        for (String waybillCode : boxWaybillCodeSet) {
                            this.buildColdChainSend(waybillCode, transPlanCode, sendM, waybillCodes, coldChainSends);
                        }
                    }
                } else {
                    log.warn("[冷链发货]无法识别的扫描编号，boxCode:{}" , boxCode);
                }
            }
            return this.batchAdd(coldChainSends);
        }
        return 0;
    }

    /**
     * 构建冷链发货信息
     *
     * @param waybillCode
     * @param transPlanCode
     * @param sendM
     * @param waybillCodes
     * @param coldChainSends
     */
    private void buildColdChainSend(String waybillCode, String transPlanCode, SendM sendM, Set<String> waybillCodes, List<ColdChainSend> coldChainSends) {
        if (waybillCodes.add(waybillCode)) {
            ColdChainSend coldChainSend = new ColdChainSend();
            coldChainSend.setSendCode(sendM.getSendCode());
            coldChainSend.setWaybillCode(waybillCode);
            coldChainSend.setTransPlanCode(transPlanCode);
            coldChainSend.setCreateSiteCode(sendM.getCreateSiteCode());
            coldChainSend.setReceiveSiteCode(sendM.getReceiveSiteCode());
            coldChainSends.add(coldChainSend);
        }
    }

    @Override
    public ColdChainSend getByTransCode(String transPlanCode) {
        if (StringUtils.isNotEmpty(transPlanCode)) {
            ColdChainSend parameter = new ColdChainSend();
            parameter.setTransPlanCode(transPlanCode);
            List<ColdChainSend> coldChainSends = coldChainSendDao.get(parameter);
            if (coldChainSends.size() > 0) {
                return coldChainSends.get(0);
            }
        }
        return null;
    }

    @Override
    public ColdChainSend getBySendCode(String waybillCode, String sendCode) {
        if (StringUtils.isNotEmpty(waybillCode) && StringUtils.isNotEmpty(sendCode)) {
            ColdChainSend parameter = new ColdChainSend();
            parameter.setWaybillCode(waybillCode);
            parameter.setSendCode(sendCode);
            List<ColdChainSend> coldChainSends = coldChainSendDao.get(parameter);
            if (coldChainSends.size() > 0) {
                return coldChainSends.get(0);
            }
        }
        return null;
    }

    @Override
    public List<ColdChainSend> getByWaybillCode(String waybillCode) {
        if (StringUtils.isNotEmpty(waybillCode)) {
            ColdChainSend parameter = new ColdChainSend();
            parameter.setWaybillCode(waybillCode);
            return coldChainSendDao.get(parameter);
        }
        return null;
    }

    @Override
    public List<TransPlanDetailResult> getTransPlanDetail(Integer createSiteCode, Integer receiveSiteCode) {
        ScheduleCargoSimpleDto queryCondition = this.getQueryCondition(createSiteCode, receiveSiteCode);
        if (queryCondition != null) {
            List<TransPlanScheduleCargoDto> result = tmsTfcWSManager.getTransPlanScheduleCargoByCondition(queryCondition);
            if (result != null) {
                if (result.size() > 0) {
                    return this.getTransPlanDetailResultList(result);
                } else {
                    return Collections.EMPTY_LIST;
                }
            }
        }
        return null;
    }

    private ScheduleCargoSimpleDto getQueryCondition(Integer createSiteCode, Integer receiveSiteCode) {
        BaseStaffSiteOrgDto createSiteDto = baseMajorManager.getBaseSiteBySiteId(createSiteCode);
        BaseStaffSiteOrgDto receiveSiteDto = baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
        if (createSiteDto != null && receiveSiteDto != null) {
            // 取昨天的零点
            Date beginPlanDepartTime = this.getDateTimeByParam(-1, 0, 0, 0);
            // 取3日后的23点59分59秒
            Date endPlanDepartTime = this.getDateTimeByParam(3, 23, 59, 59);
            String beginNodeCode = createSiteDto.getDmsSiteCode();
            String endNodeCode = receiveSiteDto.getDmsSiteCode();
            ScheduleCargoSimpleDto condition = new ScheduleCargoSimpleDto();
            condition.setBeginNodeCode(beginNodeCode);
            condition.setEndNodeCode(endNodeCode);
            condition.setBeginPlanDepartTime(beginPlanDepartTime);
            condition.setEndPlanDepartTime(endPlanDepartTime);
            // 1-卡班调度; 2-非卡班调度
            condition.setScheduleType(1);
            // 20-待确认; 30-已确认
            condition.setCargoConfirmStatus(20);
            return condition;
        }
        return null;
    }

    /**
     * 根据TMS运输接口返回对象拼装为运输计划号对应运单列表的对象集合
     *
     * @param dtoList
     * @return
     */
    private List<TransPlanDetailResult> getTransPlanDetailResultList(List<TransPlanScheduleCargoDto> dtoList) {
        Map<String, TransPlanDetailResult> resultMap = new HashMap<>();
        for (TransPlanScheduleCargoDto dto : dtoList) {
            String transPlanCode = dto.getTransPlanCode();
            if (StringUtils.isNotEmpty(transPlanCode) && StringUtils.isNotEmpty(dto.getBusinessCode())) {
                if (resultMap.containsKey(transPlanCode)) {
                    if (!resultMap.get(transPlanCode).getWaybills().contains(dto.getBusinessCode())) {
                        resultMap.get(transPlanCode).getWaybills().add(dto.getBusinessCode());
                    }
                } else {
                    TransPlanDetailResult result = new TransPlanDetailResult();
                    result.setTransPlanCode(transPlanCode);
                    List waybills = new ArrayList<>();
                    waybills.add(dto.getBusinessCode());
                    result.setWaybills(waybills);
                    resultMap.put(transPlanCode, result);
                }
            }
        }
        return new ArrayList(resultMap.values());
    }

    /**
     * 根据当日日期的偏移量和指定的时、分、秒获取日期
     *
     * @param dayOffset 当日日期的偏移量
     * @param hour      时
     * @param minute    分
     * @param second    秒
     * @return
     */
    private Date getDateTimeByParam(int dayOffset, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, dayOffset);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    public String getOrGenerateSendCode(String transPlanCode, Integer createSiteCode, Integer receiveSiteCode) {
        ColdChainSend coldChainSend = this.getByTransCode(transPlanCode);
        if (coldChainSend != null && StringUtils.isNotEmpty(coldChainSend.getSendCode())) {
            return coldChainSend.getSendCode();
        } else {
            Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> attributeKeyEnumObjectMap = new HashMap<>();
            attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, createSiteCode);
            attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, receiveSiteCode);
            return sendCodeService.createSendCode(attributeKeyEnumObjectMap, BusinessCodeFromSourceEnum.DMS_WORKER_SYS, StringUtils.EMPTY);
        }
    }

}

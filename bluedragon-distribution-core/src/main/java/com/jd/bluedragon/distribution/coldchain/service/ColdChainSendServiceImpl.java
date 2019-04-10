package com.jd.bluedragon.distribution.coldchain.service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.TmsTfcWSManager;
import com.jd.bluedragon.distribution.coldchain.dao.ColdChainSendDao;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.coldchain.domain.TransPlanDetailResult;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.tfc.dto.ScheduleCargoSimpleDto;
import com.jd.tms.tfc.dto.TransPlanScheduleCargoDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainSendServiceImpl
 * @date 2019/3/28
 */
@Service
public class ColdChainSendServiceImpl implements ColdChainSendService {

    private final static Log logger = LogFactory.getLog(ColdChainSendServiceImpl.class);

    @Autowired
    private ColdChainSendDao coldChainSendDao;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private TmsTfcWSManager tmsTfcWSManager;

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
                    Set<String> boxWaybillCodeSet = this.getWaybillCodeByBoxCode(boxCode, sendM.getCreateSiteCode());
                    if (boxWaybillCodeSet.size() > 0) {
                        for (String waybillCode : boxWaybillCodeSet) {
                            this.buildColdChainSend(waybillCode, transPlanCode, sendM, waybillCodes, coldChainSends);
                        }
                    }
                } else {
                    logger.warn("[冷链发货]无法识别的扫描编号，boxCode:" + boxCode);
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

    /**
     * 根据箱号获取运单号
     *
     * @param boxCode
     * @param createSiteCode
     * @return
     */
    private Set<String> getWaybillCodeByBoxCode(String boxCode, Integer createSiteCode) {
        Set<String> waybillCodes = new HashSet<>();
        Sorting parameter = new Sorting();
        parameter.setBoxCode(boxCode);
        parameter.setCreateSiteCode(createSiteCode);
        List<Sorting> sortingList = sortingService.findByBoxCode(parameter);
        if (sortingList.size() > 0) {
            for (Sorting sorting : sortingList) {
                waybillCodes.add(sorting.getWaybillCode());
            }
        }
        return waybillCodes;
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
            condition.setYn(1);
            return condition;
        }
        return null;
    }

    private List<TransPlanDetailResult> getTransPlanDetailResultList(List<TransPlanScheduleCargoDto> dtoList) {
        Map<String, Set<String>> resultMap = new HashMap<>();
        for (TransPlanScheduleCargoDto dto : dtoList) {
            String transPlanCode = dto.getTransPlanCode();
            if (StringUtils.isNotEmpty(transPlanCode) && StringUtils.isNotEmpty(dto.getBusinessCode())) {
                if (resultMap.containsKey(transPlanCode)) {
                    resultMap.get(transPlanCode).add(dto.getBusinessCode());
                } else {
                    Set<String> waybillSet = new HashSet<>();
                    waybillSet.add(dto.getBusinessCode());
                    resultMap.put(transPlanCode, waybillSet);
                }
            }
        }
        if (resultMap.size() > 0) {
            List<TransPlanDetailResult> resultList = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : resultMap.entrySet()) {
                TransPlanDetailResult result = new TransPlanDetailResult();
                result.setTransPlanCode(entry.getKey());
                result.setWaybills(new ArrayList<>(entry.getValue()));
                resultList.add(result);
            }
            return resultList;
        }
        return Collections.EMPTY_LIST;


    }

    /**
     * 根据当日日期的偏移量和指定的时、分、秒获取日期
     *
     * @param dayOffset 当日日期的偏移量
     * @param hour 时
     * @param minute 分
     * @param second 秒
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
            return SerialRuleUtil.generateSendCode(createSiteCode, receiveSiteCode, new Date());
        }
    }

}

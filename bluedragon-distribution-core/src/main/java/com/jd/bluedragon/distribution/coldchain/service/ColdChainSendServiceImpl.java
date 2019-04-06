package com.jd.bluedragon.distribution.coldchain.service;

import com.jd.bluedragon.distribution.coldchain.dao.ColdChainSendDao;
import com.jd.bluedragon.distribution.coldchain.domain.ColdChainSend;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

}

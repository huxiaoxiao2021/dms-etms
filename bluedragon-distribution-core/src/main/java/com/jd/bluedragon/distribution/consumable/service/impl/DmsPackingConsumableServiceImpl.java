package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.packingconsumable.domain.DmsPackingConsumableInfo;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/8/14.
 */
@Component
public class DmsPackingConsumableServiceImpl implements DmsPackingConsumableService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String CONFIRM_MESSAGE = "此运单需要进行包装，包装后请在电脑端确认";
    public  static final  Integer HINT_CODE = 201;
    public static final String  HINT_MESSAGE= "此运单需使用包装耗材，但不存在包装耗材任务";

    @Autowired
    private DmsConsumableRelationService dmsConsumableRelationService;

    @Autowired
    private PackingConsumableInfoService packingConsumableInfoService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsPackingConsumableServiceImpl.getPackingConsumableInfoByDmsId", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<DmsPackingConsumableInfo> getPackingConsumableInfoByDmsId(Integer dmsId) {

        JdResponse<DmsPackingConsumableInfo> jdResponse = new JdResponse<DmsPackingConsumableInfo>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        if (dmsId == null) {
            log.warn("获取耗材信息失败：分拣中心编号不能为null");
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("获取耗材信息失败：分拣中心编号不能为null");
            return jdResponse;
        }
        DmsPackingConsumableInfo dmsPackingConsumableInfo = new DmsPackingConsumableInfo();
        try {
            List<PackingConsumableBaseInfo> packingConsumableBaseInfoList = dmsConsumableRelationService.getPackingConsumableInfoByDmsId(dmsId);

            dmsPackingConsumableInfo.setDmsId(dmsId);
            dmsPackingConsumableInfo.setPackingConsumableBaseInfoList(packingConsumableBaseInfoList);
            //集合不为空则更新字段为支持包装耗材
            if (packingConsumableBaseInfoList != null && packingConsumableBaseInfoList.size() > 0) {
                dmsPackingConsumableInfo.setSupportStatus(Constants.DMS_SUPPORT_PACKING_STATUS);
            } else {
                dmsPackingConsumableInfo.setSupportStatus(Constants.DMS_NOT_SUPPORT_PACKING_STATUS);
            }
            jdResponse.setData(dmsPackingConsumableInfo);
        } catch (Exception e) {
            log.error("获取耗材信息失败", e);
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage("获取耗材信息失败");
        }

        return jdResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsPackingConsumableServiceImpl.getPackingConsumableInfoByDmsCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<DmsPackingConsumableInfo> getPackingConsumableInfoByDmsCode(String dmsCode) {

        JdResponse<DmsPackingConsumableInfo> jdResponse = new JdResponse<DmsPackingConsumableInfo>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        if (StringHelper.isEmpty(dmsCode)) {
            log.warn("获取耗材信息失败：分拣中心编号不能为空");
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("获取耗材信息失败：分拣中心编号不能为空");
            return jdResponse;
        }

        BaseStaffSiteOrgDto dto = null;
        try {
            dto = baseMajorManager.getBaseSiteByDmsCode(dmsCode);
        } catch (Exception e) {
            log.error("获取耗材信息失败：通过基础资料获取分拣中心信息为空！", e);
        }

        if (dto == null || dto.getSiteCode() == null) {
            log.warn("获取耗材信息失败：通过基础资料获取分拣中心信息为空！");
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("获取耗材信息失败：通过基础资料获取分拣中心信息为空！");
            return jdResponse;
        }

        return getPackingConsumableInfoByDmsId(dto.getSiteCode());
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsPackingConsumableServiceImpl.getPackingConsumableInfoByCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdResponse<PackingConsumableBaseInfo> getPackingConsumableInfoByCode(String consumableCode) {
        JdResponse<PackingConsumableBaseInfo> jdResponse = new JdResponse<PackingConsumableBaseInfo>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        if (consumableCode == null) {
            log.warn("获取耗材信息失败：编号不能为null");
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("获取耗材信息失败：编号不能为null");
            return jdResponse;
        }
        try {
            PackingConsumableBaseInfo packingConsumableBaseInfo = packingConsumableInfoService.getPackingConsumableInfoByCode(consumableCode);
            jdResponse.setData(packingConsumableBaseInfo);
        } catch (Exception e) {
            log.error("获取耗材信息失败", e);
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage("获取耗材信息失败");
        }

        return jdResponse;
    }

    @Override
    public JdResponse<Boolean> getConfirmStatusByWaybillCode(String waybillCode) {

        JdResponse<Boolean> jdResponse = new JdResponse<>(JdResponse.CODE_FAIL, JdResponse.MESSAGE_FAIL);
        CallerInfo info = Profiler.registerInfo("DMS.BASE.DmsPackingConsumableServiceImpl.getConfirmStatusByWaybillCode",
                Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            WaybillConsumableRecord record =  waybillConsumableRecordService.queryOneByWaybillCode(waybillCode);
            if (record != null && record.getConfirmStatus() == 0) {
                jdResponse.setData(Boolean.FALSE);
                jdResponse.setCode(JdResponse.CODE_SUCCESS);
                jdResponse.setMessage(HintService.getHint(HintCodeConstants.PACKING_CONSUMABLE_CONFIRM));
            }
        } catch (Exception e) {
            log.error("查询运单：{}是否存在待确认包装任务失败",waybillCode);
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }

        return jdResponse;
    }
}

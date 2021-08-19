package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.merchant.api.common.dto.BaseEntity;
import com.jd.merchant.api.pack.dto.LoadScanDto;
import com.jd.merchant.api.pack.dto.LoadScanReqDto;
import com.jd.merchant.api.pack.ws.LoadScanPackageDetailWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("loadScanPackageDetailServiceManager")
public class LoadScanPackageDetailServiceManagerImpl implements LoadScanPackageDetailServiceManager {

    @Resource
    private LoadScanPackageDetailWS loadScanPackageDetailWs;


    /**
     *   查询不包含运单号集合的库存信息
     * @param loadCar  库存查询条件
     * @param waybillCodeList   查询库存时需要去除的运单号
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.LoadScanPackageDetailServiceManagerImpl.getInspectNoSendWaybillInfo",mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdCResponse<List<LoadScanDto>> getInspectNoSendWaybillInfo(LoadCar loadCar, List<String> waybillCodeList) {
        JdCResponse<List<LoadScanDto>> res = new JdCResponse<>();
        LoadScanReqDto loadScanReqDto = new LoadScanReqDto();
        try {
            loadScanReqDto.setCreateSiteId(loadCar.getCreateSiteCode().intValue());
            loadScanReqDto.setNextSiteId(loadCar.getEndSiteCode().intValue());
            Date fromTime = DateHelper.newTimeRangeHoursAgo(new Date(), GoodsLoadScanConstants.WAIT_LOAD_RANGE_FROM_HOURS);
            loadScanReqDto.setFormTime(fromTime.getTime());
            loadScanReqDto.setToTime(System.currentTimeMillis());
            loadScanReqDto.setLoadWaybillCodeList(waybillCodeList);
            BaseEntity<List<LoadScanDto>> jsfRes = loadScanPackageDetailWs.getWaitLoadWaybillInfo(loadScanReqDto);
            if(jsfRes == null) {
                log.error("LoadScanPackageDetailServiceManagerImpl.getInspectNoSendWaybillInfo--error--装车任务查询待装运单信息失败，参数loadScanReqDto=【{}】", JsonHelper.toJson(loadScanReqDto));
                res.toError("查询库存运单信息失败");
                return res;
            }else if(jsfRes.getCode() != BaseEntity.CODE_SUCCESS) {
                log.error("LoadScanPackageDetailServiceManagerImpl.getInspectNoSendWaybillInfo--fail--装车任务查询待装运单信息失败，参数loadScanReqDto=【{}】", JsonHelper.toJson(loadScanReqDto));
                res.toFail(jsfRes.getMessage());
                return res;
            }
            res.setData(jsfRes.getData());
            res.toSucceed();
            return res;

        }catch (Exception e) {
            log.error("LoadScanPackageDetailServiceManagerImpl.getInspectNoSendWaybillInfo--调用分拣报表查询已验未发jsf异常--，参数=【{}】", JsonHelper.toJson(loadScanReqDto), e);
            res.toFail("JSF调用失败");
            return res;
        }
    }
}

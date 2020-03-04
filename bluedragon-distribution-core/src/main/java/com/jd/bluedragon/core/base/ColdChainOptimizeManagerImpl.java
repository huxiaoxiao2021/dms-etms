package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainQueryUnloadTaskRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ccmp.ctm.api.FullFlowOptimizeApi;
import com.jd.ccmp.ctm.dto.CommonDto;
import com.jd.ccmp.ctm.dto.QueryUnloadDto;
import com.jd.ccmp.ctm.dto.QueryUnloadRequest;
import com.jd.ccmp.ctm.dto.ReceiveUnloadRequest;
import com.jd.common.util.StringUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 冷链全流程优化
 * 冷链货物操作数据接口
 *
 * @auther lixin456
 * @date 2020-02-27
 */
@Service
public class ColdChainOptimizeManagerImpl implements ColdChainOptimizeManager {

    private final static Logger log = LoggerFactory.getLogger(ColdChainOptimizeManagerImpl.class);

    @Autowired
    @Qualifier("jsfFullFlowOptimizeApi")
    private FullFlowOptimizeApi fullFlowOptimizeApi;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.ColdChainOptimizeManager.receiveUnloadData", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean receiveUnloadData(ColdChainUnloadDto unloadDto) {
        ReceiveUnloadRequest request = this.getUnloadRequest(unloadDto);
        if (request != null) {
            CommonDto commonDto = fullFlowOptimizeApi.receiveUnloadData(request);
            if (commonDto.getCode() == CommonDto.CODE_SUCCESS) {
                return true;
            } else {
                log.error("[冷链操作-冷链卸货]调用JSF接口上传卸货数据返回结果失败,request:" + JsonHelper.toJson(unloadDto) + ",code:" + commonDto.getCode() + ",message:" + commonDto.getMessage());
            }
        }
        return false;
    }

    /**
     * 查询卸车任务
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.ColdChainOptimizeManager.queryUnloadTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<QueryUnloadDto> queryUnloadTask(ColdChainQueryUnloadTaskRequest request) {
        if (request != null) {
            QueryUnloadRequest unloadRequest = new QueryUnloadRequest();
            unloadRequest.setSiteId(request.getSiteId());
            unloadRequest.setQueryDays(request.getQueryDays());
            unloadRequest.setState(request.getState());
            unloadRequest.setVehicleNo(request.getVehicleNo());
            CommonDto<List<QueryUnloadDto>> commonDto = fullFlowOptimizeApi.queryUnloadTask(unloadRequest);
            if (commonDto.getCode() == CommonDto.CODE_SUCCESS) {
                return commonDto.getData();
            } else {
                log.error("[冷链操作-卸货任务查询]调用JSF接口查询卸货任务返回结果失败,request:" + JsonHelper.toJson(request) + ",code:" + commonDto.getCode() + ",message:" + commonDto.getMessage());
            }
        }
        return Collections.emptyList();
    }

    private ReceiveUnloadRequest getUnloadRequest(ColdChainUnloadDto unloadDto) {
        if (unloadDto != null) {
            ReceiveUnloadRequest unloadRequest = new ReceiveUnloadRequest();
            unloadRequest.setOrgId(unloadDto.getOrgId());
            unloadRequest.setOrgName(unloadDto.getOrgName());
            unloadRequest.setSiteId(unloadDto.getSiteId());
            unloadRequest.setSiteName(unloadDto.getSiteName());
            unloadRequest.setUnloadTime(unloadDto.getUnloadTime());
            unloadRequest.setOperateERP(unloadDto.getOperateERP());
            unloadRequest.setVehicleNo(unloadDto.getVehicleNo());
            unloadRequest.setVehicleModelName(unloadDto.getVehicleModelName());
            unloadRequest.setVehicleModelNo(unloadDto.getVehicleModelNo());
            unloadRequest.setTemp(unloadDto.getTemp());
            return unloadRequest;
        }
        return null;
    }

    /**
     * 卸货完成
     *
     * @param taskNo
     * @param operateErp
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.ColdChainOptimizeManager.unloadTaskComplete", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean unloadTaskComplete(String taskNo, String operateErp) {
        if (StringUtils.isNotBlank(taskNo) && StringUtils.isNotBlank(operateErp)) {
            CommonDto commonDto = fullFlowOptimizeApi.unloadTaskComplete(taskNo, operateErp);
            if (commonDto.getCode() == CommonDto.CODE_SUCCESS) {
                return true;
            } else {
                log.error("[冷链操作-卸货完成]调用JSF接口完成卸货返回结果失败,taskNo:" + taskNo + ",operateErp:" + operateErp + ",code:" + commonDto.getCode() + ",message:" + commonDto.getMessage());
            }
        }
        return false;
    }
}

package com.jd.bluedragon.distribution.businessIntercept.service.impl;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.OfflineLogRequest;
import com.jd.bluedragon.distribution.businessIntercept.service.IOfflineTaskCheckBusinessInterceptService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.fastjson.JSON;
import com.jd.ql.dms.common.constants.OperateNodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 离线任务处理，校验是否有拦截
 *
 * @author fanggang7
 * @time 2020-12-22 16:19:39 周二
 */
@Service("offlineTaskCheckBusinessInterceptService")
public class OfflineTaskCheckBusinessInterceptServiceImpl implements IOfflineTaskCheckBusinessInterceptService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SortingCheckService sortingCheckService;

    /**
     * 处理离线任务，校验是否有拦截
     *
     * @param offlineLogRequest 离线请求
     * @return 处理结果
     */
    @Override
    public Response<Boolean> handleOfflineTask(OfflineLogRequest offlineLogRequest) {
        log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineTask param: {}", JSON.toJSONString(offlineLogRequest));
        Response<Boolean> result = new Response<>();
        result.toSucceed();
        try {
            // 判断是否需要处理，目前只记录分拣、发货动作的数据
            if(!this.judgeNeedHandleByOperateType(offlineLogRequest)){
                return result;
            }
            // 转变为pda操作对象
            PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
            // 在线状态
            pdaOperateRequest.setOnlineStatus(2);
            pdaOperateRequest.setReceiveSiteCode(offlineLogRequest.getReceiveSiteCode());
            pdaOperateRequest.setCreateSiteCode(offlineLogRequest.getSiteCode());
            pdaOperateRequest.setCreateSiteName(offlineLogRequest.getSiteName());
            pdaOperateRequest.setBoxCode(offlineLogRequest.getBoxCode());
            pdaOperateRequest.setPackageCode(offlineLogRequest.getPackageCode());
            pdaOperateRequest.setBusinessType(offlineLogRequest.getBusinessType());
            pdaOperateRequest.setOperateUserCode(offlineLogRequest.getUserCode());
            pdaOperateRequest.setOperateUserName(offlineLogRequest.getUserName());
            pdaOperateRequest.setOperateTime(offlineLogRequest.getOperateTime());
            pdaOperateRequest.setOperateType(offlineLogRequest.getOperateType());
            // 操作节点区分
            int operateNode = this.getOperateNode(offlineLogRequest);

            pdaOperateRequest.setOperateNode(operateNode);
            pdaOperateRequest.setIsLoss(0);

            try {
                // 走一遍校验链，得到拦截结果
                sortingCheckService.sortingCheck(pdaOperateRequest);
            } catch (Exception e) {
                log.info("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineTask sortingCheck result throw exception {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("OfflineTaskCheckBusinessInterceptServiceImpl handleOfflineTask exception {}", e.getMessage(), e);
            result.toError("离线任务处理，校验是否有拦截处理异常");
        }
        return result;
    }

    /**
     * 判断是否为需要处理的操作节点
     * @param offlineLogRequest 离线请求参数
     * @return 判定结果
     * @author fanggang7
     * @time 2020-12-22 17:22:26 周二
     */
    private boolean judgeNeedHandleByOperateType(OfflineLogRequest offlineLogRequest){
        List<Integer> needHandleOperateTypeList = new ArrayList<Integer>(){{
            add(Task.TASK_TYPE_SORTING);
            add(Task.TASK_TYPE_SEND_DELIVERY);
        }};
        if(needHandleOperateTypeList.contains(offlineLogRequest.getTaskType())){
            return true;
        }
        return false;
    }

    /**
     * 获取校验链上下文的操作节点值
     * @param offlineLogRequest 离线请求参数
     * @return 判定结果
     * @author fanggang7
     * @time 2020-12-22 17:22:26 周二
     */
    private int getOperateNode(OfflineLogRequest offlineLogRequest){
        int operateNode = 0;
        if(Objects.equals(Task.TASK_TYPE_SORTING, offlineLogRequest.getTaskType())){
            operateNode = OperateNodeConstants.SORTING;
        }
        if(Objects.equals(Task.TASK_TYPE_SEND_DELIVERY, offlineLogRequest.getTaskType())){
            operateNode = OperateNodeConstants.SEND;
        }
        return operateNode;
    }
}

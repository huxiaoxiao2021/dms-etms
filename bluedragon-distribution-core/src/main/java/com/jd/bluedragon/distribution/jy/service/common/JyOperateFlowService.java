package com.jd.bluedragon.distribution.jy.service.common;

import java.util.List;

import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowDto;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;

/**
 * service接口
 *
 * @author wuyoude
 * @date 2023/4/19 8:48 PM
 */
public interface JyOperateFlowService {

    /**
     * 新增数据
     * 
     * @param data
     * 
     */
    int insert(JyOperateFlowDto data);
    /**
     * 发送mq服务
     * @param jyOperateFlow
     * @return
     */
    int sendMq(JyOperateFlowMqData jyOperateFlow);
    /**
     * 发送mq列表服务
     * @param jyOperateFlow
     * @return
     */
    int sendMqList(List<JyOperateFlowMqData> jyOperateFlow);
}

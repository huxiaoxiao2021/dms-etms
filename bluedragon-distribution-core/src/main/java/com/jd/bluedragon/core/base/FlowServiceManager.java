package com.jd.bluedragon.core.base;

import com.jd.lsb.flow.condition.ApproveQueryCondition;
import com.jd.lsb.flow.domain.Approve;
import com.jd.lsb.flow.domain.ApproveRequestOrder;
import com.jd.lsb.flow.exception.FlowException;

import java.util.List;
import java.util.Map;

/**
 * 流程处理包装接口
 *
 * @author hujiping
 * @date 2021/4/21 11:30 上午
 */
public interface FlowServiceManager {

    /**
     * 提交申请单
     * @see 'http://lcp.jdl.cn/#/docSoftwareSystem/19/670'
     * @param oaMap OA数据
     * @param businessMap 业务数据
     * @param flowControlMap 流程分支数据
     * @param name 流程编码
     * @param operateErp 提交人ERP
     * @param businessNoKey 申请单key
     * @return 申请单工单号
     * @throws FlowException 审批异常
     */
    public String startFlow(Map<String,Object> oaMap,Map<String,Object> businessMap,Map<String,Object> flowControlMap,
            String name, String operateErp, String businessNoKey) throws FlowException;

    /**
     * 申请单查询
     * @param processInstanceNo 申请单号
     * @return
     * @throws FlowException
     */
    public ApproveRequestOrder getRequestOrder(String processInstanceNo) throws FlowException;

    /**
     * 查询所有待审批任务
     * @param condition
     * @return
     * @throws FlowException
     */
    public List<Approve> getAllActiveApproveTaskList(ApproveQueryCondition condition) throws FlowException;

    /**
     * 取消申请单
     * @param processInstanceNo 申请单号
     * @param applicant 申请人ERP
     * @throws FlowException
     */
    public void cancelRequestOrder( String processInstanceNo, String applicant) throws FlowException;

}

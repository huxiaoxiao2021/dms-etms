package com.jd.bluedragon.core.base;

import java.util.List;

import com.jd.bluedragon.common.dto.abnormal.Dept;
import com.jd.bluedragon.common.dto.abnormal.DeptType;
import com.jd.bluedragon.common.dto.abnormal.TraceDept;
import com.jd.bluedragon.common.dto.abnormal.request.TraceDeptQueryRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
/**
 * 质控-部门服务管理
 * @author wuyoude
 *
 */
public interface DeptServiceQcManager {
	/**
	 * 获取部门类型
	 * @return
	 */
    JdCResponse<List<DeptType>> getDeptTypes();
    /**
     * 根据区域和类型获取部门列表
     * @param regionId
     * @param deptTypeCode
     * @return
     */
    JdCResponse<List<Dept>> getDept(Integer regionId,String deptTypeCode);
    /**
     * 调用质控接口查询推荐部门
     * @param queryRequest
     * @return
     */
	JdCResponse<List<TraceDept>> getTraceDept(TraceDeptQueryRequest queryRequest);
}

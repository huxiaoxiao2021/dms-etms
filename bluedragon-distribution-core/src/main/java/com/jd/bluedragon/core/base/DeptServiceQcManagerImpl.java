package com.jd.bluedragon.core.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.common.dto.abnormal.Dept;
import com.jd.bluedragon.common.dto.abnormal.DeptType;
import com.jd.bluedragon.common.dto.abnormal.TraceDept;
import com.jd.bluedragon.common.dto.abnormal.request.TraceDeptQueryRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.erp.util.BeanUtils;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.BaseResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.CodeTypeEnum;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.TraceDeptParam;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.service.DeptService;
/**
 * 质控-部门服务管理
 * @author wuyoude
 *
 */
@Component
public class DeptServiceQcManagerImpl implements DeptServiceQcManager{
    @Autowired
    private DeptService deptService;
    
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.qc.";
    private final Logger log = LoggerFactory.getLogger(DeptServiceQcManagerImpl.class);
	/**
	 * 获取部门类型
	 * @return
	 */
    @Cache(key = "DeptServiceQcManagerImpl.getDeptTypes", memoryEnable = false, memoryExpiredTime = 10 * 60 * 1000,
    		redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public JdCResponse<List<DeptType>> getDeptTypes(){
    	JdCResponse<List<DeptType>> result = new JdCResponse<List<DeptType>>();
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "deptService.getDeptTypes");
		try {
			BaseResult<Set<com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.DeptType>> rpcResult = deptService.getDeptTypes();
			if(rpcResult != null
					&& rpcResult.getData() != null){
				result.setData(convertDeptTypeList(rpcResult.getData()));
				result.toSucceed(rpcResult.getMessage());
			}else{
				log.warn("调用qc获取获取部门类型失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("调用qc获取获取部门类型失败！");
			}
		} catch (Exception e) {
			log.error("调用qc获取获取部门类型异常！",e);
			result.toError("调用qc获取获取部门类型异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
    }
    /**
     * 根据区域和类型获取部门列表
     * @param regionId
     * @param deptTypeCode
     * @return
     */
    @Cache(key = "DeptServiceQcManagerImpl.getDept@args0-@args1", memoryEnable = false, memoryExpiredTime = 10 * 60 * 1000,
    		redisEnable = true, redisExpiredTime = 20 * 60 * 1000)    
    public JdCResponse<List<Dept>> getDept(Integer regionId,String deptTypeCode){
    	JdCResponse<List<Dept>> result = new JdCResponse<List<Dept>>();
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "deptService.getDept");
		try {
			BaseResult<List<com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.Dept>> rpcResult = deptService.getDept(regionId, deptTypeCode);
			if(rpcResult != null
					&& rpcResult.getData() != null){
				result.setData(convertDeptList(rpcResult.getData()));
				result.toSucceed(rpcResult.getMessage());
			}else{
				log.warn("调用qc根据区域和类型获取部门列表失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("调用qc根据区域和类型获取部门列表失败！");
			}
		} catch (Exception e) {
			log.error("调用qc根据区域和类型获取部门列表异常！",e);
			result.toError("调用qc根据区域和类型获取部门列表异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
    }
	@Override
	public JdCResponse<List<TraceDept>> getTraceDept(TraceDeptQueryRequest queryRequest) {
    	JdCResponse<List<TraceDept>> result = new JdCResponse<List<TraceDept>>();
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "deptService.getTraceDept");
		try {
			TraceDeptParam traceDeptParam = new TraceDeptParam();
			if(WaybillUtil.isPackageCode(queryRequest.getCode())) {
				traceDeptParam.setCodeTypeEnum(CodeTypeEnum.PACKAGE);
			}else {
				traceDeptParam.setCodeTypeEnum(CodeTypeEnum.WAYBILL);
			}
			BeanUtils.copyProperties(queryRequest, traceDeptParam);
			if(log.isInfoEnabled()) {
				log.info("调用qc获取推荐部门列表,request:{}",JsonHelper.toJson(traceDeptParam));
			}
			BaseResult<List<com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.TraceDept>> rpcResult = deptService.getTraceDept(traceDeptParam);
			if(log.isInfoEnabled()) {
				log.info("调用qc获取推荐部门列表,result:{}",JsonHelper.toJson(rpcResult));
			}
			if(rpcResult != null
					&& rpcResult.getData() != null){
				result.setData(convertTraceDeptList(rpcResult.getData()));
				result.toSucceed(rpcResult.getMessage());
			}else{
				log.warn("调用qc获取推荐部门列表失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("调用qc获取推荐部门列表失败！");
			}
		} catch (Exception e) {
			log.error("调用qc获取推荐部门列表异常！",e);
			result.toError("调用qc获取推荐部门列表异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}     
    private List<TraceDept> convertTraceDeptList(List<com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.TraceDept> list) {
    	List<TraceDept> newList = new ArrayList<TraceDept>();
    	if(list != null) {
    		for(com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.TraceDept dept:list) {
    			TraceDept newData = new TraceDept();
    			newData.setCode(dept.getCode());
    			newData.setName(dept.getName());
    			newData.setFirstOperateTime(dept.getFirstOperateTime());
    			newData.setIsDealDept(dept.getIsDealDept());
    			newData.setIsPickDept(dept.getIsPickDept());
        		newList.add(newData);
    		}
    	}
    	//按是否推荐部门排序
    	Collections.sort(newList, new Comparator<TraceDept>() {
            @Override
            public int compare(TraceDept o1, TraceDept o2) {
                if(o1 != null
                		&& Boolean.TRUE.equals(o1.getIsDealDept())) {
                	return -1;
                }
                return 1;
            }
        });
    	return newList;
	}
	private List<Dept> convertDeptList(List<com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.Dept> deptList) {
    	List<Dept> newList = new ArrayList<Dept>();
    	if(deptList != null) {
    		for(com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.Dept dept:deptList) {
        		Dept newDept = new Dept();
        		newDept.setCode(dept.getCode());
        		newDept.setName(dept.getName());
        		newList.add(newDept);
    		}
    	}
    	return newList;
    }
    private List<DeptType> convertDeptTypeList(Set<com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.DeptType> deptTypeSet) {
    	List<DeptType> newList = new ArrayList<DeptType>();
    	if(deptTypeSet != null) {
    		for(com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.DeptType deptType:deptTypeSet) {
    			DeptType newDeptType = new DeptType();
    			newDeptType.setCode(deptType.getCode());
    			newDeptType.setName(deptType.getName());
        		newList.add(newDeptType);
    		}
    	}
    	return newList;
    }   
}

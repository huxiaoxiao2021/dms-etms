package com.jd.bluedragon.core.jsf.presort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.lbs.presort.aoi.api.constant.SearchType;
import com.jdl.lbs.presort.aoi.api.search.AoiBindSearchService;
import com.jdl.lbs.presort.aoi.api.search.query.AoiBindExactSearchQuery;
import com.jdl.lbs.presort.aoi.api.search.result.AoiBindRoadInfoDto;
import com.jdl.lbs.presort.framework.core.vo.BaseResponseVo;

/**
 * 
 * @ClassName: AoiServiceManagerImpl
 * @Description: Gis服务-aoi相关
 * @author: wuyoude
 * @date: 2023年2月21日 下午2:37:26
 *
 */
@Service("aoiServiceManager")
public class AoiServiceManagerImpl implements AoiServiceManager{
    private static final Logger log = LoggerFactory.getLogger(AoiServiceManagerImpl.class);
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.presort.aoiBindSearchService.";
    
    @Autowired
    @Qualifier("aoiBindSearchService")
    private AoiBindSearchService aoiBindSearchService;
    
    
    @Value("${beans.aoiServiceManager.isMock:false}")
    private boolean isMock;
    
    private Random random = new Random();
    
    private static Integer SUC_CODE = 2000;

	@Override
	public JdResult<List<AoiBindRoadMappingData>> aoiBindRoadMappingExactSearch(AoiBindRoadMappingQuery query) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "aoiBindRoadMappingExactSearch");
    	JdResult<List<AoiBindRoadMappingData>> result = new JdResult<List<AoiBindRoadMappingData>>();
    	if(query == null) {
    		result.toFail("入参为空！");
    		return result;
    	}
		try {
			AoiBindExactSearchQuery rpcQuery = new AoiBindExactSearchQuery();
			rpcQuery.setDataId(query.getAoiId());
			rpcQuery.setSiteCode(query.getSiteCode());
			rpcQuery.setSearchType(SearchType.NO_BLANK);
			if(log.isInfoEnabled()){
				log.info("Aoi-query-req："+JsonHelper.toJson(rpcQuery));
			}
			BaseResponseVo<List<AoiBindRoadInfoDto>>  rpcResult = this.aoiBindRoadMappingExactSearch(rpcQuery);
			if(log.isInfoEnabled()){
				log.info("Aoi-query-response："+JsonHelper.toJson(rpcResult));
			}
			if(rpcResult != null
					&& SUC_CODE.equals(rpcResult.getCode())
					&& !CollectionUtils.isEmpty(rpcResult.getData())){
				result.toSuccess();
				result.setData(toDataList(rpcResult.getData()));
			}else{
				log.warn("调用Gis查询Aoi失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("调用Gis查询Aoi失败！");
			}
		} catch (Exception e) {
			log.error("调用Gis查询Aoi异常！",e);
			result.toError("调用Gis查询Aoi异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
	/**
	 * Gis没有测试环境，需要开启mock
	 * @param aoiExactSearchQuery
	 * @return
	 */
	private BaseResponseVo<List<AoiBindRoadInfoDto>> aoiBindRoadMappingExactSearch(AoiBindExactSearchQuery aoiExactSearchQuery){
		if(!isMock) {
			return aoiBindSearchService.aoiBindRoadMappingExactSearch(aoiExactSearchQuery);
		}
		int returnType = random.nextInt(100);
		BaseResponseVo<List<AoiBindRoadInfoDto>> mockResult = new BaseResponseVo<List<AoiBindRoadInfoDto>>(SUC_CODE,"mock成功aoi结果");
		if(returnType>90) {
			//异常
			throw new RuntimeException("mock异常aoi结果");
		}else if(returnType>60){
			//失败
			mockResult.setCode(40003);
			mockResult.setMsg("mock失败aoi结果");
		}else {
			//成功
			mockResult.setData(new ArrayList<>());
			AoiBindRoadInfoDto aoiData = new AoiBindRoadInfoDto();
			mockResult.getData().add(aoiData);
			aoiData.setMergeCode(UUID.randomUUID().toString().toUpperCase().substring(0, 2));
		}
		return mockResult;
	}
	/**
	 * 数据转换
	 * @param rpcData
	 * @return
	 */
	private List<AoiBindRoadMappingData> toDataList(List<AoiBindRoadInfoDto> rpcData) {
		if(rpcData == null) {
			return null;
		}
		List<AoiBindRoadMappingData> dataList = new ArrayList<>();
		for(AoiBindRoadInfoDto item : rpcData) {
			AoiBindRoadMappingData data = new AoiBindRoadMappingData();
			data.setAoiId(item.getDataId());
			data.setSiteCode(item.getSiteCode());
			data.setRoadNo(item.getRoadNo());
			data.setAoiCode(item.getMergeCode());
			dataList.add(data);
		}
		return dataList;
	}
}

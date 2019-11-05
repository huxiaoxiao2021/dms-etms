package com.jd.bluedragon.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseInfoHelper {
	private static Logger log = LoggerFactory.getLogger(BaseInfoHelper.class);
	private static Map<Integer,BaseStaffSiteOrgDto>  siteInfoMap=new HashMap<Integer,BaseStaffSiteOrgDto>();

	public static BaseStaffSiteOrgDto getSiteInfoMap(Integer siteCode){
		return siteInfoMap.get(siteCode);
	}
	
	
	public synchronized static void setSiteInfoMap(List<BaseStaffSiteOrgDto> siteInfo_list) {
		log.info("接受站点信息size={}",siteInfo_list.size());
		//每次接受直接重置
		siteInfoMap.clear();
		for(BaseStaffSiteOrgDto baseDto:siteInfo_list){
			Integer key=baseDto.getSiteCode();
			if(siteInfoMap.containsKey(key)){
				log.info("[站点code={} 站点name={}] 更新[站点name={}]",
						baseDto.getSiteCode(),siteInfoMap.get(key).getSiteName(),baseDto.getSiteName());
			}
			siteInfoMap.put(baseDto.getSiteCode(), baseDto);
		}
		log.info("设置站点信息size={}",siteInfoMap.size());
	}
}

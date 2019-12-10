package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.reverse.domain.WmsSite;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class WmsSiteCacheManager {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Map<Integer, WmsSite> siteCaches = Collections
			.synchronizedMap(new HashMap<Integer, WmsSite>());
	public static final WmsSite EMPTY_SITE = new WmsSite();
	private static final Long CACHE_REFRESH_INTEVEVAL = 1 * 60 * 60 * 1000l;
	private static final Integer CACHE_NUM_THRESHOLD = 500;

	@Autowired
	private BaseMajorManager baseMajorManager;

	public WmsSite getWmsSite(Integer siteCode) {
		if (siteCode == null) {
			return EMPTY_SITE;
		}
		WmsSite site = siteCaches.get(siteCode);
		boolean needRefresh = false;
		
		if (site == null) {
			needRefresh = true;
		} else {
			if ((System.currentTimeMillis() - site.getLastModifiedTime()) > CACHE_REFRESH_INTEVEVAL) {
				needRefresh = true;
			}
		}
		if ((site == null) || needRefresh) {
			BaseStaffSiteOrgDto dto = queryDmsBaseSiteByCode(siteCode);
			site = toWmsSite(dto);
			if (site != null) {
				site.setLastModifiedTime(System.currentTimeMillis());
				siteCaches.put(siteCode, site);
				if (siteCaches.size() > CACHE_NUM_THRESHOLD) {
					log.warn("siteCaches缓存对象超过:{}", CACHE_NUM_THRESHOLD);
				}
			}
		}
		if (site == null) {
			return EMPTY_SITE;
		} else {
			return site;
		}
	}

	private BaseStaffSiteOrgDto queryDmsBaseSiteByCode(Integer code) {
		try {
			BaseStaffSiteOrgDto result = baseMajorManager
					.getBaseSiteBySiteId(code);
			return result;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getDmsBaseSiteByCode(code)异常", e);
			return null;
		}
	}

	private WmsSite toWmsSite(BaseStaffSiteOrgDto dto) {
		if (dto == null) {
			return null;
		}
		WmsSite site = new WmsSite();
		Integer orgId = dto.getOrgId();
		String storeCode = dto.getStoreCode();
		try {
			Integer cky2 = Integer.parseInt(storeCode.split("-")[1]);
			Integer storeId = Integer.parseInt(dto.getCustomCode());
			site.setOrgId(orgId);
			site.setCky2(cky2);
			site.setStoreId(storeId);
		} catch (Exception e) {
			log.error("调用toWmsSite", e);
			return null;
		}
		return site;
	}
	
	public boolean isValidWmsSite(WmsSite site){
		if (site.equals(EMPTY_SITE)) {
			return false;
		}

		if (site.getOrgId() == null || site.getCky2() == null
				|| site.getStoreId() == null) {
			return false;
		}
		return true;
	}

}

package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.dao.DmsBaseDictDao;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDictCondition;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.print.domain.SignConfig;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @ClassName: DmsBaseDictServiceImpl
 * @Description: 数据字典--Service接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:24:12
 *
 */
@Service("dmsBaseDictService")
public class DmsBaseDictServiceImpl extends BaseService<DmsBaseDict> implements DmsBaseDictService {
	@Autowired
	@Qualifier("dmsBaseDictDao")
	private DmsBaseDictDao dmsBaseDictDao;

	@Override
	public Dao<DmsBaseDict> getDao() {
		return this.dmsBaseDictDao;
	}


    /**
     * 根据查询条件获取数据字典数据
     * @param pagerCondition
     * @return
     */
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<DmsBaseDict> queryByCondition(PagerCondition pagerCondition) {
		return dmsBaseDictDao.queryByCondition(pagerCondition);
	}

    /**
     * 根据parentId和typeGroup查找分拣基础数据
     * @param parentId
     * @param typeGroup
     * @return
     */
    @Cache(key = "dmsBaseDictService.queryByParentIdAndTypeGroup@args0@args1", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<DmsBaseDict> queryByParentIdAndTypeGroup(Integer parentId, Integer typeGroup) {
        DmsBaseDictCondition dmsBaseDictCondition = new DmsBaseDictCondition();
        dmsBaseDictCondition.setLimit(Integer.MAX_VALUE);
        dmsBaseDictCondition.setParentId(parentId);
        dmsBaseDictCondition.setTypeGroup(typeGroup);
        return queryByCondition(dmsBaseDictCondition);
    }
    /**
     * 根据parentId和typeGroup查找分拣基础数据
     * @param parentId
     * @return
     */
    @Cache(key = "dmsBaseDictService.queryListByParentId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @Override
    public List<DmsBaseDict> queryListByParentId(Integer parentId) {
        DmsBaseDictCondition dmsBaseDictCondition = new DmsBaseDictCondition();
        dmsBaseDictCondition.setLimit(Integer.MAX_VALUE);
        dmsBaseDictCondition.setParentId(parentId);
        return queryByCondition(dmsBaseDictCondition);
    }

    @Cache(key = "dmsBaseDictService.queryLowerLevelListByTypeCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @Override
    public List<DmsBaseDict> queryLowerLevelListByTypeCode(Integer typeCode) {
        DmsBaseDict dmsBaseDict = this.queryRootByTypeCode(typeCode);
        if(dmsBaseDict == null){
            return null;
        }
        return this.queryListByParentId(dmsBaseDict.getId().intValue());
    }

    /**
     * 根据parentId 查询 解析成；<typeCode,typeName>数据结构
     * @param parentId
     * @return
     */
    @Cache(key = "dmsBaseDictService.queryMapKeyTypeCodeByTypeCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @Override
    public Map<Integer,String> queryMapKeyTypeCodeByTypeCode(Integer parentId) {
        List<DmsBaseDict> dmsBaseDictList = queryLowerLevelListByTypeCode(parentId);
        if(CollectionUtils.isEmpty(dmsBaseDictList)){
            return null;
        }
        Map<Integer,String> result = new HashMap<>();
        for(DmsBaseDict item : dmsBaseDictList){
            result.put(item.getTypeCode(),item.getTypeName());
        }
        return result;
    }

    /**
     * 根据parentId查找所有下级节点数据,返回list
     * @param typeCode
     * @return
     */
	@Override
	public DmsBaseDict queryRootByTypeCode(Integer typeCode) {
		DmsBaseDictCondition dmsBaseDictCondition = new DmsBaseDictCondition();
        dmsBaseDictCondition.setTypeCode(typeCode);
        dmsBaseDictCondition.setNodeLevel(DIC_ROOT_NODE_LEVEL);
        List<DmsBaseDict> nodes = queryByCondition(dmsBaseDictCondition);
        if(nodes != null && !nodes.isEmpty()){
        	return nodes.get(0);
        }
        return null;
	}
	/**
	 * 根据typeCode和parantId查询节点
	 */
	@Override
	public DmsBaseDict queryByTypeCodeAndParentId(Integer typeCode,
			Integer parentId) {
		DmsBaseDictCondition dmsBaseDictCondition = new DmsBaseDictCondition();
        dmsBaseDictCondition.setTypeCode(typeCode);
        dmsBaseDictCondition.setParentId(parentId);
        List<DmsBaseDict> nodes = queryByCondition(dmsBaseDictCondition);
        if(nodes != null && !nodes.isEmpty()){
        	return nodes.get(0);
        }
        return null;
	}
	/**
	 * 根据typeName和parantId查询节点
	 */
	@Override
	public DmsBaseDict queryByTypeNameAndParentId(String typeName,
			Integer parentId) {
		DmsBaseDictCondition dmsBaseDictCondition = new DmsBaseDictCondition();
        dmsBaseDictCondition.setTypeName(typeName);
        dmsBaseDictCondition.setParentId(parentId);
        List<DmsBaseDict> nodes = queryByCondition(dmsBaseDictCondition);
        if(nodes != null && !nodes.isEmpty()){
        	return nodes.get(0);
        }
        return null;
	}
    /**
     * 根据parentId查找所有下级节点数据,返回以字段TypeGroup为key的map
     * @param parentId
     * @return
     */
    @Cache(key = "dmsBaseDictService.queryMapByParentId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@Override
	public Map<Integer, List<DmsBaseDict>> queryMapByParentId(Integer parentId) {
    	List<DmsBaseDict> dataList = queryListByParentId(parentId);
		if(dataList!=null && !dataList.isEmpty()){
			Map<Integer, List<DmsBaseDict>> res = new TreeMap<Integer, List<DmsBaseDict>>();
			for(DmsBaseDict dmsBaseDict: dataList){
				Integer typeGroup = dmsBaseDict.getTypeGroup();
				if(typeGroup!=null){
					if(res.containsKey(typeGroup)){
						res.get(typeGroup).add(dmsBaseDict);
					}else{
						List<DmsBaseDict> tmpList = new ArrayList<DmsBaseDict>();
						tmpList.add(dmsBaseDict);
						res.put(typeGroup, tmpList);
					}
				}
			}
			return res;
		}
		return null;
	}
    /**
     * 根据打标配置标识名查询打标配置信息
     * <p>1、查询signConfigName对应的位置及定义配置
     * 2、查询位置及定义明细信息
     * 3、组装配置信息
     * @param signConfigName
     * @return 以打标位为key的map信息
     */
    @Cache(key = "dmsBaseDictService.getSignConfigsByConfigName@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
		   redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@Override
	public Map<Integer, SignConfig> getSignConfigsByConfigName(String signConfigName) {
		Map<Integer, SignConfig> res = new TreeMap<Integer, SignConfig>();
		//先查询根节点配置，101分组配置、102-打标配置
		DmsBaseDict positionRoot = this.queryRootByTypeCode(DIC_ROOT_TYPE_CODE_TYPE_GROUPS);
		DmsBaseDict signTextRoot = this.queryRootByTypeCode(DIC_ROOT_TYPE_CODE_SIGN_TEXTS);
		DmsBaseDict positionNode = this.queryByTypeNameAndParentId(signConfigName + DIC_SIGN_CONFIG_POSITION_SUFFIX,positionRoot.getId().intValue());
		DmsBaseDict signTextNode = this.queryByTypeNameAndParentId(signConfigName + DIC_SIGN_CONFIG_TEXTS_SUFFIX,signTextRoot.getId().intValue());
		if(positionNode != null && signTextNode != null){
			List<DmsBaseDict> positions = this.queryListByParentId(positionNode.getId().intValue());
			Map<Integer, List<DmsBaseDict>> signTexts = this.queryMapByParentId(signTextNode.getId().intValue());
			if(positions!=null && signTexts!=null && !signTexts.isEmpty()){
				for(DmsBaseDict position:positions){
					Integer typeGroup = position.getId().intValue();
					List<DmsBaseDict> signTextItems = signTexts.get(typeGroup);
					if(signTextItems!=null && !signTextItems.isEmpty()){
						SignConfig signConfig = new SignConfig();
						signConfig.setPosition(position.getTypeCode());
						signConfig.setFieldName(position.getTypeName());
						Map<String,String> signTextsMap = new HashMap<String,String>();
						for(DmsBaseDict signTextItem:signTextItems){
							signTextsMap.put(signTextItem.getTypeName(), signTextItem.getMemo());
						}
						signConfig.setSignTexts(signTextsMap);
						res.put(signConfig.getPosition(), signConfig);
					}
				}
			}
		}
		return res;
	}
    /**
     * 查询所有的分组信息，parant_id=101及下级节点
     */
	@Cache(key = "dmsBaseDictService.queryAllGroups", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
		   redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@Override
	public List<DmsBaseDict> queryAllGroups() {
		return dmsBaseDictDao.queryAllGroups();
	}
}

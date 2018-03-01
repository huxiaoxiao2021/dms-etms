package com.jd.bluedragon.distribution.base.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.print.domain.SignConfig;
import com.jd.ql.dms.common.web.mvc.api.PagerCondition;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 *
 * @ClassName: DmsBaseDictService
 * @Description: 数据字典--Service接口
 * @author wuyoude
 * @date 2017年12月28日 09:24:12
 *
 */
public interface DmsBaseDictService extends Service<DmsBaseDict> {
	/**
     * 字典名称-打标位配置后缀
     */
    public static final String DIC_SIGN_CONFIG_POSITION_SUFFIX = "PositionConfig";
    /**
     * 字典名称-打标位定义后缀
     */
    public static final String DIC_SIGN_CONFIG_TEXTS_SUFFIX = "TextsConfig";
    /**
     * 根据parentId和typeGroup查找分拣基础数据
     * @param parentId
     * @param typeGroup
     * @return
     */
    public List<DmsBaseDict> queryByParentIdAndTypeGroup(Integer parentId, Integer typeGroup);

    /**
     * 根据查询条件获取数据字典数据
     * @param pagerCondition
     * @return
     */
    public List<DmsBaseDict> queryByCondition(PagerCondition pagerCondition);
    /**
     * 根据parentId查找所有下级节点数据,返回list
     * @param parentId
     * @return
     */
    List<DmsBaseDict> queryListByParentId(Integer parentId);
    /**
     * 根据parentId查找所有下级节点数据,返回以字段TypeGroup为key的map
     * @param parentId
     * @return
     */
    Map<Integer, List<DmsBaseDict>> queryMapByParentId(Integer parentId);
    /**
     * 根据名称查询根节点配置，parantId = 0的
     * @param typeName
     * @return
     */
    DmsBaseDict queryRootNodeByTypeName(String typeName);
    /**
     * 根据打标配置标识名查询打标配置信息
     * @param signConfigName
     * @return
     */
    Map<Integer, SignConfig> getSignConfigsByConfigName(String signConfigName);
}

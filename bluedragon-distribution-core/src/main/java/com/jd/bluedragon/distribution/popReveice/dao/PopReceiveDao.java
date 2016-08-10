package com.jd.bluedragon.distribution.popReveice.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-14 下午08:43:23
 *
 * 类说明
 */
public class PopReceiveDao extends BaseDao<PopReceive> {

	public static final String namespace = PopReceiveDao.class.getName();
	
	/**
	 * 根据防重码查询收货记录
	 * @param waybillCode
	 * @return
	 */
	public PopReceive findByFingerPrint(String fingerPrint) {
		Object obj = this.getSqlSession().selectOne(namespace + ".findByFingerPrint", fingerPrint);
		PopReceive popReveice = (obj == null) ? null : (PopReceive) obj;
		return popReveice;
	}

	@SuppressWarnings("unchecked")
	public List<PopReceive> findListByPopReceive(Map<String, Object> paramMap) {
		Object obj = this.getSqlSession().selectList(namespace + ".findListByPopReceive", paramMap);
		List<PopReceive> popReceives = (List<PopReceive>)((obj == null) ? null : obj);
		return popReceives;
	}
	
	/**
	 * 根据运单号查询打印记录
	 * @param waybillCode
	 * @return
	 */
	public PopReceive findByWaybillCode(String waybillCode) {
		Object obj = this.getSqlSession().selectOne(namespace + ".findByWaybillCode", waybillCode);
		PopReceive popReveice = (obj == null) ? null : (PopReceive) obj;
		return popReveice;
	}
	
	public List<PopReceive> findPopReceiveList(Map<String,Object> map){
		@SuppressWarnings("unchecked")
		List<PopReceive> list = this.getSqlSession().selectList(namespace+".findPopReceiveList", map);
		return list;
	}
	
	public int count(Map<String,Object> map){
		 Integer count = (Integer) this.getSqlSession().selectOne(
				 namespace+".count", map);
	        return count;
	}

}

package com.jd.bluedragon.distribution.popPrint.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopSignin;
import com.jd.bluedragon.distribution.popPrint.dto.PopSigninDto;
/**
 * 
* 类描述： POP 托寄签收
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-1-24 下午2:40:38
* 版本号： v1.0
 */
public class PopSigninDao extends BaseDao<PopSignin>{
	
	public static final String namespace = PopSigninDao.class.getName();
	public int insert(PopSignin popSignin){
		int n = this.getSqlSession().insert(namespace+".insert", popSignin);
		return n;
	}
	
	public int update(PopSignin popSignin){
		int n = this.getSqlSession().update(namespace+".update", popSignin);
		return n;	
	}
	
	public List<PopSignin> getPopSigninList(PopSigninDto popSigninDto){
		List<PopSignin> list = (List<PopSignin>)this.getSqlSession().selectList(namespace+".getPopSigninList", popSigninDto);
		return list;
	}
	public int getCount(PopSigninDto popSigninDto){
		 Integer count = (Integer) this.getSqlSession().selectOne(namespace+".getCount", popSigninDto);
		return count;
	}

}

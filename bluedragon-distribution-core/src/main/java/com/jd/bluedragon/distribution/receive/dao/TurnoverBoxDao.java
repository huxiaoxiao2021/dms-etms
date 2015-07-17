package com.jd.bluedragon.distribution.receive.dao;

import java.util.List;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;

public class TurnoverBoxDao extends BaseDao<TurnoverBox> {
	public static final String namespace = TurnoverBoxDao.class.getName();
	
	public List<TurnoverBox> getTurnoverBoxList(TurnoverBox turnoverBox){
		@SuppressWarnings("unchecked")
		List<TurnoverBox> list =	(List<TurnoverBox>)this.getSqlSession().selectList(namespace+".getTurnoverBoxList",turnoverBox);
		return list; 
	}
	
	public int getCount(TurnoverBox turnoverBox){
		 Integer count = (Integer) this.getSqlSession().selectOne(namespace+".getCount", turnoverBox);
	               
		return count;
	}
}

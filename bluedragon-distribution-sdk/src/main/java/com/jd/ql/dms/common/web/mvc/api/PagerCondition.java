package com.jd.ql.dms.common.web.mvc.api;

import java.io.Serializable;
/**
 * 
 * @ClassName: PagerCondition
 * @Description: 分页查询条件接口
 * @author: wuyoude
 * @date: 2017年12月27日 下午1:24:20
 *
 */
public interface PagerCondition extends Serializable{
	/**
	 * 获取分页参数-开始位置
	 * @return
	 */
	public int getOffset();
	/**
	 * 获取分页参数-数据条数
	 * @return
	 */
	public int getLimit();
}

package com.jd.ql.dms.common.web.mvc.api;

import java.io.Serializable;
/**
 * 
 * @ClassName: Entity
 * @Description: 实体定义接口
 * @author: wuyoude
 * @date: 2017年12月20日 下午5:41:03
 *
 */
public interface Entity extends Serializable{
	/**
	 * 设置实体id值
	 * @param id
	 */
	public void setId(Long id);
	/**
	 * 获取实体id
	 * @return
	 */
	public Long getId();
}

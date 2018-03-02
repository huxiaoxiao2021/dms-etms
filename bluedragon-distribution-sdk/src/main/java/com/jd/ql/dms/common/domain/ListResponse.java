package com.jd.ql.dms.common.domain;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @ClassName: ListResponse
 * @Description: List类型的返回结果
 * @author: wuyoude
 * @date: 2017年12月20日 下午4:03:13
 * 
 * @param <E> List数据集合的类型
 */
public class ListResponse<E> extends JdResponse<List<E>>{
    
    private static final long serialVersionUID = 1L;
    
	/**
	 * 添加一个对象
	 * @param e
	 */
	public void add(E e){
		if(this.data == null){
			this.data = new ArrayList<E>();
		}
		this.data.add(e);
	}
	/**
	 * 添加一个集合对象
	 * @param e
	 */
	public void add(List<E> list){
		if(this.data == null){
			this.data = new ArrayList<E>();
		}
		this.data.addAll(list);
	}	
}

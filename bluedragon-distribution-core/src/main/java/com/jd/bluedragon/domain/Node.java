package com.jd.bluedragon.domain;

import java.io.Serializable;

/**
 * 
 * @ClassName: Node
 * @Description: (基础类，包含3个字段id,code,name)
 * @author wuyoude
 * @date 2017年6月7日 下午3:09:02
 *
 */
public class Node implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected Integer id;
	protected String code;
	protected String name;
	
	public Node(){
		super();
	}
	public Node(Integer id, String name) {
		this(id,""+id,name);
	}
	public Node(String code, String name) {
		this(null,code,name);
	}
	public Node(Integer id, String code, String name) {
		this();
		this.id = id;
		this.code = code;
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

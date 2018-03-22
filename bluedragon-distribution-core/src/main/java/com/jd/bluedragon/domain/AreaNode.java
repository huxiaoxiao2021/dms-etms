package com.jd.bluedragon.domain;


/**
 * 
 * @ClassName: AreaNode
 * @Description: (区域节点)
 * @author wuyoude
 * @date 2017年6月7日 下午3:32:54
 *
 */
public class AreaNode extends Node{
	private static final long serialVersionUID = 1L;
	public AreaNode() {
		super();
	}
	public AreaNode(Integer id, String name) {
		super(id,name);
	}
	
	@Override
	public String toString() {
		return "AreaNode [id=" + id + ", name=" + name + "]";
	}
	
}

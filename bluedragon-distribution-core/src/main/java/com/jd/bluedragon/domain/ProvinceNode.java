package com.jd.bluedragon.domain;

/**
 * 
 * @ClassName: PronvenceNode
 * @Description: (省节点)
 * @author wuyoude
 * @date 2017年6月7日 下午4:11:17
 *
 */
public class ProvinceNode extends Node{
	private static final long serialVersionUID = 1L;
	/**
	 * 区域信息
	 */
	private AreaNode area;

	public ProvinceNode(Integer id, String name){
		super(id,name);
	}

	public ProvinceNode(Integer id, String name,AreaNode area) {
		super(id,name);
		this.area = area;
	}
	public AreaNode getArea() {
		return area;
	}
	public void setArea(AreaNode area) {
		this.area = area;
	}
	@Override
	public String toString() {
		return "ProvinceNode [id=" + id + ", code=" + code + ", name=" + name
				+ ", area=" + area + "]";
	}
	
}

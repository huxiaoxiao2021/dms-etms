package com.jd.bluedragon.distribution.kuguan.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.utils.StringHelper;


public class OrderStockInfo {
	private String waybillcode;
	private Integer type;
	private String fangshi = null;
	private String fenlei = null;
	private Long qita = null;
	private String qitafangshi = null;
	private int qnum = 0;
	
	//11, 13, 15, 16, 18, 19, 42, 56, 61  这些类型的直接可推
	List<Integer> otherTypes = Lists.newArrayList(11, 13, 15, 16, 18, 19, 42, 56, 61);
	
	//2.  0 21需要判断先后款
	List<Integer> ordinaryTypes = Lists.newArrayList(0,21);
	

	public OrderStockInfo(String waybillcode, String type, String fangshi, String fenlei,
			String qita, String qitafangshi) {
		this.waybillcode = waybillcode;
		if(StringHelper.isNotEmpty(type))
			this.type = Integer.valueOf(type);
		this.fangshi = fangshi;
		this.fenlei = fenlei;
		if(StringHelper.isNotEmpty(qita))
				this.qita = Float.valueOf(qita).longValue();
		this.qitafangshi = qitafangshi;
	}
	
	public OrderStockInfo(String waybillcode, String type, String fangshi, String fenlei,
			String qita, String qitafangshi, int qnum) {
		this.waybillcode = waybillcode;
		if(StringHelper.isNotEmpty(type))
			this.type = Integer.valueOf(type);
		this.fangshi = fangshi;
		this.fenlei = fenlei;
		if(StringHelper.isNotEmpty(qita))
				this.qita = Float.valueOf(qita).longValue();
		this.qitafangshi = qitafangshi;
		this.qnum = qnum;
	}
	
	public String getWaybillcode() {
		return waybillcode;
	}

	public void setWaybillcode(String waybillcode) {
		this.waybillcode = waybillcode;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getFangshi() {
		return fangshi;
	}

	public void setFangshi(String fangshi) {
		this.fangshi = fangshi;
	}

	public String getFenlei() {
		return fenlei;
	}

	public void setFenlei(String fenlei) {
		this.fenlei = fenlei;
	}

	public Long getQita() {
		return qita;
	}

	public void setQita(Long qita) {
		this.qita = qita;
	}

	public String getQitafangshi() {
		return qitafangshi;
	}

	public void setQitafangshi(String qitafangshi) {
		this.qitafangshi = qitafangshi;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("");
		sb.append("运单号=").append(waybillcode).append(",");
		sb.append("订单类型=").append(type).append(",");
		sb.append("方式=").append(fangshi).append(",");
		sb.append("分类=").append(fenlei).append(",");
		sb.append("其它=").append(qita).append(",");
		sb.append("其它方式=").append(qitafangshi);
		sb.append("总数量=").append(qnum);
		return sb.toString();
	}
	
	/**
	 * 根据库管自我判断是否推送库管思密达
	 * 主要是明确是否是先后款
	 * 
	 * @return
	 */
	public Boolean judgeStockInfo(){
		
		boolean result = true;
		String payInfo;
		
		String pay_post = "先货";
		String pay_pre = "先款";
		String pay_error = "无法判断先款先货";


		if ("出库".equals(fangshi) && "放货".equals(fenlei)
				&& (qita== 0)) {
			payInfo = pay_post;
		} else if ("出库".equals(fangshi) && "销售".equals(fenlei)) {
			payInfo = pay_pre;
		} else {
			payInfo = pay_error;
			//先货先款不确定只有对普通fbp订单的发送才有影响
			if(this.ordinaryTypes.contains(type)){
				result=false;
			}
		}
		return result;
	}
	
	/**
	 * 综合判断是否可以推
	 * 
	 * @return
	 */
	public String judge() {
		StringBuffer resultStr = new StringBuffer();
		if (type == null)
			resultStr.append("订单信息为空!");
		if(!this.ordinaryTypes.contains(type)&&!this.otherTypes.contains(type))
			resultStr.append("订单类型不支持!");
		if (!judgeStockInfo())
			resultStr.append("不确定先后款!");
		
		//如果没有报错信息则返回OK
		if (resultStr.length() > 0)
			return resultStr.toString()+this.toString();
		else
			return JdResponse.MESSAGE_OK;
	}
	
	public static void main(String args[]){
		String waybillcode = "12345";
		String type = "21";
		String fangshi = "出库";
		String fenlei = "放货1";
		String qita = "0";
		String qitafangshi = null;
		
		OrderStockInfo stInfo = new OrderStockInfo(waybillcode, type, fangshi, fenlei,
				qita, qitafangshi);
		System.out.println(stInfo.judge());
	}
}

package com.jd.bluedragon.distribution.kuguan.domain;

import java.util.List;

/**
 * 库管查询
 *
 * @author caoyunkun
 */
public class KuGuanDomain implements java.io.Serializable {

	private static final long serialVersionUID = -2128599558209868233L;

	/** 选择类型*/
	@Deprecated 
	private String ddlType;

	/** 输入单号 */
	private String waybillCode;

	/** 超链接库管单号*/
	private String lblKdanhao;

	/** 库管单号 */
	private String lKdanhao;

    /**
     * 方式
     * 对应出管 Churu
     */
	private String lblWay;

	/** 分类 */
    /**
     * 分类
     * 对应出管Fenlei
     */
	private String lblType;

    /**
     * 其他方式
     * 对应出管 iTaFangShi
     */
	private String lblOtherWay;

	/** 经办人 */
	private String lblJingban;

	/** 日期 */
	private String lblDate;

	/** 来源 */
	private String lblFrom;

	/** 款项 */
	private String lblKuanx;

	/** 运费 */
	private String lblYun;

	/** 优惠 */
	private String lblYouhui;

	/** 其他 */
	private String lblOther;

	/** 总金额 */
	private String lblZjine;

	/** 财务单号 */
	private String lblCdanhao1;

	/** 订单号 */
	private String lblOrderid;
	
	/** 财务单号 */
	private String lblCdanhao;

	/** 商品编号 */
	private String lblWareId;
	
	/** 商品名称 */
	private String lblWare;
	
	/** 数量 */
	private String lblNum;
	
	/** 单价 */
	private String lblPrice;
	
	/** 金额 */
	private String lbljine;
	
	/**机构 */
	private String lblOrg;
	
	/**仓库 */
	private String lblStock;
	
	/**录入员*/
	private String lblLuru;
	
	/**自提点*/
	private String lblStation;
	
	/**是否签字*/
	private String lblSure;
	
	/**原单号*/
	private String lblYdanhao;
	
	/**备注*/
	private String lblRemark;
	
	/**总计*/
	private String lblstatistics;

	/**	业务类型Id*/
	private Integer typeId;
	
	private List<KuGuanDomain> stockDetails;
	
	public String getLblStock() {
		return lblStock;
	}

	public void setLblStock(String lblStock) {
		this.lblStock = lblStock;
	}

	public String getLblLuru() {
		return lblLuru;
	}

	public void setLblLuru(String lblLuru) {
		this.lblLuru = lblLuru;
	}

	public String getLblStation() {
		return lblStation;
	}

	public void setLblStation(String lblStation) {
		this.lblStation = lblStation;
	}

	public String getLblSure() {
		return lblSure;
	}

	public void setLblSure(String lblSure) {
		this.lblSure = lblSure;
	}

	public String getLblYdanhao() {
		return lblYdanhao;
	}

	public void setLblYdanhao(String lblYdanhao) {
		this.lblYdanhao = lblYdanhao;
	}

	public String getLblRemark() {
		return lblRemark;
	}

	public void setLblRemark(String lblRemark) {
		this.lblRemark = lblRemark;
	}

	public String getLblOrg() {
		return lblOrg;
	}

	public void setLblOrg(String lblOrg) {
		this.lblOrg = lblOrg;
	}

	@Deprecated
	public String getDdlType() {
		return ddlType;
	}

	@Deprecated
	public void setDdlType(String ddlType) {
		this.ddlType = ddlType;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getLblKdanhao() {
		return lblKdanhao;
	}

	public void setLblKdanhao(String lblKdanhao) {
		this.lblKdanhao = lblKdanhao;
	}

	public String getlKdanhao() {
		return lKdanhao;
	}

	public void setlKdanhao(String lKdanhao) {
		this.lKdanhao = lKdanhao;
	}

	public String getLblWay() {
		return lblWay;
	}

	public void setLblWay(String lblWay) {
		this.lblWay = lblWay;
	}

	public String getLblType() {
		return lblType;
	}

	public void setLblType(String lblType) {
		this.lblType = lblType;
	}

	public String getLblOtherWay() {
		return lblOtherWay;
	}

	public void setLblOtherWay(String lblOtherWay) {
		this.lblOtherWay = lblOtherWay;
	}

	public String getLblJingban() {
		return lblJingban;
	}

	public void setLblJingban(String lblJingban) {
		this.lblJingban = lblJingban;
	}

	public String getLblDate() {
		return lblDate;
	}

	public void setLblDate(String lblDate) {
		this.lblDate = lblDate;
	}

	public String getLblFrom() {
		return lblFrom;
	}

	public void setLblFrom(String lblFrom) {
		this.lblFrom = lblFrom;
	}

	public String getLblKuanx() {
		return lblKuanx;
	}

	public void setLblKuanx(String lblKuanx) {
		this.lblKuanx = lblKuanx;
	}

	public String getLblYun() {
		return lblYun;
	}

	public void setLblYun(String lblYun) {
		this.lblYun = lblYun;
	}

	public String getLblYouhui() {
		return lblYouhui;
	}

	public void setLblYouhui(String lblYouhui) {
		this.lblYouhui = lblYouhui;
	}

	public String getLblOther() {
		return lblOther;
	}

	public void setLblOther(String lblOther) {
		this.lblOther = lblOther;
	}

	public String getLblZjine() {
		return lblZjine;
	}

	public void setLblZjine(String lblZjine) {
		this.lblZjine = lblZjine;
	}

	public String getLblCdanhao() {
		return lblCdanhao;
	}

	public void setLblCdanhao(String lblCdanhao) {
		this.lblCdanhao = lblCdanhao;
	}

	public String getLblOrderid() {
		return lblOrderid;
	}

	public void setLblOrderid(String lblOrderid) {
		this.lblOrderid = lblOrderid;
	}

	public String getLblWareId() {
		return lblWareId;
	}

	public void setLblWareId(String lblWareId) {
		this.lblWareId = lblWareId;
	}

	public String getLblWare() {
		return lblWare;
	}

	public void setLblWare(String lblWare) {
		this.lblWare = lblWare;
	}

	public String getLblNum() {
		return lblNum;
	}

	public void setLblNum(String lblNum) {
		this.lblNum = lblNum;
	}

	public String getLblPrice() {
		return lblPrice;
	}

	public void setLblPrice(String lblPrice) {
		this.lblPrice = lblPrice;
	}

	public String getLbljine() {
		return lbljine;
	}

	public void setLbljine(String lbljine) {
		this.lbljine = lbljine;
	}

	public String getLblCdanhao1() {
		return lblCdanhao1;
	}

	public void setLblCdanhao1(String lblCdanhao1) {
		this.lblCdanhao1 = lblCdanhao1;
	}

	public String getLblstatistics() {
		return lblstatistics;
	}

	public void setLblstatistics(String lblstatistics) {
		this.lblstatistics = lblstatistics;
	}

	public List<KuGuanDomain> getStockDetails() {
		return stockDetails;
	}

	public void setStockDetails(List<KuGuanDomain> stockDetails) {
		this.stockDetails = stockDetails;
	}

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}

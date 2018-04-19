package com.jd.bluedragon.distribution.b2bRouter.domain;

/**
 * Created by xumei3 on 2017/5/31.
 */
public class ProvinceAndCity {
    /**
     * 机构编号
     */
    private String assortCode;

    /**
     * 机构名称
     */
    private String assortName;

    public ProvinceAndCity() {
		super();
	}

	public ProvinceAndCity(String assortCode, String assortName) {
        this.assortCode = assortCode;
        this.assortName = assortName;
    }

    public String getAssortCode() {
        return assortCode;
    }

    public void setAssortCode(String assortCode) {
        this.assortCode = assortCode;
    }

    public String getAssortName() {
        return assortName;
    }

    public void setAssortName(String assortName) {
        this.assortName = assortName;
    }
}

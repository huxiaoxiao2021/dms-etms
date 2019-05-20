package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.Date;
import java.util.Map;

public class SortingResponse extends JdResponse {

    private static final long serialVersionUID = 5954178829551983145L;

    public static final Integer CODE_PARAM_IS_NULL = 22001;
    public static final String MESSAGE_PARAM_IS_NULL = "包裹号或运单号不能为空";

    public static final Integer CODE_SORTING_NOT_FOUND = 22002;
    public static final String MESSAGE_SORTING_NOT_FOUND = "无包裹或运单信息";

    public static final Integer CODE_SORTING_WAITING_PROCESS = 22003;
    public static final String MESSAGE_SORTING_WAITING_PROCESS = "包裹或运单信息正在处理，请稍候";
    
    public static final Integer CODE_SORTING_SENDED = 22004;
    public static final String MESSAGE_SORTING_SENDED = "包裹/运单/箱子已经发货或未分拣，不能取消分拣";

    public static final Integer CODE_SORTING_RECORD_NOT_FOUND = 22005;
    public static final String MESSAGE_SORTING_RECORD_NOT_FOUND = "无包裹/运单/箱子分拣记录";
    
    public static final Integer CODE_SORTING_INSPECTED = 22006;
    public static final String MESSAGE_SORTING_INSPECTED = "包裹已经验货，不能取消分拣";

    public static final Integer CODE_SORTING_CANCEL_PROCESS = 22007;
    public static final String MESSAGE_SORTING_CANCEL_PROCESS = "包裹/运单/箱子正在取消分拣，请稍后";

    public static final Integer CODE_PACKAGE_NUM_LIMIT = 22008;
    public static final String MESSAGE_PACKAGE_NUM_LIMIT = "运单/箱子的包裹数大于2万，请联系IT人员报备此操作";

    public static final Integer CODE_29212 = 29212;

    public static final Integer CODE_29300 = 29300;
    public static final String MESSAGE_29300 = "此[包裹]或[运单]已经[锁定]或[取消]，请联系客服或退货";
    
    public static final Integer CODE_293000 = 293000;
    public static final String MESSAGE_293000 = "此[包裹]或[运单]已经[锁定]或[取消]，未出库，请联系客服或退货";
    
    public static final Integer CODE_29301 = 29301;
    public static final String MESSAGE_29301 = "此[包裹]或[运单]为[锁定订单]，请联系客服";
    
    public static final Integer CODE_293010 = 293010;
    public static final String MESSAGE_293010 = "此[包裹]或[运单]为[锁定订单]，未出库，请联系客服";
    
    public static final Integer CODE_29302 = 29302;
    public static final String MESSAGE_29302 = "此[包裹]或[运单]为[取消订单]，请退货";
    
    public static final Integer CODE_293020 = 293020;
    public static final String MESSAGE_293020 = "此[包裹]或[运单]为[取消订单]，未出库，请退货";
    
    public static final Integer CODE_293040 = 293040;
    public static final String MESSAGE_293040 = "此[包裹]或[运单]为[未出库]，请联系客服";
    
    public static final Integer CODE_29303 = 29303;
    public static final String MESSAGE_29303 = "此[包裹]或[运单]为[退款100分订单]，请退货";

    public static final Integer CODE_29121 = 29121;
    public static final String MESSAGE_29121 = "此单为[妥投状态]，请先核实异常，在PDA上提交配送异常后再进行逆向操作!";

    public static final Integer CODE_29122 = 29122;
    public static final String MESSAGE_29122 = "此单获取运单状态异常!";

    public static final Integer CODE_31121 = 31121;
    public static final String MESSAGE_31121 = "此单仓储已收货，是否继续退货";

    public static final Integer CODE_31122 = 31122;
    public static final String MESSAGE_31122 = "此单已报丢报损，是否继续退货";


    /** 箱号 */
    private String boxCode;

    /** 运单号 */
    private String waybillCode;

    /** 包裹号 */
    private String packageCode;

    /** 创建站点编号 */
    private Integer createSiteCode;

    /** 创建站点名称 */
    private String createSiteName;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;

    /** 创建人编号 */
    private Integer createUserCode;

    /** 创建人 */
    private String createUser;

    /** 创建时间 */
    private Date createTime;

    /** 最后操作人编号 */
    private Integer updateUserCode;

    /** 最后操作人 */
    private String updateUser;

    /** 最后修改时间 */
    private Date updateTime;
    
    /**取消分拣处理结果*/
    private Map<String,Integer> cancelResult;
    
    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return this.packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return this.createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return this.receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getCreateUserCode() {
        return this.createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return this.createTime!=null?(Date)this.createTime.clone():null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime!=null?(Date)createTime.clone():null;
    }

    public Integer getUpdateUserCode() {
        return this.updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return this.updateTime!=null?(Date)this.updateTime.clone():null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
    }

    public Map<String,Integer> getCancelResult() {
		return cancelResult;
	}

	public void setCancelResult(Map<String,Integer> cancelResult) {
		this.cancelResult = cancelResult;
	}

	public SortingResponse() {
        super();
    }

    public SortingResponse(Integer code, String message) {
        super(code, message);
    }

    public SortingResponse(Integer code, String message, Map<String,Integer> cancelResult) {
		super(code, message);
		this.cancelResult = cancelResult;
	}

	@Override
    public String toString() {
        return "SortingResponse [getRequest()=" + this.getRequest() + ", getCode()="
                + this.getCode() + ", getMessage()=" + this.getMessage() + "]";
    }

}

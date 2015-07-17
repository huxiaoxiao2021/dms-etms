package com.jd.bluedragon.distribution.api.response;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

public class InspectionECResponse extends JdResponse{
	
	private static final long serialVersionUID = 2405387867539840405L;

	public static final String MESSAGE_PARAM_PARTNER_ERROR = "三方ID或者编码错误";
	
	public static final int CODE_PARAM_BOX_ERROR=10010;
			
	public static final String MESSAGE_PARAM_BOX_ERROR="箱号不正确，请检查箱号";
	
	public static final Integer CODE_WAYBILL_CANCEL = 1001;
	public static final String MESSAGE_WAYBILL_CANCEL = "运单下所有包裹已经取消,并收回已验货包裹";

	/*异常处理时，包裹不符合处理条件*/
	public static final int CODE_DISPOSE_ERROR = 10011;
	
	public static final int CODE_PARAM_BOX_INVALID=10012;
	public static final String MESSAGE_PARAM_BOX_INVALID="箱号无接收站点，请检查箱号";
	
	public static final int CODE_PARAM_UPPER_LIMIT=10013;
	public static final String MESSAGE_PARAM_UPPER_LIMIT="超过最大限额";
	
	private List<PackageResponse> lists;
	
	public InspectionECResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InspectionECResponse(Integer code, String message, List<PackageResponse> lists) {
		super(code, message);
		this.lists = lists;
	}
	public InspectionECResponse(Integer code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}


	public List<PackageResponse> getLists() {
		return lists;
	}

	public void setLists(List<PackageResponse> lists) {
		this.lists = lists;
	}

}

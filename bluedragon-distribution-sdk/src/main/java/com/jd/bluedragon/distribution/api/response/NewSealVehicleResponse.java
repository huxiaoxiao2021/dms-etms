package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class NewSealVehicleResponse<T> extends JdResponse {

	private static final long serialVersionUID = -9096768337471427500L;

	public static final Integer CODE_EXCUTE_ERROR = 10000;
	public static final Integer CODE_SEAL_SUCCEED_BUT_WARN = 20001;
	public static final String MESSAGE_EXCUTE_ERROR = "推送运输数据失败！";
	public static final String MESSAGE_QUERY_ERROR = "获取待解封车数据失败！";
	public static final String MESSAGE_SEAL_SUCCESS = "封车成功！";
	public static final String MESSAGE_CANCEL_SEAL_SUCCESS = "取消封车成功！";
	public static final String MESSAGE_UNSEAL_SUCCESS = "解封车成功！";

    public static final String TIPS_BATCHCODE_PARAM_ERROR = "请输入正确的批次号!";
    public static final String TIPS_SITECODE_PARAM_NULL_ERROR = "请输入查询条件!";
	public static final String TIPS_BATCHCODE_PARAM_NOTEXSITE_ERROR = "该批次号没有发货信息，请操作发货后封车!";
	public static final String TIPS_BATCHCODE_SEALED_ERROR = "该发货批次号已操作封车，无法重复操作!";
	public static final String TIPS_RECEIVESITE_DIFF_ERROR = "批次号与运力编码目的地不一致，请核准后重新操作!";
	public static final String TIPS_PRESEAL_PARAM_ERROR = "运力编码和车牌不能为空!";
	public static final String TIPS_PRESEAL_NOEXIST_ERROR = "该目的地没有预封车数据，无法更新预封车信息!";


    public static final Integer CODE_UNSEAL_CAR_OUT_CHECK = 30001;
    public static final String MESSAGE_UNSEAL_CAR_OUT_CHECK = "车辆不在围栏，是否强制解封：";

    public static final Integer CODE_TRANSPORT_RANGE_CHECK = 30002;
    public static final String MESSAGE_TRANSPORT_RANGE_OUT_CHECK = "此运力编码标准发车时间 {0}:{1} 是否使用此运力编码？";
    public static final Integer CODE_TRANSPORT_RANGE_ERROR = 30003;
    public static final String MESSAGE_TRANSPORT_RANGE_ERROR = "运力编码始发地非当前分拣中心，是否强制操作？";
    public static final Integer CODE_PRESEAL_EXIST_ERROR = 30004;
    public static final String TIPS_PRESEAL_EXIST_ERROR = "该目的地已提交预封车数据，是否取消上次预封车？";

	public NewSealVehicleResponse(){

	}

	public NewSealVehicleResponse(Integer code, String message){
		super(code, message);
	}

	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}

package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName OperateBizSubTypeEnum
 * @Description 操作子类型
 * @Author wyd
 * @Date 2022/1/4 16:49
 **/
public enum OperateBizSubTypeEnum {

	/**
	 * 验货
	 */
	INSPECTION("inspection","验货"),
	/**
	 * 收货
	 */
	RECEIVE("receive","收货"),
	/**
	 * 空铁提货
	 */
	AR_RECEIVE("ar_receive", "空铁提货"),

	/**
	 * 分拣
	 */
	SORTING("sorting","分拣"),
	/**
	 * 取消分拣
	 */
	SORTING_CANCEL("sorting_cancel","取消分拣"),
	/**
	 * 发货
	 */
	SEND("send_d","发货"),
	/**
	 * 取消发货
	 */
	SEND_CANCEL("send_d_cancel","取消发货"),

	/**
	 * 自动化组板
	 */
	SORT_ADD_TO_BOARD("sortAddToBoard","自动化组板"),
	/**
	 * 自动化扫描(组板+发货)
	 */
	SORT_MACHINE_BOARD("sortMachineComBoard","自动化扫描(组板+发货)"),
	/**
	 * 自动化组板(自动开板)
	 */
	SORT_COMBINATION_BOARD_NEW("sortCombinationBoardNew","自动化组板(自动开板)"),
	/**
	 * 人工组板(自动开板)
	 */
	COMBINATION_BOARD_NEW("combinationBoardNew","人工组板(自动开板)"),
	/**
	 * 组板发货岗扫描(组板+发货)
	 */
	JY_BOARD_SCAN("comBoardScan","组板发货岗扫描(组板+发货)"),
	/**
	 * 组板发货岗大宗扫描(组板+发货)
	 */
	JY_BOARD_WAYBILL_SCAN("comBoardWaybillScan","组板发货岗大宗扫描(组板+发货)"),

	/**
	 * 取消组板
	 */
	BOARD_CANCEL("board_cancel","取消组板"),

	/**
	 * 干支封车
	 */
	TRUNK_SEAL("trunk_seal","干支封车"),
	/**
	 * 传摆封车
	 */
	SHUTTLE_SEAL("shuttle_seal","传摆封车"),
	/**
	 * 接货仓封车
	 */
	WAREHOUSE_SEAL("warehouse_seal","接货仓封车"),
	/**
	 * 组板封车
	 */
	BOARD_SEAL("board_seal","组板封车"),
	/**
	 * 空铁封车
	 */
	AR_SEAL("ar_seal","空铁封车"),
	/**
	 * 工作台一键封车
	 */
	WORKBENCH_SEAL("workbench_seal","工作台一键封车"),
	/**
	 * 离线封车
	 */
	OFFLINE_SEAL("offline_seal","离线封车"),
	/**
	 * 离线传摆封车
	 */
	OFFLINE_SHUTTLE_SEAL("offline_shuttle_seal","离线传摆封车"),

	/**
	 * 解封车
	 */
	UNSEAL("unseal", "解封车"),
	/**
	 * 提报异常并解封车
	 */
	ABNORMAL_UNSEAL("abnormal_unseal", "提报异常并解封车"),

	/**
	 * 异常处理
	 */
	ABNORMAL_HANDLE("abnormal_handle", "异常处理"),
	/**
	 * 异常提报(新)
	 */
	ABNORMAL_REPORT_H5("abnormal_report_h5", "异常提报(新)"),

	/**
	 * 按包裹称重
	 */
	PACKAGE_WEIGHT_VOLUME("package_weight_volume", "按包裹称重"),
	/**
	 * 按运单称重
	 */
	WAYBILL_WEIGHT_VOLUME("waybill_weight_volume", "按运单称重"),
	/**
	 * 按箱称重
	 */
	BOX_WEIGHT_VOLUME("box_weight_volume", "按箱称重"),
	/**
	 * 按加盟商交接称重
	 */
	HANDOVER_WEIGHT_VOLUME("handover_weight_volume", "按加盟商交接称重");


	private OperateBizSubTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	private String code;
	private String name;
	/**
	 * 根据code获取名称
	 * @param code
	 * @return
	 */
    public static String getNameByCode(String code) {
    	OperateBizSubTypeEnum data = getEnum(code);
    	if(data != null) {
    		return data.getName();
    	}
        return null;
    }
	/**
	 * 根据code获取enum
	 * @param code
	 * @return
	 */
    public static OperateBizSubTypeEnum getEnum(String code) {
        for (OperateBizSubTypeEnum value : OperateBizSubTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
    /**
     * 判断code是否存在
     * @param code
     * @return
     */
    public boolean exist(String code) {
        return null != getEnum(code);
    }

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}

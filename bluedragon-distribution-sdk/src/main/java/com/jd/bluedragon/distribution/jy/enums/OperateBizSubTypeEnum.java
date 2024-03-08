package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName OperateBizSubTypeEnum
 * @Description 操作子类型
 * @Author wyd
 * @Date 2022/1/4 16:49
 **/
public enum OperateBizSubTypeEnum {

	INSPECTION("inspection","验货"),
	RECEIVE("receive","收货"),

	SORTING("sorting","分拣"),
	SORTING_CANCEL("sorting_cancel","取消分拣"),
	SEND("send_d","发货"),
	SEND_CANCEL("send_d_cancel","取消发货"),

	SORT_ADD_TO_BOARD("sortAddToBoard","自动化组板"),
	SORT_MACHINE_BOARD("sortMachineComBoard","自动化扫描(组板+发货)"),
	SORT_COMBINATION_BOARD_NEW("sortCombinationBoardNew","自动化组板(自动开板)"),
	COMBINATION_BOARD_NEW("combinationBoardNew","人工组板(自动开板)"),
	JY_BOARD_SCAN("comBoardScan","组板发货岗扫描(组板+发货)"),
	JY_BOARD_WAYBILL_SCAN("comBoardWaybillScan","组板发货岗大宗扫描(组板+发货)"),

	BOARD_CANCEL("board_cancel","取消组板"),

	UNSEAL("unseal", "解封车"),
	ABNORMAL_UNSEAL("abnormal_unseal", "提报异常并解封车"),

	ABNORMAL_DELIVERY("abnormal_delivery", "配送异常"),
    ;
	
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

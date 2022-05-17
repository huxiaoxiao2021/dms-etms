package com.jd.bluedragon.distribution.board.domain;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class BoardConstants {
    /**
     * 组板流向模式记录  erp维度
     */
    public static final String CACHE_KEY_BOARD_FLOW_TYPE_ERP = "cache_key_board_flow_type_erp_";
    /**
     *  redis 有效期（天） CACHE_KEY_BOARD_FLOW_TYPE_ERP_TIME
     *  下一次操作距离创建时间达到 CACHE_KEY_BOARD_FLOW_TYPE_ERP_TIME_RENEWAL 时做redis续期
     */
    public static final Integer CACHE_KEY_BOARD_FLOW_TYPE_ERP_TIME = 45;
    public static final long CACHE_KEY_BOARD_FLOW_TYPE_ERP_TIME_RENEWAL = 30l * 24l * 3600l * 1000l;

    /**
     * 组板流向模式： 1单流向 ， 2 多流向 (默认）
     */
    public static final int SINGLE_FLOW_BOARD = 1;
    public static final int MULTI_FLOW_BOARD = 2;


}

package com.jd.bluedragon.distribution.print.domain;


import java.io.Serializable;

/**
 * @author liwenji
 * @description 
 * @date 2023-06-06 16:05
 */
public class TrackDto implements Serializable {
    
    public static final Integer GET_START_SITE_ID = 1;
    
    public static final String GET_C2C_START_SITE_ID_1 = "c2c订单特殊处理，获取到始发分拣中心为：%s";

    public static final String GET_NOT_C2C_START_SITE_ID_1 = "获取到始发分拣中心为：%s";

    public static final Integer SCRAP_SORTING_SITE_ID = 2;
    public static final Integer NO_PREPARE_SITE_CODE = 3;

    public static final Integer USE_NEW_CROSS_PACKAGE_TAG_WS_UCC_OPEN = 4;
    public static final Integer AVIATION_TYPE = 5;
    public static final Integer NOT_AVIATION_TYPE = 6;
    public static final Integer GET_REVERSE_CROSS_PACKAGE = 7;
    public static final Integer ZI_TI_TYPE = 8;
    public static final String ZI_TI_TYPE_8 = "自提类型更换目的地地址信息：%s";
    public static final Integer HIDDEN_CROSS_INFO = 10;
    public static final String HIDDEN_CROSS_INFO_9 = "waybillSign第31位标识为3，隐藏面单信息";
    public static final Integer PRINT_CROSS_RESULT = 9;
    public static final String PRINT_CROSS_RESULT_SUCCESS_9 = "获取滑道笼车成功： %s";
    public static final String PRINT_CROSS_RESULT_ERROR_9 = "获取滑道笼车失败： %s";


    public static final String AVIATION_TYPE_START_END_EQUAL = "查询航空类型没有数据就查询陆运大全表，始发和目的相等维护了道口，可以返回陆运大全表";

    public static final String GET_CROSS_PACKAGE_TAG_BY_PARA_METHOD = "basicSecondaryWS.getCrossPackageTagByPara";
    public static final String QUERY_CROSS_PACKAGE_TAG_BY_PARAM_METHOD = "baseCrossPackageTagWS.queryCrossPackageTagByParam";
    public static final String GET_REVERSE_CROSS_PACKAGE_TAG_METHOD =  "basicSecondaryWS.getReverseCrossPackageTag";
    public static final String QUERY_CROSS_DETAIL_BY_DMS_ID_AND_SITE_CODE = "basicSortCrossDetailWS.queryCrossDetailByDmsIdAndSiteCode";

    public static final String CROSS_CONF_NEED_CREAT = "未查询到始发地：%s --> 目的地：%s 【%s】类型的滑道笼车信息，请联系省区负责人配置！ " +
            "https://joyspace.jd.com/pages/6w8WdgnEgwR5Cc9Dd5j2";

    private String request;
    
    private String response;
    
    private String log;
    
    private String method;
    
    private Integer id;
    
    public TrackDto(Integer id, String log) {
        this.id = id;
        this.log = log;
    }
    public TrackDto(Integer id) {
        this.id = id;
    }

    public TrackDto(Integer id, String log, String request, String response, String method) {
        this.id = id;
        this.log = log;
        this.request = request;
        this.response = response;
        this.method = method;
    }
    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

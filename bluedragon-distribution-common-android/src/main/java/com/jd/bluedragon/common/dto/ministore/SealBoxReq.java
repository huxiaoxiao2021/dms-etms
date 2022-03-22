package com.jd.bluedragon.common.dto.ministore;

import java.util.List;

public class SealBoxReq {
    private Long   miniStoreBindRelationId;//绑定关系id
    private String storeCode;
    private List<String> iceBoardCodes;
    private String boxCode;
    private List<String> packageCodes;
}

package com.jd.bluedragon.distribution.jy.dto.collectpackage;

import com.jd.bluedragon.distribution.api.domain.OperatorData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class BatchCancelCollectPackageMqDto implements Serializable {
    private static final long serialVersionUID = 1754900652075377158L;
    private String bizId;
    private String boxCode;
    private List<String> packageCodeList;
    private String updateUserName;
    private String updateUserErp;
    private Integer updateUserCode;
    private Integer siteCode;
    private String siteName;
    /**
     * 操作数据对象
     */
    private OperatorData operatorData;



}

package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class CancelComBoardDto extends BaseReq implements Serializable {
    private static final long serialVersionUID = 8158324710994501051L;
    private List<String> wayBillCodeList;
    private List<String> packageCodeList;
}

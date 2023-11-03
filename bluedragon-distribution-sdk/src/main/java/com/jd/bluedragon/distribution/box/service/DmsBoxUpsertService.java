package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.api.request.box.CreateBoxReq;
import com.jd.bluedragon.distribution.api.response.box.CreateBoxInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.BoxDto;
import com.jd.bluedragon.distribution.box.domain.BoxGenReq;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * @ClassName DmsBoxUpsertService
 * @Description 箱号对外修改服务
 * @Author wyh
 * @Date 2021/2/2 18:39
 **/
public interface DmsBoxUpsertService {

    /**
     * 生成箱号接口
     * @param req
     * @return
     */
    InvokeResult<BoxDto> generateBox(BoxGenReq req);

}

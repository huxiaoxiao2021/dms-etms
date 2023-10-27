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

    /**
     * 创建箱号
     *
     * @param createBoxReq 创建箱号入参
     * @return 箱号数据
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    Result<CreateBoxInfo> createBox(CreateBoxReq createBoxReq);
}

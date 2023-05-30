package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpBaseReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskPageReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskStatisticsReq;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskOfWaitReceiveDto;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/25 21:25
 * @Description: 三无
 */
public interface JySanwuExceptionService {

    JdCResponse<List<ExpTaskOfWaitReceiveDto>> getWaitReceiveSanwuExceptionByPage(ExpTaskStatisticsReq req);

}

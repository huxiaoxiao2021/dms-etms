package com.jd.bluedragon.distribution.reprintRecord.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordQuery;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 包裹补打jsf接口
 *
 * @author fanggang7
 * @time 2020年11月01日21:14:05 周日
 */
public interface ReprintRecordJsfService {

    /**
     * 统计总数
     * @param query 查询参数
     * @return 分页数据结果
     * @author fanggang7
     * @date 2020-11-03 14:30:34 周二
     */
    Response<Long> queryCount(ReprintRecordQuery query);

    /**
     * 分页查询
     *
     * @param query 请求参数
     * @return 分页数据
     * @author fanggang7
     * @date 2020-11-01 22:15:47 周日
     */
    Response<PageDto<ReprintRecordVo>> queryPageList(ReprintRecordQuery query);
}

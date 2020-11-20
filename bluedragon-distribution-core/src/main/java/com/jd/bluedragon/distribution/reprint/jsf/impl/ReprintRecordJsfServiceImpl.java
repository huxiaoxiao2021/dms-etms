package com.jd.bluedragon.distribution.reprint.jsf.impl;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordQuery;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordVo;
import com.jd.bluedragon.distribution.reprintRecord.service.ReprintRecordJsfService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 包裹补打记录
 *
 * @author fanggang7
 * @time 2020年11月01日21:53:33 周日
 */
@Service("dmsReprintRecordService")
public class ReprintRecordJsfServiceImpl implements ReprintRecordJsfService {

    @Autowired
    private ReprintRecordService reprintRecordService;

    /**
     * 统计总数
     *
     * @param query 查询参数
     * @return 分页数据结果
     * @author fanggang7
     * @date 2020-11-03 14:30:34 周二
     */
    @Override
    public Response<Long> queryCount(ReprintRecordQuery query) {
        return reprintRecordService.queryCount(query);
    }

    /**
     * 分页查询
     *
     * @param query 请求参数
     * @return 分页数据
     * @author fanggang7
     * @date 2020-11-01 22:15:47 周日
     */
    @Override
    public Response<PageDto<ReprintRecordVo>> queryPageList(ReprintRecordQuery query) {
        return reprintRecordService.queryPageList(query);
    }
}

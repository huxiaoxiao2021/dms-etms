package com.jd.bluedragon.distribution.reprint.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordQuery;
import com.jd.bluedragon.distribution.reprintRecord.dto.ReprintRecordVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

public interface ReprintRecordService {

    /**
     * 判断运单是否被补打过
     * @param barCode 运单号或包裹号
     * @return true表示补打过，false表示还没有补打过
     */
    boolean isBarCodeRePrinted(String barCode);

    void insertRePrintRecord(ReprintRecord rePrintRecord);

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
     * @param query 查询参数
     * @return 分页数据结果
     * @author fanggang7
     * @date 2020-11-03 14:30:34 周二
     */
    Response<PageDto<ReprintRecordVo>> queryPageList(ReprintRecordQuery query);
}

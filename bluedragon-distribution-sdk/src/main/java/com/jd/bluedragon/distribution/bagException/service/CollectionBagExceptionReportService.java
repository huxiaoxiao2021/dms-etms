package com.jd.bluedragon.distribution.bagException.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.bagException.domain.CollectionBagExceptionReport;
import com.jd.bluedragon.distribution.bagException.request.CollectionBagExceptionReportQuery;
import com.jd.bluedragon.distribution.bagException.vo.CollectionBagExceptionReportVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 集包异常举报
 *
 * @author fanggang7
 * @time 2020-09-24 21:32:41 周四
 */
public interface CollectionBagExceptionReportService {

    /**
     * 分页查询通知数据
     * @param query 查询参数
     * @return 分页数据结果
     * @author fanggang7
     * @date 2020-07-02 16:41:33 周四
     */
    Response<PageDto<CollectionBagExceptionReportVo>> queryPageList(CollectionBagExceptionReportQuery query);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return CollectionBagExceptionReport 通知详情
     * @author fanggang7
     * @date 2020-07-07 19:52:00 周二
     */
    Response<CollectionBagExceptionReport> selectByPrimaryKey(Long id);

    /**
     * 新增
     * @param collectionBagExceptionReport CollectionBagExceptionReport
     * @return Response 插入后自增ID
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    Response<Integer> add(CollectionBagExceptionReport collectionBagExceptionReport);

    /**
     * 按主键更新
     * @param collectionBagExceptionReport CollectionBagExceptionReport
     * @return Response 更新条数
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    Response<Integer> updateByPrimaryKey(CollectionBagExceptionReport collectionBagExceptionReport);

}

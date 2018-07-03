package com.jd.bluedragon.distribution.abnormalDispose.service;

import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeCondition;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeInspection;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeMain;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeSend;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年06月18日 09时:36分
 */
public interface AbnormalDisposeService {

    /**
     * 查询未验货明细
     *
     * @param abnormalDisposeCondition
     * @return
     */
    public PagerResult<AbnormalDisposeInspection> queryInspection(AbnormalDisposeCondition abnormalDisposeCondition);

    /**
     * 查询主页面统计
     *
     * @param abnormalDisposeCondition
     * @return
     */
    public PagerResult<AbnormalDisposeMain> queryMain(AbnormalDisposeCondition abnormalDisposeCondition);

    /**
     * 查询未发货明细
     *
     * @param abnormalDisposeCondition
     * @return
     */
    public PagerResult<AbnormalDisposeSend> querySend(AbnormalDisposeCondition abnormalDisposeCondition);

    /**
     * 保存异常编码
     *
     * @param abnormalDisposeInspection
     * @return
     */
    JdResponse<String> saveAbnormalQc(AbnormalDisposeInspection abnormalDisposeInspection);

    /**
     * 导出未验货数据
     *
     * @param abnormalDisposeCondition
     * @return
     */
    public List<List<Object>> getExportDataInspection(AbnormalDisposeCondition abnormalDisposeCondition);

    /**
     * 导出未发货明细
     *
     * @param abnormalDisposeCondition
     * @return
     */
    public List<List<Object>> getExportDataSend(AbnormalDisposeCondition abnormalDisposeCondition);
}

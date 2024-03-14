package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealAddDto;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealDto;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealRes;

import java.util.List;

/**
 * @author pengchong28
 * @description 装车评价申诉服务
 * @date 2024/3/1
 */
public interface JyEvaluateAppealService {
    /**
     * 根据条件获取评价记录申诉实体列表
     * @param conditions 条件列表
     * @return 符合条件的评价记录申诉实体列表
     */
    Response<List<JyEvaluateRecordAppealDto>> getListByCondition(List<String> conditions);

    /**
     * 提交装车评价申诉数据
     * @param addDto 待添加的装车评价申诉数实体列表
     * @return 响应是否成功的布尔值
     */
    Response<Boolean> submitAppeal(JyEvaluateRecordAppealAddDto addDto);

    /**
     * 查询申诉详情，带图片
     * @param condition 条件
     * @return 符合条件的申诉详情列表
     */
    Response<List<JyEvaluateRecordAppealDto>> getDetailByCondition(JyEvaluateRecordAppealDto condition);

    /**
     * 申诉记录审核接口，1是通过，2是驳回
     * @param res 请求参数
     * @return 返回一个包含布尔值的Response对象，表示申诉列表是否有效
     */
    Response<Boolean> checkAppeal(JyEvaluateRecordAppealRes res);

    /**
     * 获取申诉被拒绝的次数
     * @param loadSiteCode 装载站点代码
     * @return 申诉被拒绝的次数
     */
    Response<Integer> getAppealRejectCount(Long loadSiteCode);
}

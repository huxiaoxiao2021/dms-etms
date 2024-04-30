package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.jy.evaluate.*;

import java.util.List;
import java.util.Map;

/**
 * @author pengchong28
 * @description 装车评价通用对外jsf服务
 * @date 2024/3/1
 */
public interface JyEvaluateCommonJsfService {

    /**
     * 根据条件获取评价记录申诉实体列表
     * @param conditions 条件列表
     * @return 符合条件的评价记录申诉实体列表
     */
    Response<List<JyEvaluateRecordAppealDto>> getListByCondition(List<String> conditions);

    /**
     * 申诉记录提交保存接口
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
     * @param res 申诉对象
     * @return 返回一个包含布尔值的Response对象，表示申诉列表是否有效
     */
    Response<Boolean> checkAppeal(JyEvaluateRecordAppealRes res);

    /**
     * 获取申诉被拒绝的次数
     * @param loadSiteCode 载入站点代码
     * @return 响应的整数类型数据
     */
    Response<Integer> getAppealRejectCount(Long loadSiteCode);


    /**
     * 更新权限通过ID
     * @param entity 要更新的权限实体
     * @return 布尔类型的响应，表示更新是否成功
     */    /**
     * 根据ID更新权限
     * @param entity 要更新的权限实体
     * @return 响应是否更新成功的布尔值
     */
    public Response<Boolean> updatePermissionsById(JyEvaluateAppealPermissionsEntity entity);


}

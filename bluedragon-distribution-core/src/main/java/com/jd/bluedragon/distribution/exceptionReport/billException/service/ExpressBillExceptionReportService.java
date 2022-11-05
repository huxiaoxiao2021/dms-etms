package com.jd.bluedragon.distribution.exceptionReport.billException.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.Enum.ExpressBillExceptionReportTypeEnum;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FaceFirstAbnormalType;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FaceSecondAbnormalType;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FirstSiteVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.ReportTypeVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.request.ExpressBillExceptionReportRequest;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.exceptionReport.billException.request.ExpressBillExceptionReportQuery;

import java.util.List;
import java.util.Map;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/21 14:43
 */
public interface ExpressBillExceptionReportService {
    /**
     * 面单异常举报提交
     * @param reportRequest
     * @return
     */
    JdCResponse<Boolean> reportExpressBillException(ExpressBillExceptionReportRequest reportRequest);

    /**
     * 通过包裹号 获取运单始发站点(贴面单的地方)
     * @param packageCode
     * @return
     */
    JdCResponse<FirstSiteVo> getFirstSiteByPackageCode(String packageCode);

    /**
     * 获取所有面单举报类型
     * @return
     */
    JdCResponse<Map<Integer,String>> getAllExceptionReportType();

    /**
     * 提供给安卓的面单举报类型 旧接口
     * @return
     */
    JdCResponse<List<ExpressBillExceptionReportTypeEnum>> getAllExceptionReportTypeList();


    /**
     * 获取所有面单举报类型-提供给安卓-新接口
     */
    JdCResponse<List<ReportTypeVo>> getAllExceptionReportTypeListNew();

    /**
     * 获取一级原因
     *
     * @return
     */
    List<FaceFirstAbnormalType> getFirstAbnormalType();

    /**
     * 根据一级原因编码获取二级原因
     *
     * @return
     */
    List<FaceSecondAbnormalType> getSecondAbnormalType(Integer firstAbnormalType);

    /**
     * 根据商家编码更新数据
     * 
     * @param query
     * @return
     */
    boolean updateByBusiCode(ExpressBillExceptionReport query);

    /**
     * 根据ID查询包裹图片
     *
     * @param id
     * @return
     */
    List<String> getPicUrlsById(Integer id);
}
    

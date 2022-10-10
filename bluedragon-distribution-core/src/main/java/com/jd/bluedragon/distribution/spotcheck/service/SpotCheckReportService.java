package com.jd.bluedragon.distribution.spotcheck.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.SpotCheckReportQueryCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.WeightVolumePictureDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;

/**
 * 抽检报表服务接口
 *
 * @author hujiping
 * @date 2021/12/13 3:21 下午
 */
public interface SpotCheckReportService {

    /**
     * 根据条件分页查询抽检数据
     *
     * @param condition
     * @return
     */
    PagerResult<WeightVolumeSpotCheckDto> listData(SpotCheckReportQueryCondition condition);

    /**
     * 根据条件分页查询已抽检包裹明细
     *
     * @param condition
     * @return
     */
    PagerResult<WeightVolumeSpotCheckDto> packageDetailListData(SpotCheckReportQueryCondition condition);

    /**
     * csv方式导出
     *
     * @param condition
     * @param bfw
     */
    void export(SpotCheckReportQueryCondition condition, BufferedWriter bfw);

    /**
     * 根据条件查询已抽检包裹图片
     *
     * @param condition
     * @return
     */
    InvokeResult<Pager<WeightVolumePictureDto>> searchPicture(SpotCheckReportQueryCondition condition);

    /**
     * 上传超标图片
     *
     * @param image
     * @param request
     * @return
     */
    InvokeResult<Boolean> uploadExcessPicture(MultipartFile image, HttpServletRequest request);

    /**
     * 安全校验
     *
     * @param waybillCode
     * @param userErp
     * @return
     */
    InvokeResult<Boolean> securityCheck(String waybillCode, String userErp);
}

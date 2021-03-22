package com.jd.bluedragon.distribution.sealVehicle;

import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealQueryRequest;
import com.jd.bluedragon.distribution.sealVehicle.domain.PassPreSealRecord;
import com.jd.bluedragon.distribution.sealVehicle.domain.QuickSealQueryRequest;
import com.jd.bluedragon.distribution.sealVehicle.domain.UnSealVehicleDetail;
import com.jd.bluedragon.distribution.sealVehicle.domain.UnSealVehicleInfo;
import com.jd.bluedragon.distribution.sealVehicle.domain.VehicleBaseInfo;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

public interface DmsSealVehicleService {

    /*
    * 获取未封车信息列表
    *
    * */
    JdResponse<List<UnSealVehicleInfo>> getUnSealVehicleInfo(Integer createSiteCode, Integer hourRange, String createUserErp);
    /*
    * 获取未封车信息列表(替代原有的方法，增加车牌号条件)
    *
    * */
    JdResponse<List<UnSealVehicleInfo>> getUnSealVehicleInfos(QuickSealQueryRequest queryRequest);
    /*
     * 获取未封车信息明细
     *
     * */
    JdResponse<UnSealVehicleDetail> getUnSealSendCodeDetail(Integer createSiteCode, String transportCode, Integer hourRange);

    /*
     * 获取车牌信息列表
     *
     * */
    JdResponse<List<VehicleBaseInfo>> getVehicleBaseInfoList(String transportCode);

    /*
    * 修改运力编码中的车辆基础信息
    * */
    JdResponse modifyVehicleBaseInfo(String transportCode, VehicleBaseInfo vehicleBaseInfo);


    /*
     * 校验数据提交
     *
     * hasBatchInfo: 前台一键封车提交是否传递运力下的批次信息
     * */
    JdResponse checkTransportVehicleSubmit(String transportCode, List<String> vehicleNumberList, Boolean hasBatchInfo);
    /**
     * 查询预封车看板信息
     * @param queryCondition
     * @return
     */
    JdResponse<PageDto<PassPreSealRecord>> queryPassPreSealData(PassPreSealQueryRequest queryCondition);
}

package com.jd.bluedragon.distribution.sealVehicle;

import com.jd.bluedragon.distribution.sealVehicle.domain.UnSealVehicleDetail;
import com.jd.bluedragon.distribution.sealVehicle.domain.UnSealVehicleInfo;
import com.jd.bluedragon.distribution.sealVehicle.domain.VehicleBaseInfo;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.List;

public interface DmsSealVehicleService {

    /*
    * 获取未封车信息列表
    *
    * */
    JdResponse<List<UnSealVehicleInfo>> getUnSealVehicleInfo(Integer createSiteCode, Integer hourRange);

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
     * */
    JdResponse checkTransportVehicleSubmit(String transportCode, List<String> vehicleNumberList, Boolean transportReady);

}

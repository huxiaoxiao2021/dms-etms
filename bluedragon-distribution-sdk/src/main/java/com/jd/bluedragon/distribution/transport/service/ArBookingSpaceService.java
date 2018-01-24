package com.jd.bluedragon.distribution.transport.service;

import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpaceCondition;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: ArBookingSpaceService
 * @Description: 空铁项目-订舱表--Service接口
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
public interface ArBookingSpaceService extends Service<ArBookingSpace> {

    /**
     * 导出记录
     * @param arBookingSpaceCondition
     * @return
     */
    public List<List<Object>> getExportData(ArBookingSpaceCondition arBookingSpaceCondition);

    /**
     * 保存或修改
     * @param arBookingSpace
     * @param userCode 登录人账户
     * @param userName 登录人名称
     * @param createSiteCode 分拣中心编号
     * @param createSiteName 分拣中心名称
     * @return
     */
    public boolean saveOrUpdate(ArBookingSpace arBookingSpace,String userCode,String userName,Long createSiteCode,String createSiteName);


    /**
     * excel 批量导入数据
     * @param list
     * @return
     */
    public boolean importExcel(List<ArBookingSpace> list,String userCode,String userName,Long createSiteCode,String createSiteName);
}

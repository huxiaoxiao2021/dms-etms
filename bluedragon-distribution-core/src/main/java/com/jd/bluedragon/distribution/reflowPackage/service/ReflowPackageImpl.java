package com.jd.bluedragon.distribution.reflowPackage.service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reflowPackage.dao.ReflowPackageDao;
import com.jd.bluedragon.distribution.reflowPackage.domain.ReflowPackage;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("reflowPackageService")
public class ReflowPackageImpl implements ReflowPackageService {
    private static final Logger log = LoggerFactory.getLogger(ReflowPackageImpl.class);

    @Autowired
    @Qualifier("reflowPackageDao")
    private ReflowPackageDao reflowPackageDao;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    /**
     * 新增
     * @param mode
     * @return
     */
    @Override
    public int add(ReflowPackage mode){
        return reflowPackageDao.add(mode);
    }

    /**
     * 更新
     * @param mode
     * @return
     */
    @Override
    public int update(ReflowPackage mode){
        return reflowPackageDao.update(mode);
    }

    /**
     * 判断数据是否存在
     * @param mode
     * @return
     */
    @Override
    public boolean isExist(ReflowPackage mode){
        return reflowPackageDao.getCount(mode)>0;
    }

    /**
     * 包裹回流扫描提交
     * @param mode
     * @return
     */
    @Override
    public JdCResponse<Boolean> reflowPackageSubmit(ReflowPackage mode){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        String waybillCode = WaybillUtil.getWaybillCodeByPackCode(mode.getPackageCode());
        Waybill waybill=this.waybillCommonService.findByWaybillCode(waybillCode);
        if(null==waybill){
            log.warn("获取不到运单信息：{}",waybillCode);
            result.toFail("包裹回流上报失败，没有找到该包裹运单信息");
            return result;
        }

        JdResult<CrossPackageTagNew> tag=getCrossAndCageCarByWaybill(mode.getSiteCode(),waybill);
        if(tag.isSucceed() && null!=tag.getData()) {
            mode.setChuteCode(tag.getData().getDestinationCrossCode());
            mode.setCageCarCode(tag.getData().getDestinationTabletrolleyCode());
        }

        int res=-1;
        if(isExist(mode)){
            res=update(mode);
        } else {
            res=add(mode);
        }

        if(res<0){
            result.toFail("包裹回流上报失败，数据落库异常");
        }

        return result;
    }

    /**
     * 根据运单始发地和运单信息获取运单目的地滑道号、目的地笼车号
     * @param originalDmsId 运单始发地
     * @param waybill 运单信息
     * @return destinationCrossCode：目的滑道；destinationTabletrolleyCode：目的笼车
     */
    public JdResult<CrossPackageTagNew> getCrossAndCageCarByWaybill(Integer originalDmsId,Waybill waybill){
        Integer receiveSiteCode=waybill.getSiteCode();
        //获取始发道口类型
        Integer originalCrossType = BusinessUtil.getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay());

        BaseDmsStore baseDmsStore = new BaseDmsStore();
        JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore,receiveSiteCode,originalDmsId,originalCrossType);
        if(!jdResult.isSucceed()) {
            log.warn("获取运单滑道号笼车号信息失败:{}，运单号:{}", jdResult.getMessage(),waybill.getWaybillCode());
        }

        return jdResult;
    }

    /**
     * 根据对象查询符合条件的数据
     * @param mode
     * @return
     */
    @Override
    public List<ReflowPackage> getDataByBean(ReflowPackage mode){
        return reflowPackageDao.getDataByBean(mode);
    }

}

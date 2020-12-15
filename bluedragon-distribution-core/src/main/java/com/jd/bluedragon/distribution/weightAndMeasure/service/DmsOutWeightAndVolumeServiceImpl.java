package com.jd.bluedragon.distribution.weightAndMeasure.service;

import com.jd.bluedragon.distribution.businessIntercept.domain.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.weightAndMeasure.dao.DmsOutWeightAndVolumeDao;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import com.jd.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("dmsOutWeightAndVolumeService")
public class DmsOutWeightAndVolumeServiceImpl implements DmsOutWeightAndVolumeService{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DmsOutWeightAndVolumeDao dmsOutWeightAndVolumeDao;

    @Autowired
    private IBusinessInterceptReportService businessInterceptReportService;

    // 称重量方节点
    @Value("${businessIntercept.dispose.node.weightAndVolume}")
    private Integer disposeNodeWeightAndVolume;

    /**
     * 保存分拣应付重量体积信息
     * @param dmsOutWeightAndVolume
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public void saveOrUpdate(DmsOutWeightAndVolume dmsOutWeightAndVolume){
        List<DmsOutWeightAndVolume> weightAndVolumeList = dmsOutWeightAndVolumeDao.queryByBarCode(dmsOutWeightAndVolume);

        if(weightAndVolumeList == null || weightAndVolumeList.size()<1){
            dmsOutWeightAndVolumeDao.add(dmsOutWeightAndVolume);
        }else{
            dmsOutWeightAndVolumeDao.update(dmsOutWeightAndVolume);
        }
        this.sendDisposeAfterInterceptMsg(dmsOutWeightAndVolume);
    }

    /**
     * 发送拦截后处理动作消息
     * @return 发送结果
     * @author fanggang7
     * @time 2020-12-10 11:21:39 周四
     */
    private boolean sendDisposeAfterInterceptMsg(DmsOutWeightAndVolume dmsOutWeightAndVolume){
        long currentTimeMillis = System.currentTimeMillis();
        SaveDisposeAfterInterceptMsgDto saveDisposeAfterInterceptMsgDto = new SaveDisposeAfterInterceptMsgDto();
        saveDisposeAfterInterceptMsgDto.setBarCode(dmsOutWeightAndVolume.getBarCode());
        saveDisposeAfterInterceptMsgDto.setOperateTime(currentTimeMillis);
        saveDisposeAfterInterceptMsgDto.setDisposeNode(disposeNodeWeightAndVolume);
        saveDisposeAfterInterceptMsgDto.setOperateUserCode(dmsOutWeightAndVolume.getWeightUserCode());
        saveDisposeAfterInterceptMsgDto.setOperateUserName(dmsOutWeightAndVolume.getWeightUserName());

        String saveInterceptMqMsg = JSON.toJSONString(saveDisposeAfterInterceptMsgDto);
        try {
            businessInterceptReportService.sendDisposeAfterInterceptMsg(saveDisposeAfterInterceptMsgDto);
        } catch (Exception e) {
            log.error("sendInterceptMsg exception [{}]" , saveInterceptMqMsg, e);
        }
        return true;
    }

    /**
     * 查询这个分拣中心对该箱号/包裹的所有记录
     * @param barCode
     * @param dmsCode
     * @return
     */
    @Override
    public List<DmsOutWeightAndVolume> getListByBarCodeAndDms(String barCode,Integer dmsCode){
        DmsOutWeightAndVolume dmsOutWeightAndVolume = new DmsOutWeightAndVolume();
        dmsOutWeightAndVolume.setBarCode(barCode);
        dmsOutWeightAndVolume.setCreateSiteCode(dmsCode);

        List<DmsOutWeightAndVolume> weightAndVolumeList = dmsOutWeightAndVolumeDao.queryByBarCode(dmsOutWeightAndVolume);
        if(weightAndVolumeList == null || weightAndVolumeList.size() < 1){
            return new ArrayList<DmsOutWeightAndVolume>();
        }
        return weightAndVolumeList;
    }

    /**
     * 查询这个分拣中心对该箱号/包裹的一条记录
     * @param barCode
     * @param dmsCode
     * @return
     */
    @Override
    public DmsOutWeightAndVolume getOneByBarCodeAndDms(String barCode,Integer dmsCode){
        DmsOutWeightAndVolume dmsOutWeightAndVolume = new DmsOutWeightAndVolume();
        dmsOutWeightAndVolume.setBarCode(barCode);
        dmsOutWeightAndVolume.setCreateSiteCode(dmsCode);

        return dmsOutWeightAndVolumeDao.queryOneByBarCode(dmsOutWeightAndVolume);
    }

    @Override
    public List<DmsOutWeightAndVolume> getListByBarCodesAndDms(List<String> barCodeList, Integer createSiteCode) {
        return dmsOutWeightAndVolumeDao.queryListByBarCodes(barCodeList, createSiteCode);
    }

    /**
     * 将数据置成无效is_delete=1
     * @param barCode
     * @param dmsCode
     */
    @Override
    public void deleteByBarCodeAndDms(String barCode,Integer dmsCode){
        DmsOutWeightAndVolume dmsOutWeightAndVolume = new DmsOutWeightAndVolume();
        dmsOutWeightAndVolume.setBarCode(barCode);
        dmsOutWeightAndVolume.setCreateSiteCode(dmsCode);
        dmsOutWeightAndVolume.setIsDelete(1);

        dmsOutWeightAndVolumeDao.update(dmsOutWeightAndVolume);
    }

}

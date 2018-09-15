package com.jd.bluedragon.distribution.weightAndMeasure.service;

import com.jd.bluedragon.distribution.weightAndMeasure.dao.DmsOutWeightAndVolumeDao;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("dmsOutWeightAndVolumeService")
public class DmsOutWeightAndVolumeServiceImpl implements DmsOutWeightAndVolumeService{

    @Autowired
    DmsOutWeightAndVolumeDao dmsOutWeightAndVolumeDao;

    /**
     * 保存分拣应付重量体积信息
     * @param dmsOutWeightAndVolume
     * @return
     */
    public void saveOrUpdate(DmsOutWeightAndVolume dmsOutWeightAndVolume){
        List<DmsOutWeightAndVolume> weightAndVolumeList = dmsOutWeightAndVolumeDao.queryByBarCode(dmsOutWeightAndVolume);

        if(weightAndVolumeList == null || weightAndVolumeList.size()<1){
            dmsOutWeightAndVolumeDao.add(dmsOutWeightAndVolume);
        }else{
            dmsOutWeightAndVolume.setCreateTime(null);
            dmsOutWeightAndVolumeDao.update(dmsOutWeightAndVolume);
        }
    }

    /**
     * 查询这个分拣中心对该箱号/包裹的所有记录
     * @param barCode
     * @param dmsCode
     * @return
     */
    public List<DmsOutWeightAndVolume> getListByBarCodeAndDms(String barCode,Integer dmsCode){
        DmsOutWeightAndVolume dmsOutWeightAndVolume = new DmsOutWeightAndVolume();
        dmsOutWeightAndVolume.setBarCode(barCode);
        dmsOutWeightAndVolume.setCreateSiteCode(dmsCode);

        List<DmsOutWeightAndVolume> weightAndVolumeList = dmsOutWeightAndVolumeDao.queryByBarCode(dmsOutWeightAndVolume);
        if(weightAndVolumeList == null && weightAndVolumeList.size() < 1){
            return null;
        }
        return weightAndVolumeList;
    }

    /**
     * 查询这个分拣中心对该箱号/包裹的一条记录
     * @param barCode
     * @param dmsCode
     * @return
     */
    public DmsOutWeightAndVolume getOneByBarCodeAndDms(String barCode,Integer dmsCode){
        DmsOutWeightAndVolume dmsOutWeightAndVolume = new DmsOutWeightAndVolume();
        dmsOutWeightAndVolume.setBarCode(barCode);
        dmsOutWeightAndVolume.setCreateSiteCode(dmsCode);

        return dmsOutWeightAndVolumeDao.queryOneByBarCode(dmsOutWeightAndVolume);
    }

}

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
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("barCode",dmsOutWeightAndVolume.getBarCode());
        param.put("createSiteCode",dmsOutWeightAndVolume.getCreateSiteCode());
        List<DmsOutWeightAndVolume> weightAndVolumeList = dmsOutWeightAndVolumeDao.queryByBarCode(param);

        if(weightAndVolumeList == null || weightAndVolumeList.size()<1){
            dmsOutWeightAndVolumeDao.add(dmsOutWeightAndVolume);
        }else{
            dmsOutWeightAndVolumeDao.update(dmsOutWeightAndVolume);
        }
    }

    /**
     * 查询这个分拣中心对该箱号/包裹的查询记录
     * @param barCode
     * @param dmsCode
     * @return
     */
    public DmsOutWeightAndVolume getByBarCodeAndDms(String barCode,Integer dmsCode){
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("barCode",barCode);
        param.put("createSiteCode",dmsCode);
        List<DmsOutWeightAndVolume> weightAndVolumeList = dmsOutWeightAndVolumeDao.queryByBarCode(param);
        if(weightAndVolumeList == null && weightAndVolumeList.size() < 1){
            return null;
        }
        return weightAndVolumeList.get(0);
    }

}

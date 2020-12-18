package com.jd.bluedragon.distribution.weightAndMeasure.service;

import com.jd.bluedragon.distribution.weightAndMeasure.dao.DmsOutWeightAndVolumeDao;
import com.jd.bluedragon.distribution.weightAndMeasure.domain.DmsOutWeightAndVolume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("dmsOutWeightAndVolumeService")
public class DmsOutWeightAndVolumeServiceImpl implements DmsOutWeightAndVolumeService{

    @Autowired
    DmsOutWeightAndVolumeDao dmsOutWeightAndVolumeDao;

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

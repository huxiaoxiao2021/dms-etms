package com.jd.bluedragon.distribution.crossbox.dao;

import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dudong on 2016/9/1.
 */
public class CrossBoxDaoTest extends AbstractDaoIntegrationTest{
    @Autowired
    private CrossBoxDao crossBoxDao;

    @Test
    public void testAddCrossBox() {
        CrossBox crossBox = new CrossBox();
        crossBox.setOriginalDmsId(1);
        crossBox.setOriginalDmsName("zhangs");
        crossBox.setCreateTime(new Date());
        crossBox.setDestinationDmsId(2);
        crossBox.setDestinationDmsName("lisi");
        crossBoxDao.addCrossBox(crossBox);
    }


    @Test
    public void testUpdateYnById(){
        CrossBox crossBox = new CrossBox();
        crossBox.setId(1);
        crossBoxDao.updateYnCrossBoxById(crossBox);
    }


    @Test
    public void testSelectActiveCrossByDmsId() {
        CrossBox crossBox = new CrossBox();
        crossBox.setOriginalDmsId(1);
        crossBox.setDestinationDmsId(2);
        crossBoxDao.selectActiveCrossBoxByDmsId(crossBox);
    }


    @Test
    public void testCheckLineExist() {
        CrossBox crossBox = new CrossBox();
        crossBox.setOriginalDmsId(1);
        crossBox.setDestinationDmsId(2);
        crossBoxDao.checkLineExist(crossBox);
    }


    @Test
    public void testQueryByCondition() {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("startIndex", new Integer(1));
        param.put("pageSize", new Integer(10));
        crossBoxDao.queryByCondition(param);

    }


    @Test
    public void testGetCrossBoxById() {
        crossBoxDao.getCrossBoxById(1);
    }


    @Test
    public void testUpdateCrossBoxByDms() {
        CrossBox crossBox = new CrossBox();
        crossBox.setOriginalDmsId(1);
        crossBox.setDestinationDmsId(2);
        crossBox.setTransferOneId(1);
        crossBox.setTransferOneName("skas");
        crossBoxDao.updateCrossBoxByDms(crossBox);
    }


    @Test
    public void testGetFullLineByDmsId() {
        CrossBox crossBox = new CrossBox();
        crossBox.setOriginalDmsId(1);
        crossBox.setDestinationDmsId(2);
        crossBoxDao.getFullLineByDmsId(crossBox);
    }

}



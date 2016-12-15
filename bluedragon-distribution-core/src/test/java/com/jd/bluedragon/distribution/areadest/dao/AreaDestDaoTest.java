package com.jd.bluedragon.distribution.areadest.dao;

import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lixin39 on 2016/12/8.
 */
public class AreaDestDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private AreaDestDao areaDestDao;

    @Test
    public void testAdd() {
        try {
            AreaDest areaDest = new AreaDest();
            areaDest.setCreateSiteCode(610);
            areaDest.setCreateSiteName("通州分拣中心");
            areaDest.setTransferSiteCode(910);
            areaDest.setTransferSiteName("马驹桥分拣中心");
            areaDest.setReceiveSiteCode(101010);
            areaDest.setReceiveSiteName("回龙观自提点");
            areaDest.setCreateUser("TEST");
            areaDest.setCreateUserCode(1001);
            areaDestDao.add(areaDest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddBatch() {
        List<AreaDest> list = new ArrayList<AreaDest>();

        for (int i = 0; i < 100; i++) {
            AreaDest areaDest = new AreaDest();
            areaDest.setCreateSiteCode(610 + i);
            areaDest.setCreateSiteName("通州分拣中心");
            areaDest.setTransferSiteCode(910 + i);
            areaDest.setTransferSiteName("马驹桥分拣中心");
            areaDest.setReceiveSiteCode(101010 + i);
            areaDest.setReceiveSiteName("回龙观自提点");
            areaDest.setCreateUser("TEST");
            areaDest.setCreateUserCode(1001 + i);
            list.add(areaDest);
        }
        System.out.println(areaDestDao.addBatch(list));
    }

    @Test
    public void testGetList() {
        Integer createSiteCode = 610;
        Integer transferSiteCode = 910;
        Integer receiverSiteCode = 101010;
        Map<String, Object> params = new HashMap<String, Object>();
        //params.put("createSiteCode", createSiteCode);
        params.put("transferSiteCode", transferSiteCode);
        //params.put("receiverSiteCode", receiverSiteCode);
        List<AreaDest> list = areaDestDao.getList(params);
        for (AreaDest area : list) {
            System.out.println(area.toString());
        }
    }

    @Test
    public void testDisableByParams() {
        Integer createSiteCode = 610;
        Integer transferSiteCode = 910;
        Integer receiverSiteCode = 101010;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("createSiteCode", createSiteCode);
        params.put("transferSiteCode", transferSiteCode);
        params.put("receiverSiteCode", receiverSiteCode);
        params.put("updateUser", "");
        areaDestDao.disableByParams(params);
    }
}

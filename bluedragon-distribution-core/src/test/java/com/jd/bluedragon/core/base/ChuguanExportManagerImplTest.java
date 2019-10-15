package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.dao.common.SpringAppContextConfigPath;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.stock.iwms.export.param.ChuguanParam;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/10/15
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = SpringAppContextConfigPath.ORACLE_APP_CONTEXT_PATH)
public class ChuguanExportManagerImplTest {

    @Autowired
    private ChuguanExportManager chuguanExportManager;

    @Test
    public void testinsertChuguan(){
        List<ChuguanParam> chuguanParamList = Lists.newArrayList();
        long result = chuguanExportManager.insertChuguan(chuguanParamList);
        Assert.assertEquals(result,1);
    }

    @Test
    public void testqueryByWaybillCode(){
        KuGuanDomain result = chuguanExportManager.queryByWaybillCode("");
        Assert.assertNotNull(result);
    }

}

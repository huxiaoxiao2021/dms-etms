package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author : xumigen
 * @date : 2019/9/21
 */
public class DmsBaseDictServiceImplTest extends AbstractDaoIntegrationTest {

    @Autowired
    private DmsBaseDictService dmsBaseDictService;

    @Test
    public void testqueryMapKeyTypeCodeByParentId(){
        Map<Integer,String> map = dmsBaseDictService.queryMapKeyTypeCodeByTypeCode(Constants.BASEDICT_GOODS_TYPE_TYPECODE);
        Assert.assertTrue(map.size() == 3);
    }
}

package com.jd.bluedragon.core.jsf.dms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.blocker.dto.CommonDto;
import com.jd.etms.blocker.dto.ExceptionOrderDto;
import com.jd.etms.blocker.dto.ExceptionOrderQueryDto;
import com.jd.etms.blocker.webservice.BlockerQueryWS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/3/26 11:21
 */
@RunWith(MockitoJUnitRunner.class)
public class BlockerQueryWSJsfManagerImplTest {

    @InjectMocks
    private com.jd.bluedragon.core.jsf.dms.impl.BlockerQueryWSJsfMangerImpl BlockerQueryWSJsfMangerImpl;

    @Mock
    BlockerQueryWS blockerQueryWS;

    @Before
    public  void  before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test1(){
        ReflectionTestUtils.setField(BlockerQueryWSJsfMangerImpl,"blockerQueryWS",blockerQueryWS);

        //结果1
        CommonDto<List<ExceptionOrderDto>> result1  = null;

        //结果2
        CommonDto<List<ExceptionOrderDto>> result2 = new CommonDto<>();
        result2.setCode(CommonDto.CODE_SUCCESS);
        List<ExceptionOrderDto> list = new ArrayList<>();
        list.add(new ExceptionOrderDto());
        result2.setData(list);
        Mockito.when(blockerQueryWS.queryExceptionOrders(Mockito.<ExceptionOrderQueryDto>any())).thenReturn(result2);

        String  waybillCode = "JDVA00003243169";
        JdCResponse jdCResponse =  BlockerQueryWSJsfMangerImpl.queryExceptionOrders(waybillCode);
        System.out.println(JsonHelper.toJsonMs(jdCResponse));
    }
}
    

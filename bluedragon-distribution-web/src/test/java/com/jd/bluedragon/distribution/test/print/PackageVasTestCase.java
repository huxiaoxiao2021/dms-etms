package com.jd.bluedragon.distribution.test.print;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillQueryManagerImpl;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.GoodsDto;
import com.jd.etms.waybill.dto.GoodsQueryDto;
import com.jd.etms.waybill.dto.WaybillPickupVasDto;
import junit.framework.Assert;
import org.apache.avro.generic.GenericData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

//包裹增值服务mock测试
@RunWith(MockitoJUnitRunner.class)
public class PackageVasTestCase
{
    @Mock
    WaybillQueryApi waybillQueryApi;

    @InjectMocks
    WaybillQueryManagerImpl waybillQueryManager;

    @Test
    public void testPackageSpecialRequirement() throws Exception{

        BaseEntity<List<GoodsDto>> baseEntity = new BaseEntity<>();
        List<GoodsDto> goodsDtoList = new ArrayList<>();
        GoodsDto goodsDto = new GoodsDto();
        goodsDto.setPackBarcode("JDV000503537329-2-5-");
        List<WaybillPickupVasDto> waybillPickupVasDtoList = new ArrayList<>();
        WaybillPickupVasDto waybillPickupVasDto = new WaybillPickupVasDto();
        //waybillPickupVasDto.setPrimaryParam("上门安装");
        WaybillPickupVasDto waybillPickupVasDto1 = new WaybillPickupVasDto();
        //waybillPickupVasDto1.setPrimaryParam("轻拿轻放");
        waybillPickupVasDtoList.add(waybillPickupVasDto);
        waybillPickupVasDtoList.add(waybillPickupVasDto1);
        goodsDto.setWaybillPickupVasDtoList(waybillPickupVasDtoList);
        goodsDto.setWaybillCode("JDV000503537329");


        GoodsDto goodsDto1 = new GoodsDto();
        goodsDto1.setPackBarcode("JDV000503537329-1-5-");
        List<WaybillPickupVasDto> waybillPickupVasDtoList1 = new ArrayList<>();
        WaybillPickupVasDto waybillPickupVasDto20 = new WaybillPickupVasDto();
        waybillPickupVasDto20.setPrimaryParam("不知道 随便加");
        WaybillPickupVasDto waybillPickupVasDto21 = new WaybillPickupVasDto();
        waybillPickupVasDto21.setPrimaryParam("增值服务");
        waybillPickupVasDtoList1.add(waybillPickupVasDto20);
        waybillPickupVasDtoList1.add(waybillPickupVasDto21);
        goodsDto1.setWaybillPickupVasDtoList(waybillPickupVasDtoList1);
        goodsDto1.setWaybillCode("JDV000503537329");



        goodsDtoList.add(goodsDto);
        goodsDtoList.add(goodsDto1);
        baseEntity.setData(goodsDtoList);

        Mockito.when(waybillQueryApi.queryGoodsDataByWCode(any(GoodsQueryDto.class)))
                .thenReturn(baseEntity);

        /*Mockito.when(waybillQueryApi.queryGoodsDataByWCode(any(GoodsQueryDto.class)))
                .thenThrow(new RuntimeException("db操作异常"));*/

        PrintPackage pack = new PrintPackage();

        String s1 = null;
        Map<String,String> map1 = waybillQueryManager.doGetPackageVasInfo("JDV000503537329");
        System.out.println(JsonHelper.toJson(map1));
        /*System.out.println("return1"+return1);
        pack.setPackageSpecialRequirement(return1);
        System.out.println("pack输出"+JsonHelper.toJson(pack));
        Assert.assertEquals(true,return1 == null);*/

        String s2="";
        Map<String,String> map2= waybillQueryManager.doGetPackageVasInfo("JDV000503537329");
        /*System.out.println("return2"+return2);
        Assert.assertEquals(true,return2 == null);*/
    }

}

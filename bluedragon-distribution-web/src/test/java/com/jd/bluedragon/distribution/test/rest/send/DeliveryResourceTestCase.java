package com.jd.bluedragon.distribution.test.rest.send;

import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.send.domain.SendThreeDetail;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.test.AbstractTestCase;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;

/**
 * Created by wangtingwei on 2015/3/6.
 */
public class DeliveryResourceTestCase extends AbstractTestCase {

    @Autowired
    private DeliveryResource deliveryResource;

    @Test
    public void testCheckThreeDelivery() throws Exception{
        PrintStream myout = new PrintStream(new FileOutputStream(new File("D:/发货验证日志.txt")));
        System.setOut(myout);
        String[] boxCodes1={"BC010F001010A00100006007","BC010F001010A00100006006","BC010F001010A00100006008","BC010F001010A00100006009"};
        testCheckThreeDelivery(10,1006,1,"1006-1-201208010817170",boxCodes1);

        String[] boxCodes2={"170003972N2S3H11","170003972N1S3H11","170003972N3S3H11"};
        testCheckThreeDelivery(10,1006,1,"1006-1-20121112142723594",boxCodes2);

        String[] boxCodes3={"170003972N2S3H11","170003972N1S3H11"};
        testCheckThreeDelivery(10,1006,1,"1006-1-20121112142723594",boxCodes3);

        String[] boxCodes4={"303417797-1-3-","303417797-3-3-","303417797-2-3-"};
        testCheckThreeDelivery(10,1006,1,"1006-1-20121112161848466",boxCodes4);

        String[] boxCodes5={"32985988313-565-600-","303417797-3-3-","303417797-2-3-"};
        testCheckThreeDelivery(10,511,910,"511-910-201411081704480",boxCodes5);

        String[] boxCodes6={"303417797-1-3000-","303417797-3-3000-","303417797-2-3000-"};
        testCheckThreeDelivery(10,1006,1,"1006-1-20121112161848466",boxCodes6);

        String[] boxCodes7={"3034FDSFS00-","303417797-3-88-","30343231999-2-3-PPPP"};
        testCheckThreeDelivery(10,1006,1,"1006-1-20121112161848466",boxCodes7);

        String[] boxCodes8={"DFSADFSAAFD中国AFDS9998","TAFA本东ADFD-DAFD","3TTA&^!@#$%^&*()FDP"};
        testCheckThreeDelivery(10,1006,1,"1006-1-20121112161848466",boxCodes8);

        String[] boxCodes9={null,"fasdfads",""};
        testCheckThreeDelivery(10,1006,1,"1006-1-20121112161848466",boxCodes9);

        FileReader reader=new FileReader("D:/发货验证JSON文件1.txt");
        BufferedReader br=new BufferedReader(reader);
        StringBuilder sb=new StringBuilder();
        String line=null;
        while(null!=( line=br.readLine())){
            sb.append(line);
        }
        bigJsonCheck(sb.toString());


       System.setOut(System.out);
    }

    private void testCheckThreeDelivery(int businessType,int createSiteCode,int receiveSiteCode,String sendCode,String[] boxCodes) throws Exception{
        List<DeliveryRequest> list=new ArrayList<DeliveryRequest>();
        for (String item :boxCodes) {
            DeliveryRequest request = new DeliveryRequest();
            request.setSiteCode(createSiteCode);
            request.setBusinessType(businessType);
            request.setReceiveSiteCode(receiveSiteCode);
            request.setSendCode(sendCode);
            request.setBoxCode(item);
            list.add(request);
        }
        ThreeDeliveryResponse result= deliveryResource.checkThreeDelivery(list);
        System.out.println("------------------start-------invoke DeliveryResource.checkThreeDelivery-----------");
        System.out.println("发货类型：[" + businessType + "],创建站点：[" + createSiteCode + "],收货站点：[" + receiveSiteCode + "],发货批次号：[" + sendCode + "],箱号集合：[" + arrayToString(boxCodes) + "]");
        System.out.println("结果编号：" + result.getCode().toString());
        if(result.getCode().equals(30003)){
            System.out.println("存在不全订单为：");
            for (SendThreeDetail item: result.getData()){
                System.out.println(item.getPackageBarcode() + "【" + item.getMark()+"】");
            }
        }

        Assert.assertFalse("发货验证一单多件不齐", result.getCode().equals(500));
        System.out.println("--------------------end--------------------");
    }

    private String arrayToString(String[] array){
        StringBuilder sb=new StringBuilder();
        for (String item:array){
            sb.append(item+",");
        }
        return sb.toString().substring(0,sb.toString().length()-1);
    }

    private void bigJsonCheck(String json){
        DeliveryRequest[] array= JsonHelper.jsonToArray(json,DeliveryRequest[].class);
        List<DeliveryRequest> list=Arrays.asList(array);
        ThreeDeliveryResponse result= deliveryResource.checkThreeDelivery(list);
        System.out.println("------------------start-------invoke DeliveryResource.checkThreeDelivery-----------");
        System.out.println("发货类型：[" +list.get(0).getBusinessType() + "],创建站点：[" + list.get(0).getSiteCode()
                + "],收货站点：[" + list.get(0).getReceiveSiteCode() + "],发货批次号：[" + list.get(0).getSendCode() + "],箱号集合：[") ;
        for (DeliveryRequest item:list){
            System.out.println(item);
        }
        System.out.println("]");
        System.out.println("结果编号：" + result.getCode().toString());
        if(result.getCode().equals(30003)){
            System.out.println("存在不全订单为：");
            for (SendThreeDetail item: result.getData()){
                System.out.println(item.getPackageBarcode() + "【" + item.getMark()+"】");
            }
        }

        Assert.assertFalse("发货验证一单多件不齐", result.getCode().equals(500));
        System.out.println("--------------------end--------------------");
    }
}

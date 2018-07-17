package com.jd.bluedragon.utils;

import com.jd.bluedragon.distribution.send.domain.ShouHuoConverter;
import com.jd.bluedragon.distribution.send.domain.ShouHuoInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * XML工具类测试用例
 */
public class XmlHelperTest {

    public static void main(String[] args) {

        ShouHuoInfo tShouHuoInfo = new ShouHuoInfo();
        tShouHuoInfo.setCarNo("2");
        String result =XmlHelper
                .objectToXml(tShouHuoInfo, new ShouHuoConverter());

        System.out.println(result);
    }
}

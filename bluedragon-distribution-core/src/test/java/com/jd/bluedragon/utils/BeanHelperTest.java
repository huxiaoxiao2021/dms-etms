package com.jd.bluedragon.utils;

import com.jd.bluedragon.distribution.api.request.RejectRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;
import org.junit.Test;

import java.util.Date;

/**
 * <p>
 * Created by lixin39 on 2018/10/8.
 */
public class BeanHelperTest {

    @Test
    public void testCopyProperties(){
        ReverseReject source = new ReverseReject();
        source.setOperateTime(new Date());
//        RejectRequest source = new RejectRequest();
//        source.setOperateTime("2018-09-30 01:30:00");

        ReverseReject target = new ReverseReject();
        BeanHelper.copyProperties(target, source);
        System.out.println(target.getOperateTime().toString());
    }

}

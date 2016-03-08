package com.jd.bluedragon.configuration.ucc;


import com.jd.std.ucc.client.client.ConfClientFactory;

/**
 * Created by wangtingwei on 2016/2/19.
 */
public class AbstractConfigure {
   public void getInstance(){
      ConfClientFactory.getConfClient();
       /*
       <dependency>
            <groupId>com.jd.std.ucc</groupId>
            <artifactId>confcenter-client</artifactId>
            <version>1.1.2-SNAPSHOT</version>
        </dependency>
        */
   }
}

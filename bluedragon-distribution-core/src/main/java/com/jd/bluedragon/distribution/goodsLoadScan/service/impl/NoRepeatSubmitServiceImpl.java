package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.goodsLoadScan.service.NoRepeatSubmitService;
import com.jd.jim.cli.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

public class NoRepeatSubmitServiceImpl implements NoRepeatSubmitService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String repeatValue="1";

    //防重复提交时间
    private Long lockTime = 60L * 1000;

    /**
     * 重复提交script
     * keys 提交uuid 主键
     * argv 超时时间
     * key不存在，设置超时时间，则返回可以提交
     * key存在，重复提交
     * 返回 1：可以提交
     *     0：重复提交
     */
    public static String repeatSubmit = "local lockSet = redis.call('setnx', KEYS[1], ARGV[1])\n"
            +"if lockSet == 1 then\n"
            +"redis.call('pexpire', KEYS[1], ARGV[2]);\n"
            +"return 1 ;\n"
            +"else \n"
            +"return 0;\n"
            +"end;\n";
    @Resource
    private final Cluster jimClient;

    public NoRepeatSubmitServiceImpl(Cluster jimClient) {
        this.jimClient = jimClient;
    }


    @Override
    public Long checkRepeatSubmit(Long taskId) {

        List<String> keysList = Arrays.asList(taskId.toString());
        List<String> argsList = Arrays.asList(repeatValue,String.valueOf(lockTime));

        String sha = jimClient.scriptLoad(this.repeatSubmit);
        /**
         * execute lua script
         */
        Object result = jimClient.evalsha(sha,keysList,argsList,false);

        log.info("execute result is:{}", JSON.toJSONString(result));
        return (Long)result;
    }
}

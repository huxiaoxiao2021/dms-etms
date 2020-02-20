package com.jd.bluedragon.distribution.log;

import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class BusinessLogProfilerBuilder {

    private static final int SOURCESYS=112;//实操日志在businesslog对应的sourcesys

    BusinessLogProfiler businessLogProfiler;

    public BusinessLogProfilerBuilder() {
        this.businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setLogType("developer");
        this.businessLogProfiler.setSourceSys(SOURCESYS);
        try {
            businessLogProfiler.setServerIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            businessLogProfiler.setServerIp("");
        }
        this.businessLogProfiler.setTimeStamp(new Date().getTime());
    }

    public BusinessLogProfilerBuilder BusinessLogProfilerBuilder(String waybillCode) {
        this.businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setLogType("developer");
        this.businessLogProfiler.setSourceSys(112);//实操日志
        try {
            businessLogProfiler.setServerIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            businessLogProfiler.setServerIp("");
        }
        this.businessLogProfiler.setTimeStamp(new Date().getTime());
        return this;
    }


    public BusinessLogProfilerBuilder bizType(int biztype) {
        this.businessLogProfiler.setBizType(biztype);
        return this;
    }

    public BusinessLogProfilerBuilder operateType(int operatetype) {
        this.businessLogProfiler.setOperateType(operatetype);
        return this;
    }

    public BusinessLogProfilerBuilder methodName(String methodName) {
        this.businessLogProfiler.setMethodName(methodName);
        return this;
    }

    public BusinessLogProfilerBuilder operateResponse(Object reponse) {
        this.businessLogProfiler.setOperateResponse(JSONObject.toJSONString(reponse,true));
        return this;
    }

    public BusinessLogProfilerBuilder operateRequest(Object request) {
        this.businessLogProfiler.setOperateRequest(JSONObject.toJSONString(request));
        return this;
    }

    public BusinessLogProfilerBuilder url(String url) {
        this.businessLogProfiler.setUrl(url);
        return this;
    }

    public BusinessLogProfilerBuilder reMark(String remark) {
        this.businessLogProfiler.setRemark(remark);
        return this;
    }

    public BusinessLogProfilerBuilder processTime(long endTime, long startTime) {
        this.businessLogProfiler.setProcessTime(endTime - startTime);
        this.businessLogProfiler.setTimeStamp(endTime);
        return this;
    }

    public BusinessLogProfilerBuilder timeStamp(long time) {
        this.businessLogProfiler.setTimeStamp(time);
        return this;
    }

    public BusinessLogProfiler build() {
        return this.businessLogProfiler;
    }


}

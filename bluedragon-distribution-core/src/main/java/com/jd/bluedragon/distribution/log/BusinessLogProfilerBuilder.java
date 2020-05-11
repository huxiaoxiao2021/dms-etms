package com.jd.bluedragon.distribution.log;

import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 创建 BusinessLogProfiler
 */
public class BusinessLogProfilerBuilder {

    private BusinessLogProfiler businessLogProfiler;

    public BusinessLogProfilerBuilder() {
        this.businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setLogType("developer");
        businessLogProfiler.setRequestTime(System.currentTimeMillis());
        this.businessLogProfiler.setSourceSys(BusinessLogConstans.SourceSys.DMS_OPERATE.getCode());//默认是 实操日志
        try {
            businessLogProfiler.setServerIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            businessLogProfiler.setServerIp("");
        }
        this.businessLogProfiler.setTimeStamp(System.currentTimeMillis());
    }

    public BusinessLogProfilerBuilder(BusinessLogConstans.SourceSys sourceSys) {
        this.businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setLogType("developer");
        this.businessLogProfiler.setSourceSys(sourceSys.getCode());
        try {
            businessLogProfiler.setServerIp(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            businessLogProfiler.setServerIp("");
        }
        this.businessLogProfiler.setTimeStamp(System.currentTimeMillis());
    }

    public BusinessLogProfilerBuilder operateTypeEnum(BusinessLogConstans.OperateTypeEnum operateTypeEnum){
        this.businessLogProfiler.setSourceSys(operateTypeEnum.getSourceSysCode());
        this.businessLogProfiler.setBizType(operateTypeEnum.getBizTypeCode());
        this.businessLogProfiler.setOperateType(operateTypeEnum.getCode());
        return this;
    }

    public BusinessLogProfilerBuilder bizType(BusinessLogConstans.OperateTypeEnum operateTypeEnum) {
        this.businessLogProfiler.setBizType(operateTypeEnum.getBizTypeCode());
        return this;
    }

    public BusinessLogProfilerBuilder operateType(BusinessLogConstans.OperateTypeEnum operateTypeEnum) {
        this.businessLogProfiler.setOperateType(operateTypeEnum.getCode());
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

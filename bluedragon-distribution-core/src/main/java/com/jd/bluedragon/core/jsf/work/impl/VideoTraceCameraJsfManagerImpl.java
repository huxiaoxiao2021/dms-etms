package com.jd.bluedragon.core.jsf.work.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.work.VideoTraceCameraJsfManager;
import com.jd.etms.framework.utils.JsonUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.videoTraceCamera.VideoTraceCameraConfig;
import com.jdl.basic.api.domain.videoTraceCamera.VideoTraceCameraQuery;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jdl.basic.api.service.videoTraceCamera.VideoTraceCameraJsfService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("videoTraceCameraJsfManager")
public class VideoTraceCameraJsfManagerImpl implements VideoTraceCameraJsfManager {
    @Autowired
    private VideoTraceCameraJsfService videoTraceCameraJsfService;

    /**
     * 查询摄像头绑定的网格业务主键
     * 如果非空第一个为摄像头作为主摄像头绑定的网格
     * @param deviceNo
     * @param nationalChannelCode
     * @param createTime 格式 "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "VideoTraceCameraJsfManagerImpl.queryVideoTraceCameraConfig",
            mState={JProEnum.TP,JProEnum.FunctionError})
    public List<String> queryVideoTraceCameraConfig(String deviceNo, String nationalChannelCode,
                                                    String createTime, Integer status, Integer siteCode){
        //查询参数
        VideoTraceCameraQuery videoTraceCameraQuery = new VideoTraceCameraQuery();
        videoTraceCameraQuery.setCameraCode(deviceNo);
        videoTraceCameraQuery.setNationalChannelCode(nationalChannelCode);
        videoTraceCameraQuery.setOperateTimeStr(createTime);
        videoTraceCameraQuery.setSiteCode(siteCode);
        //不指定查询所有状态
        videoTraceCameraQuery.setStatus(status);
        Result<List<VideoTraceCameraConfig>> result = videoTraceCameraJsfService.queryVideoTraceCameraConfig(videoTraceCameraQuery);
        log.info("查询摄像头绑定的网格信息查询，deviceNo:{},nationalChannelCode:{},createTime:{},result:{}", deviceNo, 
                nationalChannelCode, createTime, JsonUtils.beanToJson(result));
        
        List<VideoTraceCameraConfig> configs = null;
        List<String> workGridKeys = new ArrayList<>();
        if(result != null && CollectionUtils.isNotEmpty(configs = result.getData())){
            String masterGridKey = getMasterGridKey(configs);
            if(StringUtils.isNotBlank(masterGridKey)){
                workGridKeys.add(masterGridKey);
            }
            for(VideoTraceCameraConfig config : configs){
                if(!workGridKeys.contains(config.getRefWorkGridKey())){
                    workGridKeys.add(config.getRefWorkGridKey());
                }
            }
        }
        log.info("查询摄像头绑定的网格信息查询，deviceNo:{},nationalChannelCode:{},createTime:{},workGridKeys:{}", deviceNo,
                nationalChannelCode, createTime, String.join(",", workGridKeys));
        return workGridKeys;
    }

    /**
     * 获取主相同对应网格，如果未配置取最早的配置
     * @param configs
     * @return
     */
    private String getMasterGridKey(List<VideoTraceCameraConfig> configs){
        //主摄像头
        Byte masterCamera =  1;
        //主摄像头对应网格
        String masterGridKey = configs.stream().filter(c -> masterCamera.equals(c.getMasterCamera()))
                .map(VideoTraceCameraConfig::getRefWorkGridKey).findFirst().orElse(null);
        //创建时间最小
        if(StringUtils.isBlank(masterGridKey)){
            VideoTraceCameraConfig minCreateTimeConfig = configs.stream().min(Comparator.comparing(VideoTraceCameraConfig::getCreateTime))
                    .orElse(null);
            if(minCreateTimeConfig != null){
                masterGridKey = minCreateTimeConfig.getRefWorkGridKey();
            }
        }
        return masterGridKey;
    }
    
    
}

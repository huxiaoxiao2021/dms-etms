package com.jd.bluedragon.distribution.weightAndVolumeCheck.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.video.request.VideoUploadRequest;
import com.jd.bluedragon.common.dto.video.response.VideoUploadInfo;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.*;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckOfB2bService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.service.VideoServiceManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 10:24
 */
@Controller
@RequestMapping("weightAndVolumeCheckOfB2b")
public class WeightAndVolumeCheckOfB2bController extends DmsBaseController {

    private static final Logger logger = LoggerFactory.getLogger(WeightAndVolumeCheckOfB2bController.class);

    private static final String SPOT_CHECK_VIDEO_PREFIX = "spot:check:video:";
    private static final long SPOT_CHECK_VIDEO_TIMEOUT = 24L;

    @Autowired
    private WeightAndVolumeCheckOfB2bService weightAndVolumeCheckOfB2bService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private VideoServiceManager videoServiceManager;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    /**
     * 对接视频中台的应用ID
     */
    @Value("${jsf.vodService.appId}")
    private Integer appId;

    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){
        Integer createSiteCode = -1;
        LoginUser loginUser = getLoginUser();
        String loginErp = null;
        if(loginUser != null){
            createSiteCode = loginUser.getSiteCode();
            loginErp = loginUser.getUserErp();
        }
        model.addAttribute("createSiteCode",createSiteCode);
        model.addAttribute("loginErp",loginErp);
        return "/weightAndVolumeCheck/weightAndVolumeCheckOfB2b";
    }

    /**
     * 初始化运单校验页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult<List<WeightVolumeCheckOfB2bPackage>> init(){
        InvokeResult<List<WeightVolumeCheckOfB2bPackage>> result = new InvokeResult<>();
        List<WeightVolumeCheckOfB2bPackage> list = new ArrayList<>();
        result.setData(list);
        return result;
    }

    /**
     * 获取运单信息
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/getWaybillInfo")
    @ResponseBody
    public InvokeResult<Boolean> getWaybillInfo(@QueryParam("waybillCode") String waybillCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(!WaybillUtil.isWaybillCode(waybillCode) && !WaybillUtil.isPackageCode(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "单号不符合规则!");
            return result;
        }
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(WaybillUtil.getWaybillCode(waybillCode));
        if(waybill == null || waybill.getGoodNumber() == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "此单无运单数据，请联系'分拣小秘'!");
            return result;
        }
        result.setData(!Objects.equals(waybill.getGoodNumber(), 1));
        return result;
    }

    /**
     * 运单维度校验是否超标
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/checkIsExcessOfWaybill", method = RequestMethod.POST)
    @ResponseBody
    public SpotCheckPagerResult<WeightVolumeCheckOfB2bWaybill> checkIsExcessOfWaybill(@RequestBody WeightVolumeCheckConditionB2b condition){
        SpotCheckPagerResult<WeightVolumeCheckOfB2bWaybill> spotCheckPagerResult = new SpotCheckPagerResult<WeightVolumeCheckOfB2bWaybill>();
        spotCheckPagerResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        if(StringUtils.isEmpty(condition.getWaybillOrPackageCode())){
            // 初始化数据
            spotCheckPagerResult.setTotal(0);
            spotCheckPagerResult.setRows(new ArrayList<WeightVolumeCheckOfB2bWaybill>());
            return spotCheckPagerResult;
        }
        InvokeResult<List<WeightVolumeCheckOfB2bWaybill>> invokeResult = weightAndVolumeCheckOfB2bService.checkIsExcessOfWaybill(condition);
        if(!invokeResult.codeSuccess() || CollectionUtils.isEmpty(invokeResult.getData())){
            spotCheckPagerResult.setCode(invokeResult.getCode());
            spotCheckPagerResult.setMessage(invokeResult.getMessage());
            return spotCheckPagerResult;
        }
        spotCheckPagerResult.setTotal(invokeResult.getData().size());
        spotCheckPagerResult.setRows(invokeResult.getData());
        return spotCheckPagerResult;
    }

    /**
     * 运单维度提交
     * @param param
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/waybillSubmitUrl", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<String> waybillSubmit(@RequestBody WeightVolumeCheckConditionB2b param){
        InvokeResult<String> invokeResult = weightAndVolumeCheckOfB2bService.dealExcessDataOfWaybill(param);
        // 提交成功以后，如果本次提交了视频并且跟redis缓存中的videoId不一致，则需要删除之前的垃圾视频
        if (InvokeResult.RESULT_SUCCESS_CODE == invokeResult.getCode()) {
            deleteTrashVideo(param);
        }
        return invokeResult;
    }

    private void deleteTrashVideo(WeightVolumeCheckConditionB2b request) {
        if (StringUtils.isNotBlank(request.getVideoUrl())) {
            String waybillCode = request.getWaybillOrPackageCode();
            Long videoId = request.getVideoId();
            String cacheVideoId = redisClientOfJy.get(SPOT_CHECK_VIDEO_PREFIX + waybillCode);
            if (cacheVideoId != null && cacheVideoId.contains(Constants.SEPARATOR_COMMA)) {
                String[] videoIdArray = cacheVideoId.split(Constants.SEPARATOR_COMMA);
                logger.info("deleteTrashVideo|controller删除垃圾视频:request={},size={}", JsonHelper.toJson(request), videoIdArray.length);
                for (String videoIdStr : videoIdArray) {
                    if (!videoIdStr.equals(String.valueOf(videoId))) {
                        videoServiceManager.deleteVideo(Long.valueOf(videoIdStr));
                    }
                }
            }
        }
    }

    /**
     * 跳转上传页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping("/toUpload")
    public String toUpload(@QueryParam("waybillOrPackageCode")String waybillOrPackageCode,
                           @QueryParam("createSiteCode")Integer createSiteCode,
                           @QueryParam("weight")Double weight,
                           @QueryParam("excessType")Integer excessType,
                           @QueryParam("isMultiPack")Boolean isMultiPack,
                           Model model) {
        model.addAttribute("waybillOrPackageCode",waybillOrPackageCode);
        model.addAttribute("createSiteCode",createSiteCode);
        model.addAttribute("weight",weight);
        model.addAttribute("excessType",excessType);
        model.addAttribute("isMultiPack",isMultiPack);
        return "/weightAndVolumeCheck/excessPictureUploadOfB2b";
    }

    /**
     * 上传超标图片
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/uploadExcessPicture", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<String> uploadExcessPicture(@RequestParam("image") MultipartFile image,
                                                                                       HttpServletRequest request) {
        return weightAndVolumeCheckOfB2bService.uploadExcessPicture(image,request);
    }

    /**
     * 上传超标视频
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/uploadExcessVideo", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<String> uploadExcessVideo(@RequestParam("video") MultipartFile image,
                                                    HttpServletRequest request) {
        return weightAndVolumeCheckOfB2bService.uploadExcessVideo(image,request);
    }

    /**
     * 获取视频上传信息
     */
    @Authorization(Constants.DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R)
    @RequestMapping(value = "/getVideoUploadUrl", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<VideoUploadInfo> getVideoUploadUrl(VideoUploadRequest request) {
        logger.info("getVideoUploadUrl|controller:request={}", JsonHelper.toJson(request));
        InvokeResult<VideoUploadInfo> invokeResult = new InvokeResult<>();
        // 组装参数
        Map<String, Object> params = new HashMap<>();
        params.put("appid", appId);
        params.put("videoName", request.getVideoName());
        if (StringUtils.isNotBlank(request.getVideoTag())) {
            params.put("videoTag", request.getVideoTag());
        }
        if (StringUtils.isNotBlank(request.getVideoDesc())) {
            params.put("videoDesc", request.getVideoDesc());
        }
        params.put("clientIp", "192.168.1.1");
        params.put("fileSize", request.getFileSize());
        params.put("uploadPin", request.getOperateErp());
        params.put("ipPort", StringUtils.isBlank(request.getIpPort()) ? String.valueOf(Constants.NUMBER_ZERO) : request.getIpPort());
        params.put("uuid", StringUtils.isBlank(request.getDeviceId()) ? UUID.randomUUID().toString() : request.getDeviceId());
        if (StringUtils.isNotBlank(request.getAudioId())) {
            params.put("audioId", request.getAudioId());
        }
        if (StringUtils.isNotBlank(request.getBreakPoint())) {
            params.put("breakpoint", request.getBreakPoint());
        }
        // 调用视频中台接口
        VideoUploadInfo videoUploadInfo = videoServiceManager.getVideoUploadUrl(params);
        if (videoUploadInfo != null) {
            // 为了防止垃圾数据，临时记录本次抽检运单号对应的视频ID一天，待抽检数据提交时便于比较删除
            String cacheVideoId = redisClientOfJy.get(SPOT_CHECK_VIDEO_PREFIX + request.getWaybillCode());
            if (cacheVideoId == null) {
                redisClientOfJy.setEx(SPOT_CHECK_VIDEO_PREFIX + request.getWaybillCode(), String.valueOf(videoUploadInfo.getVideoId()), SPOT_CHECK_VIDEO_TIMEOUT, TimeUnit.HOURS);
            } else {
                cacheVideoId = cacheVideoId + Constants.SEPARATOR_COMMA + videoUploadInfo.getVideoId();
                logger.info("getVideoUploadUrl|controller非首次获取视频上传信息:request={},size={}", JsonHelper.toJson(request), cacheVideoId.split(Constants.SEPARATOR_COMMA).length);
                redisClientOfJy.setEx(SPOT_CHECK_VIDEO_PREFIX + request.getWaybillCode(), cacheVideoId, SPOT_CHECK_VIDEO_TIMEOUT, TimeUnit.HOURS);
            }
            invokeResult.setData(videoUploadInfo);
        } else {
            videoUploadInfo = new VideoUploadInfo();
            videoUploadInfo.setUploadUrl("http://vod-storage-272769.oss.cn-north-1.jcloudcs.com/jdVideo.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20210928T144847Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=2760709056FE0FA54E589B9787384339%2F20210928%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Signature=c98b4056a32fae44c3a624174c2f5351af978d7bd8ab35fb8fcd55157bfddf2b&X-Oss-Callback-ObjectKey=+32lpDoIbe1ZPcIl67apCdCcf35vz8FFMJ2QnJiZK/ZVREgNhJjqNPNQqJlhSMqwG84D2dUzfc94df5RytM5DW2KRMAKC/BJ2PQIvFbWc00%3D");
            videoUploadInfo.setPlayUrl("https://testmvod.300hu.com/3ed99664vodbjngwcloud1/7c78c0ec/208536645492817921/f0.mp4");
            videoUploadInfo.setVideoId(105842921L);
            invokeResult.setData(videoUploadInfo);
        }
        return invokeResult;
    }

}

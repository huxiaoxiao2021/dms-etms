package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.google.common.collect.Lists;
import com.jcloud.jss.JingdongStorageService;
import com.jcloud.jss.client.Request;
import com.jcloud.jss.domain.ObjectListing;
import com.jcloud.jss.domain.ObjectSummary;
import com.jcloud.jss.http.JssInputStreamEntity;
import com.jcloud.jss.service.BucketService;
import com.jcloud.jss.service.ObjectService;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalPictureMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: WeightAndVolumeCheckServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/22 17:48
 */
@Service("weightAndVolumeCheckService")
public class WeightAndVolumeCheckServiceImpl implements WeightAndVolumeCheckService {

    private final Logger logger = Logger.getLogger(this.getClass());

    /** 存储空间文件夹名称 */
    private static final String JSS_BUCKET = "jss.bucket.picture";

    /** 对象存储 **/
    /**外部 访问域名 */
    private static final String STORAGE_DOMAIN_COM = "storage.jd.com";
    /**内部 访问域名 */
    private static final String STORAGE_DOMAIN_LOCAL = "storage.jd.local";

    @Autowired
    @Qualifier("dmsAbnormalInfoMQToPanZe")
    private DefaultJMQProducer dmsAbnormalInfoMQToPanZe;

    @Autowired
    private JingdongStorageService jingdongStorageService;

    /**
     * 上传超标图片
     * @param imageName 文件名
     * @param imageSize 流的大小
     * @param inputStream 上传流
     * @return
     */
    @Override
    public void uploadExcessPicture(String imageName, long imageSize, InputStream inputStream) throws Exception {
        try {
            String value = PropertiesHelper.newInstance().getValue(JSS_BUCKET);
            ObjectService objectService = jingdongStorageService.bucket(value).object(imageName);
            JssInputStreamEntity entity =  new JssInputStreamEntity(inputStream, imageSize);
            Request.Builder builder = (Request.Builder) FieldUtils.readField(objectService, "builder", true);
            builder.entity(entity);
            objectService.put();
            inputStream.close();
            logger.info(MessageFormat.format("上传文件成功imageName:{0},imageSize:{1}", imageName, imageSize));
        }finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException ioe){
                logger.error("关闭输入流再异常：",ioe);
            }
        }
    }

    /**
     * 查看超标图片
     * @param packageCode 包裹号
     * @return
     */
    @Override
    public InvokeResult<String> searchExcessPicture(String packageCode,Integer siteCode) {

        InvokeResult<String> result = new InvokeResult<String>();
        try{
            List<String> urls = new ArrayList<>();
            ObjectListing objectListing = listObject(packageCode, null, 100);
            if(objectListing != null && objectListing.getObjectSummaries() != null &&
                    !objectListing.getObjectSummaries().isEmpty()){
                for(ObjectSummary objectSummary : objectListing.getObjectSummaries()){
                    URI uri = getURI(objectSummary.getKey());
                    if(uri != null){
                        String uriString = uri.toString();
                        //将内部访问域名替换成外部访问域名
                        uriString = uriString.replaceAll(STORAGE_DOMAIN_LOCAL,STORAGE_DOMAIN_COM);
                        uri = URI.create(uriString);
                        if(uri != null){
                            urls.add(uri.toString());
                        }
                    }
                }
            }
            //获取最近的对应的图片并返回
            String excessPictureUrl = getRecentUrl(urls,siteCode);
            if("".equals(excessPictureUrl)){
                result.parameterError("图片未上传!"+packageCode);
                return result;
            }
            result.setData(excessPictureUrl);
        }catch (Exception e){
            logger.error("查看图片失败!"+packageCode);
            result.parameterError("查看图片失败!"+packageCode);
        }
        return result;
    }

    private String getRecentUrl(List<String> urls,Integer siteCode){
        String recentUrl = "";
        try{
            if(urls.size() == 0){
                return recentUrl;
            }else if(urls.size() == 1){
                String[] packageCodeAndOperateTimes = getArrayByUrl(urls.get(0));
                if(packageCodeAndOperateTimes.length == 3){
                    return urls.get(0);
                }
                return recentUrl;
            }else{

                Map<String,Long> map = new HashMap<>();
                for(String url : urls){
                    String[] packageCodeAndOperateTimes = getArrayByUrl(url);
                    String operateTime = "";
                    if(packageCodeAndOperateTimes.length == 3){
                        String siteCodeFromOSS = packageCodeAndOperateTimes[1];
                        if(siteCodeFromOSS.equals(siteCode)){
                            operateTime = packageCodeAndOperateTimes[packageCodeAndOperateTimes.length - 1];
                        }
                    }else{
                        break;
                    }
                    if(!"".equals(operateTime)){
                        long l = Long.parseLong(operateTime);
                        map.put(url,l);
                    }
                }
                Object[] objects = map.values().toArray();
                Arrays.sort(objects);
                for(String url : map.keySet()){
                    if(map.get(url) == objects[objects.length-1]){
                        recentUrl = url;
                        break;
                    }
                }
                return recentUrl;
            }
        }catch (Exception e){
            logger.error("获取图片路径异常!");
            return recentUrl;
        }

    }

    private String[]  getArrayByUrl(String url) {
        String[] splits = url.split("/");
        String pictureName = splits[splits.length - 1];
        String[] pictureNames = pictureName.split("\\.");
        String pictureNamePrefix = pictureNames[0];
        return pictureNamePrefix.split("_");
    }


    /**
     * 上传异常照片的同时给判责发消息
     * @param abnormalPictureMq
     * @param siteCode
     */
    public void sendMqToPanZe(AbnormalPictureMq abnormalPictureMq,Integer siteCode){
        try{
            abnormalPictureMq.setAbnormalId("");    //TODO 根据运单号从es中获取异常id（主键）
            InvokeResult<String> result = searchExcessPicture(abnormalPictureMq.getWaybillCode(),siteCode);
            if(result != null && result.getCode() == InvokeResult.RESULT_SUCCESS_CODE){
                abnormalPictureMq.setExcessPictureAddress(result.getData());
            }else{
                return;
            }
            dmsAbnormalInfoMQToPanZe.send(abnormalPictureMq.getWaybillCode(), JsonHelper.toJson(abnormalPictureMq));
        }catch (Exception e){
            logger.error("异常消息发送失败!"+abnormalPictureMq.getWaybillCode());
        }
    }

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    @Override
    public PagerResult<WeightAndVolumeCheck> queryByCondition(WeightAndVolumeCheckCondition condition) {

        //TODO 从es中获取数据并返回前台
        return null;
    }

    /**
     * 导出
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("复核日期");
        heads.add("运单号");
        heads.add("包裹号");
        heads.add("商家名称");
        heads.add("信任商家");
        heads.add("复核区域");
        heads.add("复核分拣");
        heads.add("机构类型");
        heads.add("复核人erp");
        heads.add("分拣复重kg");
        heads.add("复核长宽高cm");
        heads.add("复核体积cm³");
        heads.add("计费操作区域");
        heads.add("计费操作机构");
        heads.add("计费操作人ERP");
        heads.add("计费重量kg");
        heads.add("计费体积cm³");
        heads.add("重量差异");
        heads.add("体积重量差异");
        heads.add("误差标准值");
        heads.add("是否超标");
        heads.add("有无图片");
        heads.add("图片链接");
        resList.add(heads);
        condition.setLimit(-1);
        PagerResult<WeightAndVolumeCheck> result = queryByCondition(condition);
        if(result != null && result.getRows() != null && result.getRows().size() > 0){
            List<WeightAndVolumeCheck> list = result.getRows();
            //表格信息
            for(WeightAndVolumeCheck weightAndVolumeCheck : list){
                List<Object> body = Lists.newArrayList();
                body.add(weightAndVolumeCheck.getReviewDate() == null ? null : DateHelper.formatDate(weightAndVolumeCheck.getReviewDate(), Constants.DATE_TIME_FORMAT));
                body.add(weightAndVolumeCheck.getWaybillCode());
                body.add(weightAndVolumeCheck.getPackageCode());
                body.add(weightAndVolumeCheck.getBusiName());
                body.add(weightAndVolumeCheck.getIsTrustBusiName()==1?"是":"否");
                body.add(weightAndVolumeCheck.getReviewOrg());
                body.add(weightAndVolumeCheck.getReviewCreateSiteName());
                body.add(weightAndVolumeCheck.getMechanismType()==1?"分拣中心":"转运中心");
                body.add(weightAndVolumeCheck.getReviewErp());
                body.add(weightAndVolumeCheck.getReviewWeight());
                body.add(weightAndVolumeCheck.getReviewLwh());
                body.add(weightAndVolumeCheck.getReviewVolume());
                body.add(weightAndVolumeCheck.getBillingOperateOrg());
                body.add(weightAndVolumeCheck.getBillingOperateDepartment());
                body.add(weightAndVolumeCheck.getBillingOperateErp());
                body.add(weightAndVolumeCheck.getBillingWeight());
                body.add(weightAndVolumeCheck.getBillingVolume());
                body.add(weightAndVolumeCheck.getWeightDiff());
                body.add(weightAndVolumeCheck.getVolumeWeightDiff());
                body.add(weightAndVolumeCheck.getDiffStandard());
                body.add(weightAndVolumeCheck.getIsExcess()==1?"超标":"未超标");
                body.add(weightAndVolumeCheck.getIsHasPicture()==1?"有":"无");
                body.add(weightAndVolumeCheck.getPictureAddress());
                resList.add(body);
            }
        }
        return  resList;
    }


    /**
     * 列出对象(最多返回1千 )
     * @param prefix 前缀
     * @param marker 标记 从marker开始获取列表
     * @param maxKeys 返回 Object 信息的数量，最大为 1000
     */
    public ObjectListing listObject(String prefix, String marker, int maxKeys ){
        if(maxKeys <= 0 ){
            maxKeys = 1000;
        }
        String value = PropertiesHelper.newInstance().getValue(JSS_BUCKET);
        BucketService bucketService = jingdongStorageService.bucket(value);
        if(StringUtils.isNotBlank(prefix)){
            bucketService.prefix(prefix);
        }
        if(StringUtils.isNotBlank(marker)){
            bucketService.marker(marker);
        }
        return  bucketService.maxKeys(maxKeys).listObject();
    }

    /**
     * 获取对应版本的下载地址
     * 抛出异常
     */
    public URI getURI(String keyName){
        //获得带有预签名的下载地址timeout == 10000
        String value = PropertiesHelper.newInstance().getValue(JSS_BUCKET);
        URI uri = jingdongStorageService.bucket(value).object(keyName).generatePresignedUrl(10000);
        return uri;
    }
}

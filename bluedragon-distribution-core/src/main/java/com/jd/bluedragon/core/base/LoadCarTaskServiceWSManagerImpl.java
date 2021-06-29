package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.merchant.api.pack.dto.LoadCarTaskReqDto;
import com.jd.merchant.api.pack.ws.LoadCarTaskServiceWS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("loadCarTaskServiceWSManagerImpl")
public class LoadCarTaskServiceWSManagerImpl implements LoadCarTaskServiceWSManager {

    @Autowired
    private LoadCarTaskServiceWS loadCarTaskServiceWS;


    @Override
    public JdCResponse<Boolean> uploadPhotoCheck(GoodsLoadingReq goodsLoadingReq) {
        JdCResponse<Boolean> res = new JdCResponse<Boolean>();
        LoadCarTaskReqDto loadCarTaskReqDto = null;
        try{
            loadCarTaskReqDto = new LoadCarTaskReqDto();
            loadCarTaskReqDto.setOperatorErp(goodsLoadingReq.getUser().getUserErp());
            loadCarTaskReqDto.setSiteCode(goodsLoadingReq.getCurrentOperate().getSiteCode());
            loadCarTaskReqDto.setBusinessId(goodsLoadingReq.getTaskId());
            loadCarTaskReqDto.setBusinessType(GoodsLoadScanConstants.TRANSPORT_BUSINESS_TYPE_LOAD);

            //cf: https://cf.jd.com/pages/viewpage.action?pageId=481378850
            com.jd.merchant.api.common.dto.JdCResponse<Boolean> jsfRes = loadCarTaskServiceWS.uploadPhotoCheck(loadCarTaskReqDto);
            if(jsfRes == null) {
                if(log.isInfoEnabled()) {
                    log.info("LoadCarTaskServiceWSManagerImpl.uploadPhotoCheck---上传照片校验返回null, 参数=【{}】", JsonUtils.toJSONString(loadCarTaskReqDto));
                }
                res.toFail("上传照片校验异常");
            } else if(!jsfRes.getCode().equals(200)) {
                if(log.isInfoEnabled()) {
                    log.info("LoadCarTaskServiceWSManagerImpl.uploadPhotoCheck---上传照片校验错误, 参数=【{}】，返回=【{}】",
                            JsonUtils.toJSONString(loadCarTaskReqDto), JsonUtils.toJSONString(jsfRes));
                }
                res.toFail(jsfRes.getMessage());
            }

            res.toSucceed();
            res.setData(jsfRes.getData());
            return res;

        }catch (Exception e) {
            if(log.isInfoEnabled()) {
                log.info("LoadCarTaskServiceWSManagerImpl.uploadPhotoCheck--error--上传照片校验返回null, 参数=【{}】", JsonUtils.toJSONString(loadCarTaskReqDto), e);
            }
            res.toError("操作失败");
            return res;
        }
    }
}

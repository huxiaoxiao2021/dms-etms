package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.ql.asset.api.AssetApplyQueryApi;
import com.jd.ql.asset.dto.MatterPackageRelationDto;
import com.jd.ql.asset.dto.ResultData;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 集包袋相关服务管理类
 *
 * @author: hujiping
 * @date: 2020/5/22 16:35
 */
@Service("assertQueryManager")
public class AssertQueryManagerImpl implements AssertQueryManager {

    private static final Logger log = LoggerFactory.getLogger(AssertQueryManagerImpl.class);

    @Autowired
    private AssetApplyQueryApi assetApplyQueryApi;

    /**
     * 根据运单号获取集包袋
     * @see "https://cf.jd.com/pages/viewpage.action?pageId=301813577"
     * @param dto
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.AssertQueryManagerImpl.queryBindMaterialByCode",
            mState = {JProEnum.TP})
    public ResultData<List<String>> queryBindMaterialByCode(MatterPackageRelationDto dto) {

        return assetApplyQueryApi.queryBindMaterialByCode(dto);
    }


}

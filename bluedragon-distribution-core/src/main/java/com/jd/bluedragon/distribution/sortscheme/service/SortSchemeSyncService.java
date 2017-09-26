package com.jd.bluedragon.distribution.sortscheme.service;

import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;

/**
 * Created by wuzuxiang on 2017/1/11.
 */
public interface SortSchemeSyncService {

    public Boolean sendDtc(SortSchemeRequest request,String url,Integer siteCode);

    public boolean sync(String url,String siteCode);
}

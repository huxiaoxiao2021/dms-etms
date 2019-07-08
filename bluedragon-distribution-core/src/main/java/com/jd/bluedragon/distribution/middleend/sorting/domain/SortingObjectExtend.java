package com.jd.bluedragon.distribution.middleend.sorting.domain;

import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.shared.services.sorting.api.dto.SortingObject;

public class SortingObjectExtend extends SortingObject{
    private String boxCode;
    private SiteDto createSite;
    private SiteDto receiveSite;
    private Sorting dmsSorting;

    public SortingObjectExtend(Task sortingTask){
        String body = sortingTask.getBody().substring(1, sortingTask.getBody().length() - 1);
        SortingRequest request = JsonHelper.jsonToArray(body, SortingRequest.class);
        if (request != null) {
            Sorting dmsSorting = Sorting.toSorting(request);
            dmsSorting.setStatus(Sorting.STATUS_DONE);// 运单回传状态默认为1，以后可以去掉
            //补上始发和目的


            return sorting;
        }
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public SiteDto getCreateSite() {
        return createSite;
    }

    public void setCreateSite(SiteDto createSite) {
        this.createSite = createSite;
    }

    public SiteDto getReceiveSite() {
        return receiveSite;
    }

    public void setReceiveSite(SiteDto receiveSite) {
        this.receiveSite = receiveSite;
    }

    public Sorting getDmsSorting() {
        return dmsSorting;
    }

    public void setDmsSorting(Sorting dmsSorting) {
        this.dmsSorting = dmsSorting;
    }
}

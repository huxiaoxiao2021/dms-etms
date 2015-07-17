package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * @author dudong
 * @date 2015/5/12
 */
public class SortDistResponse extends JdResponse {
    private static final long serialVersionUID = -6558658115889622512L;
    private List<SortDistRelation> sortDists;

    public SortDistResponse(){

    }

    public SortDistResponse(Integer code, String message){
        super(code, message);
    }

    public List<SortDistRelation> getSortDists() {
        return sortDists;
    }

    public void setSortDists(List<SortDistRelation> sortDists) {
        this.sortDists = sortDists;
    }
}

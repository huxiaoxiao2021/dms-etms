package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * Created by dudong on 2015/4/17.
 */
public class DatadictResponse extends JdResponse{
    private static final long serialVersionUID = -965005212487805352L;

    private List<BaseDatadict> datadicts;

    public DatadictResponse(){
        super();
    }

    public DatadictResponse(Integer code, String message){
        super(code,message);
    }

    public List<BaseDatadict> getDatadicts() {
        return datadicts;
    }

    public void setDatadicts(List<BaseDatadict> datadicts) {
        this.datadicts = datadicts;
    }
}

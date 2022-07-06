package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import lombok.Data;

import java.io.Serializable;
@Data
public class UnloadBaseDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;
    private CurrentOperate currentOperate;
    private User user;
    private String vehicleNumber;
}

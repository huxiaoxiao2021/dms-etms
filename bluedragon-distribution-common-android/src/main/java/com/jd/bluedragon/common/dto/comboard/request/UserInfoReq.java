package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;

public class UserInfoReq extends BaseReq implements Serializable {

  private static final long serialVersionUID = 9091979548025713254L;
  private String erp;
  private String passwd;

  public String getErp() {
    return erp;
  }

  public void setErp(String erp) {
    this.erp = erp;
  }

  public String getPasswd() {
    return passwd;
  }

  public void setPasswd(String passwd) {
    this.passwd = passwd;
  }
}

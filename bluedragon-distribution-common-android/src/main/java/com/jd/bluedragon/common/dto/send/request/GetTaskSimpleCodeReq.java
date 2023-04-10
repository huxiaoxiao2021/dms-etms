package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;
import java.util.List;

public class GetTaskSimpleCodeReq extends BaseReq implements Serializable {

  private static final long serialVersionUID = -2905776876881227719L;
  //主任务编号-派车单
  private String sendVehicleBizId;
  //子任务编号-派车单明细
  private String sendVehicleDetailBizId;
  /**
   * 拍照图片
   */
  private List<String> imgUrlList;

  public String getSendVehicleBizId() {
    return sendVehicleBizId;
  }

  public void setSendVehicleBizId(String sendVehicleBizId) {
    this.sendVehicleBizId = sendVehicleBizId;
  }

  public String getSendVehicleDetailBizId() {
    return sendVehicleDetailBizId;
  }

  public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
    this.sendVehicleDetailBizId = sendVehicleDetailBizId;
  }

  public List<String> getImgUrlList() {
    return imgUrlList;
  }

  public void setImgUrlList(List<String> imgUrlList) {
    this.imgUrlList = imgUrlList;
  }
}

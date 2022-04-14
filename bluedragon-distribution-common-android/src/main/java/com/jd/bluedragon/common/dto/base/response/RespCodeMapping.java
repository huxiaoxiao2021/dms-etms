package com.jd.bluedragon.common.dto.base.response;

/**
 * @author weixiaofeng12
 */
public enum RespCodeMapping {

  SUCCESS(200, "成功"),
  UNKNOW_ERROR(500, "服务器开小差，请联系分拣小秘！"),
  MINI_STORE_DEVICE_CODE_ISNULL(5001,"设备码不能为空！"),
  MINI_STORE_HASBEEN_BIND(5001,"微仓码已被绑定！"),
  MINI_STORE_IS_NOT_AVAILABLE(5002,"微仓当前不可用！"),
  MINISTORE_SYS_INVOKE_EXCEPTION(-1,"保温箱服务调用异常"),
  INCE_BOARD_HASBEEN_BIND(5003,"冰板码已经被被绑定！"),
  INCE_BOARD_IS_NOT_AVAILABLE(5004,"冰板当前不可用！"),
  BOX_HASBEEN_BIND(5005,"箱号已经被绑定！"),
  DEVICE_HASBEEN_BIND(5006,"已经存在绑定的设备记录，请勿重复绑定！"),
  NO_LEGAL_BIND_RELATIONSHIP(5008,"未找到正确的绑定关系"),
  NO_BIND_DATA(5009,"未查询到相关的绑定数据！"),
  NO_BIND_RELATION_BETWEEN_BOX_AND_PACKAGE(5010,"该包裹未在集包码下集包！");


  RespCodeMapping(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  private Integer code;
  private String message;

  public void setMessage(String message) {
    this.message = message;
  }

  RespCodeMapping(String message) {
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public static RespCodeMapping getProperType(String name) {
    RespCodeMapping[] respCodeMappings = RespCodeMapping.values();
    for (RespCodeMapping respCodeMapping : respCodeMappings) {
      if (respCodeMapping.name().equals(name)) {
        return respCodeMapping;
      }
    }
    return null;
  }

  public static RespCodeMapping responseCodeMappingAdapter(
          RespCodeMapping respCodeMapping, String msg) {
    if (null != msg && !"".equals(msg)) {
      respCodeMapping.setMessage(respCodeMapping.getMessage() + msg);
    }
    return respCodeMapping;
  }
}

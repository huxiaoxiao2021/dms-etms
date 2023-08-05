package com.jd.bluedragon.distribution.station.query;

import java.io.Serializable;

public class NoticeToTimelineDto implements Serializable {

  private static final long serialVersionUID = 7364646052060970029L;
  private String titile;
  private String content;
  private String url;
  private String erp;

  public String getTitile() {
    return titile;
  }

  public void setTitile(String titile) {
    this.titile = titile;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getErp() {
    return erp;
  }

  public void setErp(String erp) {
    this.erp = erp;
  }
}

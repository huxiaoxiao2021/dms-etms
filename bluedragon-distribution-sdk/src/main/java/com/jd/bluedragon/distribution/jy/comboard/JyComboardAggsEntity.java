package com.jd.bluedragon.distribution.jy.comboard;

import java.util.Date;

public class JyComboardAggsEntity {
    private Long id;

    private String send_flow;

    private String biz_id;

    private String board_code;

    private String product_type;

    private Byte scan_type;

    private Integer scanned_count;

    private Integer board_count;

    private Integer more_scanned_count;

    private Integer intercept_count;

    private Integer wait_scan_count;

    private Date create_time;

    private Date update_time;

    private Boolean yn;

    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSend_flow() {
        return send_flow;
    }

    public void setSend_flow(String send_flow) {
        this.send_flow = send_flow;
    }

    public String getBiz_id() {
        return biz_id;
    }

    public void setBiz_id(String biz_id) {
        this.biz_id = biz_id;
    }

    public String getBoard_code() {
        return board_code;
    }

    public void setBoard_code(String board_code) {
        this.board_code = board_code;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public Byte getScan_type() {
        return scan_type;
    }

    public void setScan_type(Byte scan_type) {
        this.scan_type = scan_type;
    }

    public Integer getScanned_count() {
        return scanned_count;
    }

    public void setScanned_count(Integer scanned_count) {
        this.scanned_count = scanned_count;
    }

    public Integer getBoard_count() {
        return board_count;
    }

    public void setBoard_count(Integer board_count) {
        this.board_count = board_count;
    }

    public Integer getMore_scanned_count() {
        return more_scanned_count;
    }

    public void setMore_scanned_count(Integer more_scanned_count) {
        this.more_scanned_count = more_scanned_count;
    }

    public Integer getIntercept_count() {
        return intercept_count;
    }

    public void setIntercept_count(Integer intercept_count) {
        this.intercept_count = intercept_count;
    }

    public Integer getWait_scan_count() {
        return wait_scan_count;
    }

    public void setWait_scan_count(Integer wait_scan_count) {
        this.wait_scan_count = wait_scan_count;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
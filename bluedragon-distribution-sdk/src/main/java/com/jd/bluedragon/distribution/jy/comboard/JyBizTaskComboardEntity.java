package com.jd.bluedragon.distribution.jy.comboard;

import java.util.Date;

public class JyBizTaskComboardEntity {
    private Long id;

    private String biz_id;

    private String board_code;

    private String send_code;

    private Byte status;

    private Long start_site_id;

    private String start_site_name;

    private Long end_site_id;

    private String end_site_name;

    private String create_user_erp;

    private String create_user_name;

    private String update_user_erp;

    private String update_user_name;

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

    public String getSend_code() {
        return send_code;
    }

    public void setSend_code(String send_code) {
        this.send_code = send_code;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getStart_site_id() {
        return start_site_id;
    }

    public void setStart_site_id(Long start_site_id) {
        this.start_site_id = start_site_id;
    }

    public String getStart_site_name() {
        return start_site_name;
    }

    public void setStart_site_name(String start_site_name) {
        this.start_site_name = start_site_name;
    }

    public Long getEnd_site_id() {
        return end_site_id;
    }

    public void setEnd_site_id(Long end_site_id) {
        this.end_site_id = end_site_id;
    }

    public String getEnd_site_name() {
        return end_site_name;
    }

    public void setEnd_site_name(String end_site_name) {
        this.end_site_name = end_site_name;
    }

    public String getCreate_user_erp() {
        return create_user_erp;
    }

    public void setCreate_user_erp(String create_user_erp) {
        this.create_user_erp = create_user_erp;
    }

    public String getCreate_user_name() {
        return create_user_name;
    }

    public void setCreate_user_name(String create_user_name) {
        this.create_user_name = create_user_name;
    }

    public String getUpdate_user_erp() {
        return update_user_erp;
    }

    public void setUpdate_user_erp(String update_user_erp) {
        this.update_user_erp = update_user_erp;
    }

    public String getUpdate_user_name() {
        return update_user_name;
    }

    public void setUpdate_user_name(String update_user_name) {
        this.update_user_name = update_user_name;
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
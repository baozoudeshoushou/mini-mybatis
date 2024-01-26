package com.lin.mybatis.entity;

import java.sql.Timestamp;

/**
 * @Author linjiayi5
 * @Date 2024/1/26 16:43:02
 */
public class TheSysUser {

    private Long theId;

    private String theTenantId;

    private String theName;

    private Timestamp update_time;

    private Integer theVersion;

    public Long getTheId() {
        return theId;
    }

    public void setTheId(Long theId) {
        this.theId = theId;
    }

    public String getTheTenantId() {
        return theTenantId;
    }

    public void setTheTenantId(String theTenantId) {
        this.theTenantId = theTenantId;
    }

    public String getTheName() {
        return theName;
    }

    public void setTheName(String theName) {
        this.theName = theName;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public Integer getTheVersion() {
        return theVersion;
    }

    public void setTheVersion(Integer theVersion) {
        this.theVersion = theVersion;
    }

    @Override
    public String toString() {
        return "TheSysUser{" +
                "theId=" + theId +
                ", theTenantId='" + theTenantId + '\'' +
                ", theName='" + theName + '\'' +
                ", update_time=" + update_time +
                ", theVersion=" + theVersion +
                '}';
    }
}

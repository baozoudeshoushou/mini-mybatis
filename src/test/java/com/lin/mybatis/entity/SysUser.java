package com.lin.mybatis.entity;


import java.sql.Timestamp;

/**
 * @Author linjiayi5
 * @Date 2023/5/5 16:41:49
 */
public class SysUser {

    private Long id;

    private String tenant_id;

    private String name;

    private Timestamp update_time;

    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "id=" + id +
                ", tenant_id='" + tenant_id + '\'' +
                ", name='" + name + '\'' +
                ", update_time=" + update_time +
                ", version=" + version +
                '}';
    }

}

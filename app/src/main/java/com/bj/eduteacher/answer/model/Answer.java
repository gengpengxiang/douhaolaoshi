package com.bj.eduteacher.answer.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Answer {


    /**
     * shiti_id : 1
     * daan_teacher : 1,2
     * shiti_ordernum : 1
     */
    @Id(autoincrement = true)
    private Long id;


    @JSONField(ordinal = 1)
    private int shiti_id;

    @JSONField(ordinal = 2)
    private String daan_teacher;
    @JSONField(ordinal = 3)
    private int shiti_ordernum;


    @Generated(hash = 1757910699)
    public Answer(Long id, int shiti_id, String daan_teacher, int shiti_ordernum) {
        this.id = id;
        this.shiti_id = shiti_id;
        this.daan_teacher = daan_teacher;
        this.shiti_ordernum = shiti_ordernum;
    }

    @Generated(hash = 53889439)
    public Answer() {
    }

    public Answer(int shiti_id, String daan_teacher, int shiti_ordernum) {
        this.shiti_id = shiti_id;
        this.daan_teacher = daan_teacher;
        this.shiti_ordernum = shiti_ordernum;
    }

    public int getShiti_id() {
        return shiti_id;
    }

    public void setShiti_id(int shiti_id) {
        this.shiti_id = shiti_id;
    }

    public String getDaan_teacher() {
        return daan_teacher;
    }

    public void setDaan_teacher(String daan_teacher) {
        this.daan_teacher = daan_teacher;
    }

    public int getShiti_ordernum() {
        return shiti_ordernum;
    }

    public void setShiti_ordernum(int shiti_ordernum) {
        this.shiti_ordernum = shiti_ordernum;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}

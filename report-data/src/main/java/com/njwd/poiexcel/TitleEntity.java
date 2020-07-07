package com.njwd.poiexcel;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/3/3 9:48
 */
public class TitleEntity {
    public  String t_id;
    public  String t_pid;
    public  String t_content;
    public  String t_fieldName;
    public TitleEntity(){}
    public TitleEntity(String t_id, String t_pid, String t_content, String t_fieldName) {
        this.t_id = t_id;
        this.t_pid = t_pid;
        this.t_content = t_content;
        this.t_fieldName = t_fieldName;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getT_pid() {
        return t_pid;
    }

    public void setT_pid(String t_pid) {
        this.t_pid = t_pid;
    }

    public String getT_content() {
        return t_content;
    }

    public void setT_content(String t_content) {
        this.t_content = t_content;
    }

    public String getT_fieldName() {
        return t_fieldName;
    }

    public void setT_fieldName(String t_fieldName) {
        this.t_fieldName = t_fieldName;
    }

}

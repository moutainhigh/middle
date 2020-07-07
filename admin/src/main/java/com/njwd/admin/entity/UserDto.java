package com.njwd.admin.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 *@description:
 *@author: fancl
 *@create: 2019-12-19 
 */
public class UserDto implements Serializable {
    /**
     * 用户账号
     */
    private String account;
    /**
     * 用户密码
     */
    private String password;

    private Integer age;
    /**
     * 图片验证码
     */
    /*@NotBlank(message = "验证码不允许为空,请输入")*/
    private String validCode;

    /**
     * 记住我
     */
    private boolean rememberMe;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public void setAge(Integer age){
        this.age = age;
    }
    public Integer getAge(){
        return age;
    }
}

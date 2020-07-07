package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.Roles;
import com.njwd.entity.admin.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;


/**
 * @author caihui
 * @date 2019/10/16
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserVo extends User {
    /**
     * 应用ID
     */
    private String appId;
    /**
     * 应用账号
     */
    private String appCount;
    /**
     * sessionId登录时返回
     */
    private String sessionId;
    /**
     * 返回前台提示消息
     */
    private String message;
    /**
     * 角色类型名称
     */
    private String roleName;
    /**
     * 返回前端登录失败次数
     */
    private int failNum;
    /**
     * 组织名称
     */
    private String orgName;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 岗位名称
     */
    private String postName;
    /**
     * 管理员数量：用于登录时判断是否是管理员
     */
    private int adminIdCount;

    /**
     * 是否管理员（0：否，1：是）
     */
    private Byte isAdmin;

    /**
     * 角色权限
     */
    private Roles roles;
    /**
     * 登录时，密码还有10天过期，消息提醒
     */
    private String remindedMessage;

    /**
     * 角色菜单按钮权限
     */
    private List<AppVo> roleInfo;

    /**
     * 系统唯一标识
     */
    private String appSign;

    /**
     * 数据权限集合
     */
    private List<UserJurisdictionVo> dataInfo;

    /**
     * 数据权限
     */
    private String dataInfoStr;


}

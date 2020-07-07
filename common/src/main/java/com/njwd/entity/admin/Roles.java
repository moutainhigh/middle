package com.njwd.entity.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Roles {

    /***    首页  ***/
    /**
     * 浏览
     */
    private String homeSkim;

    /**
     * HR同步
     */
    private String homeReceive;

    /**
     * 内部用户
     */
    private String homeInnerUser;

    /**
     * 外部用户
     */
    private String homeExterUser;

    /**
     * 组织数量
     */
    private String homeOrgNum;

    /**
     * 部门数量
     */
    private String homeDeptNum;

    /**
     * 岗位数量
     */
    private String homePostNum;

    /**
     * 应用数量
     */
    private String homeAppNum;

    /***    权限  ***/
    /**
     * 浏览
     */
    private String roleSkim;

    /**
     * 新增
     */
    private String roleCreate;

    /**
     * 编辑
     */
    private String roleEdit;

    /**
     * 权限
     */
    private String roleControl;

    /**
     * 删除
     */
    private String roleDelete;

    /***    日志  ***/
    /**
     * 操作日志
     */
    private String handleSkim;

    /**
     * 登录日志
     */
    private String loginSkim;

    /***    用户  ***/
    /**
     * 浏览
     */
    private String userSkim;

    /**
     * 新增
     */
    private String userCreate;

    /**
     * 批量开通
     */
    private String userBatchOpen;

    /**
     * 批量回收
     */
    private String userBatchRecover;

    /**
     * 编辑
     */
    private String userEdit;

    /**
     * 解锁
     */
    private String userDeBlock;

    /**
     * 删除
     */
    private String userDelete;

    /**
     * 重置密码
     */
    private String userResetPassWord;

    /***    组织  ***/
    /**
     * 浏览
     */
    private String orgSkim;

    /**
     * 添加组织
     */
    private String orgAddForm;

    /**
     * 添加子公司
     */
    private String orgAddSubCompany;

    /**
     * 添加部门
     */
    private String orgAddDepartment;

    /**
     * 添加子部门
     */
    private String orgAddSubDepartment;

    /**
     * 编辑
     */
    private String orgEdit;

    /**
     * 删除
     */
    private String orgDelete;

    /***    岗位  ***/
    /**
     * 浏览
     */
    private String postSkim;

    /**
     * 新增
     */
    private String postCreate;

    /**
     * 编辑
     */
    private String postEdit;

    /**
     * 删除
     */
    private String postDelete;

    /***    应用  ***/
    /**
     * 浏览
     */
    private String applySkim;

    /**
     * 新增
     */
    private String applyCreate;

    /**
     * 编辑
     */
    private String applyEdit;

    /**
     * 删除
     */
    private String applyDelete;

    /**
     *  禁用 启用
     */
    private String applyStatus;

    /***    HR同步日志  ***/
    /**
     * 浏览
     */
    private String hrSkim;

    /**
     * 同步
     */
    private String hrReceive;

}
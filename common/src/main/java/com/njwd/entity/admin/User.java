package com.njwd.entity.admin;

import com.njwd.entity.admin.vo.AppVo;
import com.njwd.entity.admin.vo.UserJurisdictionVo;
import com.njwd.entity.basedata.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class User extends BaseModel {

    /**
    * 企业ID
    */
    private Long rootEnterpriseId;
    /**
     * 企业名称
     */
    private String rootEnterpriseName;

    /**
    * 用户类型 0：内部用户、1：外部用户
    */
    private Integer userType;

    /**
    * 来源系统用户ID
    */
    private String sourceUserId;
    /**
     * 用户ID
     */
    private Long id;
    /**
    * 员工号
    */
    private String userCode;
    /**
     * 用户名
     */
    private String userName;

    /**
    * 密码
    */
    private String password;

    /**
    * 姓名
    */
    private String name;

    /**
    * 手机号
    */
    private String mobile;

    /**
    * 邮箱
    */
    private String email;

    /**
    * 身份类型 0：员工、 1：经销商、2：供应商
    */
    private Byte identityType;

    /**
    * 证件类型 0：身份证、1：护照
    */
    private Byte credentialType;

    /**
    * 证件号
    */
    private String credentialNo;

    /**
    * 所属集团 归属集团ID
    */
    private Long groupId;

    private String orgCode;

    /**
    * 所属组织 组织表ID
    */
    private Long orgId;

    private String deptCode;

    /**
    * 所属部门 部门表ID
    */
    private Long deptId;

    private String jobsCode;

    /**
    * 岗位 岗位表ID
    */
    private Long jobsId;

    /**
    * 角色 角色表ID
    */
    private Long roleId;

    /**
    * 职级 职级
    */
    private String jobRankId;

    private String mainDeptCode;

    /**
    * 主职部门 部门表ID
    */
    private Long mainDeptId;

    private String secondaryDeptCode;

    /**
    * 兼职部门 部门表ID
    */
    private Long secondaryDeptId;

    /**
     * 是否预置（0：是，表示超管，1：否，非超管用户）
     */
    private Byte isPreset;

    /**
    * 账户状态 0：正常、1：锁定
    */
    private Byte status;
    /**
     *数据来源：0：新增；1：导入
     */
    private Byte source;
    /**
     * 是否首次登录：0：否；1：是
     */
    private Byte isLoginFirst;
    /**
     * 登录密码生效时间
     */
    private Date pwdEffectiveTime;

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
     * 登录时，密码还有10天过期，消息提醒
     */
    private String remindedMessage;

    /**
     * 角色菜单按钮权限
     */
    private List<AppVo> roleInfo;

    /**
     * 数据权限集合
     */
    private List<UserJurisdictionVo> dataInfo;

    /**
     * 数据权限
     */
    private String dataInfoStr;
}
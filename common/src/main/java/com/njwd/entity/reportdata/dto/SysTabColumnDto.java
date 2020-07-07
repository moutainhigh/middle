package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.SysTabColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuxiang
 * 前端入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysTabColumnDto extends SysTabColumn {
    private static final long serialVersionUID = -7845036373705634834L;
    /**
     * 菜单: 0、USER 1、ADMIN
     */
    private Byte isEnterpriseAdmin;
}
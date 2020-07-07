package com.njwd.entity.reportdata.vo;

import com.njwd.entity.reportdata.SysTabColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @Author liuxiang
 * @Description 表格配置项
 * @Date:14:19 2019/6/19
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysTabColumnVo extends SysTabColumn {

    private static final long serialVersionUID = -4752436816844323077L;

    /**
     * 下级表格配置list
     */
    private List<SysTabColumnVo> children;
}
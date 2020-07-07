package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.App;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author XiaFq
 * @Description AppVo TODO
 * @Date 2019/11/11 7:28 下午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AppVo extends App {

    /**
     * 应用类型名称
     */
    private String appTypeName;

    /**
     * 对接方式名称
     */
    private String jointModeName;

    /**
     * 菜单列表
     */
    private List<MenuVo> menuList;

    /**
     * 菜单权限
     */
    private String roleStr;

    /**
     *  系统唯一标识
     */
    private String appSign;

}

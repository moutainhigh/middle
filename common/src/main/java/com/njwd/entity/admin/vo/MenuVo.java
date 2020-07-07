package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.Menu;
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
public class MenuVo extends Menu {
    /**
     * 组件集合
     */
    private List<MenuVo> menuList;

    /**
     * 组件集合
     */
    private List<MenuVo> buttonList;
}

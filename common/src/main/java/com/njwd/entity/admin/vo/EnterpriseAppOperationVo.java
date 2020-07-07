package com.njwd.entity.admin.vo;

import com.njwd.entity.admin.EnterpriseApp;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppAddVo 用户新增更新操作
 * @Date 2019/11/12 10:08 上午
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EnterpriseAppOperationVo extends EnterpriseApp {

    /**
     * 应用列表
     */
    private List<AppVo> appVos;

    /**
     * 标签列表
     */
    private List<TagVo> tagVos;
}

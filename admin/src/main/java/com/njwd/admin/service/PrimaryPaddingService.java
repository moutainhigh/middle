package com.njwd.admin.service;

import com.njwd.entity.admin.App;
import com.njwd.entity.admin.TableAttribute;
import com.njwd.entity.admin.dto.EntePrimaryPaddingDto;
import com.njwd.entity.admin.dto.EnterpriseAppDataTypeDto;
import com.njwd.entity.admin.dto.EnterpriseDataTypeDto;
import com.njwd.entity.admin.vo.AppTableAttributeVo;
import com.njwd.entity.admin.vo.PrimaryPaddingVo;

import java.util.List;

/**
* @Author Chenfulian
* @Description 数据填充Service类
* @Date  2019/11/20 10:36
*/
public interface PrimaryPaddingService {

    /**
     * 查询主系统可以修改填充的字段信息，即默认填充规则
     * @author Chenfulian
     * @date 2019/12/2 16:08
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    List<PrimaryPaddingVo> getPrimarySysFields(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 查询可行用于填充的应用
     * @author Chenfulian
     * @date 2019/12/2 16:10
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    List<App> getSelectableApp(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 获取第三方应用字段，用于填充中台字段
     * @author Chenfulian
     * @date 2019/12/2 16:11
     * @param enterpriseAppDataTypeDto 企业id,数据类型,应用id
     * @return
     */
    List<AppTableAttributeVo> getAppField(EnterpriseAppDataTypeDto enterpriseAppDataTypeDto);

    /**
     * 查询某企业某主数据的填充规则
     * @author Chenfulian
     * @date 2019/12/2 16:11
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    List<PrimaryPaddingVo> getPrimayPadding(EnterpriseDataTypeDto enterpriseDataTypeDto);

    /**
     * 更新填充规则
     * @author Chenfulian
     * @date 2019/12/2 16:12
     * @param entePrimaryPaddingDto 企业id,数据类型,填充规则list
     * @return
     */
    boolean updatePrimayPadding(EntePrimaryPaddingDto entePrimaryPaddingDto);

    /**
     * 查询填充规则，并且拼接sql
     * @author Chenfulian
     * @date 2019/12/2 16:14
     * @param enterpriseDataTypeDto 企业id,数据类型
     * @return
     */
    void dealPrimaryPadding(EnterpriseDataTypeDto enterpriseDataTypeDto);
}

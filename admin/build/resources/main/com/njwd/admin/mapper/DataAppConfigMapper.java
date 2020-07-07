package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.dto.DataAppConfigDto;
import com.njwd.entity.admin.vo.AppForEnterpriseVo;
import com.njwd.entity.admin.vo.DataAppConfigVo;
import com.njwd.entity.admin.vo.DataCategoryVo;

import java.util.List;

/**
 * @Author XiaFq
 * @Description DataAppConfigMapper 数据应用配置Mapper
 * @Date 2019/11/8 5:36 下午
 * @Version 1.0
 */
public interface DataAppConfigMapper extends BaseMapper {


    /**
     * 查询企业安装的应用列表
     * @author XiaFq
     * @date 2019/12/11 9:26 上午
     * @param enteId 企业id
     * @return List<DataAppConfigVo>
     */
    List<AppForEnterpriseVo> getDataAppList(String enteId);

    /**
     * 查询应用对应的数据类型
     * @author XiaFq
     * @date 2019/12/13 3:21 下午
     * @param dataAppConfigDto
     * @return List<DataCategoryVo>
     */
    List<DataCategoryVo> getDataCategoryList(DataAppConfigDto dataAppConfigDto);
    
    /**
     * 查询单个数据应用配置
     * @author XiaFq
     * @date 2019/12/11 11:59 上午
     * @param dataAppConfigDto
     * @return DataAppConfigVo
     */
    DataAppConfigVo getDataAppConfig(DataAppConfigDto dataAppConfigDto);

    /**
     * 保存应用数据配置
     * @author XiaFq
     * @date 2019/12/11 1:42 下午
     * @param dataAppConfigDto
     * @return 
     */
    void saveDataAppConfig(DataAppConfigDto dataAppConfigDto);

    /**
     * 更新应用数据配置
     * @author XiaFq
     * @date 2019/12/11 1:47 下午
     * @param dataAppConfigDto
     * @return
     */
    void updateDataAppConfig(DataAppConfigDto dataAppConfigDto);

    /**
     * 删除企业下应用的数据配置
     * @author XiaFq
     * @date 2019/12/11 1:47 下午
     * @param dataAppConfigDto
     * @return
     */
    void deleteByAppIdEnterpriseId(DataAppConfigDto dataAppConfigDto);

    /**
     * 批量插入
     * @author XiaFq
     * @date 2019/12/13 4:11 下午
     * @param dataCategoryVos
     * @return 
     */
    void batchSave(List<DataCategoryVo> dataCategoryVos);
}

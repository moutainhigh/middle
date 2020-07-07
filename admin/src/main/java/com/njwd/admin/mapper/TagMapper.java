package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.Tag;
import com.njwd.entity.admin.vo.TagVo;

import java.util.List;

/**
* @Author XiaFq
* @Description 应用标签表
* @Date  2019/11/12 9:47 上午
*/
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 查询应用标签列表
     * @author XiaFq
     * @date 2019/12/2 3:34 下午
     * @param
     * @return
     */
    List<TagVo> selectTagListForDict();

    /**
     * 通过企业应用id查询标签
     * @author XiaFq
     * @date 2019/12/2 3:34 下午
     * @param enterpriseAppId
     * @return List<TagVo>
     */
    List<TagVo> selectTagByEnteAppId(String enterpriseAppId);

    /**
     * 通过企业启用id删除企业标签
     * @author XiaFq
     * @date 2019/12/2 3:34 下午
     * @param enterpriseId
     * @return 
     */
    void deleteEnteTagByEnteAppId(String enterpriseId);
}

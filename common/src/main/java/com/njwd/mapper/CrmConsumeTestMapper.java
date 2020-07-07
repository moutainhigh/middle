package com.njwd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.CrmConsume;
import com.njwd.entity.kettlejob.dto.CrmConsumeDto;
import com.njwd.entity.kettlejob.vo.CrmConsumeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description 会员消息记录
 * @Author 会员消费记录
 * @Date 2019/11/14
 **/

@Repository
public interface CrmConsumeTestMapper extends BaseMapper<CrmConsume> {
    /**
     * 新增会员消费记录
     * @param list
     * @return
     */
    Integer addCrmCardConsumeTest(List<CrmConsumeDto> list);

    /**
     * 查询会员消费记录
     * @param crmCardConsumeDto
     * @return
     */
    List<CrmConsumeVo> findCrmCardConsumeBatchTest(@Param("crmCardConsumeDto") CrmConsumeDto crmCardConsumeDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateCrmCardConsumeTest(List<CrmConsumeDto> list);

    /**
     * 洗数据
     * @param consumeDto
     * @return
     */
    Integer updateCleanConsumeTest(@Param("consumeDto") CrmConsumeDto consumeDto);

    /**
     * 查询未清洗的消费流水数量
     * @param consumeDto
     * @return
     */
    Integer findUnCleanConsumeNumTest(@Param("consumeDto") CrmConsumeDto consumeDto);

    /**
     * 查询未清洗数据返回基础数据标识
     * @param consumeDto
     * @return
     */
    List<Map<String,Object>> findUnCleanCodeTest(@Param("consumeDto") CrmConsumeDto consumeDto);

    /**
     * 查询消费流水的最大时间
     * @param consumeDto
     * @return
     */
    String findMaxConsumeTimeTest(@Param("consumeDto") CrmConsumeDto consumeDto);

}

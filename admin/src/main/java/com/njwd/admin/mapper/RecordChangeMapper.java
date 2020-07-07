package com.njwd.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.admin.dto.RecordChangeDto;
import com.njwd.entity.admin.vo.RecordChangeVo;

import java.util.List;

/**
 * 匹配更变记录
 * @author XiaFq
 * @date 2020/1/7 3:46 下午
 */
public interface RecordChangeMapper extends BaseMapper {

    /**
     * 查询修改记录
     * @author XiaFq
     * @date 2020/1/8 9:06 上午
     * @param recordChangeDto
     * @return RecordChangeVo
     */
    RecordChangeVo getRecordChangeInfo(RecordChangeDto recordChangeDto);

    /**
     * 批量插入
     * @author XiaFq
     * @date 2020/1/8 9:06 上午
     * @param list
     * @return
     */
    void saveRecordChangeBatch(List<RecordChangeDto> list);
    
    /**
     * 批量更新
     * @author XiaFq
     * @date 2020/1/8 9:11 上午
     * @param list
     * @return 
     */
    void updateRecordChangeBatch(List<RecordChangeDto> list);

    /**
     * 查询rely表数据
     * @author XiaFq
     * @date 2020/1/8 11:56 上午
     * @param recordChangeDto
     * @return String
     */
    RecordChangeDto selectRelyDataByThirdId(RecordChangeDto recordChangeDto);
}

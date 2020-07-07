package com.njwd.platform.mapper;

import com.njwd.entity.platform.dto.NoticeDto;
import com.njwd.entity.platform.vo.NoticeVo;

import java.util.List;

public interface NoticeMapper {
    int deleteByPrimaryKey(Long noticeId);

    int insert(NoticeDto record);

    int insertSelective(NoticeDto record);

    int updateByPrimaryKeySelective(NoticeDto record);

    int updateByPrimaryKeyWithBLOBs(NoticeDto record);

    int updateByPrimaryKey(NoticeDto record);

    /**
     * @Description: 查询公告列表
     * @Param:
     * @return: List<NoticeVo>
     * @Author: huxianghong
     * @Date: 2020/3/25 17:15
     */
    List<NoticeVo> selectNoiceList(NoticeDto noticeDto);
}
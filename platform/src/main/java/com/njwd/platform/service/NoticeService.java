package com.njwd.platform.service;

import com.njwd.entity.platform.dto.NoticeDto;
import com.njwd.entity.platform.vo.NoticeVo;

import java.util.List;

/**
 * @Description 公告模块业务
 * @Date 2020/3/25 16:34
 * @Author 胡翔鸿
 */
public interface NoticeService {

    /**
     * @Description: 查询公告列表业务
     * @Param:
     * @return: List<NoticeVo>
     * @Author: huxianghong
     * @Date: 2020/3/25 17:15
     */
    List<NoticeVo> findNoticeList(NoticeDto noticeDto);
}

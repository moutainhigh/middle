package com.njwd.platform.service.impl;

import com.njwd.entity.platform.dto.NoticeDto;
import com.njwd.entity.platform.vo.NoticeVo;
import com.njwd.platform.mapper.NoticeMapper;
import com.njwd.platform.service.NoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    NoticeMapper noticeMapper;
    /**
     * @Description: 查询公告列表业务
     * @Param:
     * @return: List<NoticeVo>
     * @Author: huxianghong
     * @Date: 2020/3/25 17:15
     */
    @Override
    public List<NoticeVo> findNoticeList(NoticeDto noticeDto) {
        List<NoticeVo> noticeList = noticeMapper.selectNoiceList(noticeDto);
        return noticeList;
    }
}

package com.njwd.platform.controller;

import com.njwd.entity.platform.dto.NoticeDto;
import com.njwd.entity.platform.vo.NoticeVo;
import com.njwd.platform.service.NoticeService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 公告模块
 * @Date 2020/3/25 16:09
 * @Author 胡翔鸿
 */
@Api(value = "NoticeController",tags = "公告模块")
@RestController
@RequestMapping("notice")
public class NoticeController extends BaseController {

    @Resource
    NoticeService noticeService;

    /**
     * @Description: 查询公告列表
     * @Param:
     * @return: Result<List<NoticeVo>>
     * @Author: huxianghong
     * @Date: 2020/3/25 17:13
     */
    @ApiOperation(value = "无条件查询公告列表",notes = "无条件查询公告列表")
    @PostMapping("/findNoticeList")
    public Result<List<NoticeVo>> findNoticeList(){
        NoticeDto noticeDto = new NoticeDto();
        List<NoticeVo> noticeList = noticeService.findNoticeList(noticeDto);
        return ok(noticeList);
    }
}

package com.njwd.reportdata.controller;

import com.njwd.support.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 看板
 * @Author LuoY
 * @Date 2019/11/20
 */
@Api(value = "bossShowBoardController",tags = "看板")
@RestController
@RequestMapping("bossShowBoard")
public class BossShowBoardController extends BaseController {
}

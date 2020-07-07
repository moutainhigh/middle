package com.njwd.reportdata.api;

import com.njwd.entity.reportdata.dto.RealTimeProfitDto;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.util.Map;

/**
 * @ClassName RealTimeMiddleApi
 * @Description RealTimeMiddleApi
 * @Author admin
 * @Date 2020/4/28 15:49
 */
@RequestMapping("reportdata/realTimePorfitMiddle")
public interface RealTimeMiddleApi {

    /**
     * 批量更新报表表信息
     *
     * @param realTimProfitDto
     * @return Result
     * @author: 李宝
     * @create: 2020-04-28
     */
    @PostMapping("refreshRealTimeProfitMiddle")
    Result refreshRealTimeProfitMiddle(@RequestBody RealTimeProfitDto realTimProfitDto) throws ParseException;
}

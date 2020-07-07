package com.njwd.entity.platform.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PageApplyCashDto extends ApplyCashDto{

    private Page<ApplyCashDto> page = new Page<>();
}

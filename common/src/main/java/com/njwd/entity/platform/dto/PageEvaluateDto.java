package com.njwd.entity.platform.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class PageEvaluateDto extends EvaluateDto {

    private Page<EvaluateDto> page = new Page<>();
}

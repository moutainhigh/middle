package com.njwd.entity.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class EvaluatePostDto implements Serializable {

    private Integer pageNo;

    private Integer pageSize;

    private Long goodsId;
}

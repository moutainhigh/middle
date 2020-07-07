package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.DiscountsSafeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author jds
 * @Description 退赠优免安全阀值
 * @create 2019/12/3 9:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiscountsSafeDto extends DiscountsSafeVo implements Serializable {


    private List<DiscountsSafeDto> discountsSafeDtoList;


}

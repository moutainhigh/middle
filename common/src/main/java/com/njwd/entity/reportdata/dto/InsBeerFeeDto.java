package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.InsBeerFeeVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author jds
 * @Description 啤酒进场费
 * @create 2019/12/3 9:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InsBeerFeeDto extends InsBeerFeeVo implements Serializable {


    private List<InsBeerFeeDto> beerFeeList;


}

package com.njwd.entity.basedata.dto;

import com.njwd.entity.basedata.BaseShop;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/11/27 11:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseShopDto extends BaseShop {
    private static final long serialVersionUID = 8690188891574129602L;
    /**
     * 门店ID 集合
     */
    private List<String> shopIdList;
    /**
     * 区域ID 集合
     */
    private List<String> regionIdList;
}

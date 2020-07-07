package com.njwd.common;

import java.math.BigDecimal;

/**
 * @param
 * @author fancl
 * @description 供应链常量实体
 * @date 2020/3/25
 * @return
 */
public interface ScmConstant {

    interface FoodAnalysis {

        String WINE = "酒水";
        //央厨code
        String CENTER_CODE_CONFIG = "center_code";

    }

    //维度定义
    interface DimensionType {
        //菜品维度
        String FOOD = "food";
        //菜品门店维度
        String FOOD_SHOP = "food_shop";
    }

    //本期 同比环比标识
    interface ChainType {
        //本期
        String CURRENT = "current";
        //同比
        String THE_SAME = "the_same";
        //环比
        String CHAIN = "chain";
    }

    /**
     * 毛利分析表
     */
    interface GrossProfitAnalysis {
        //菜品库存周转天数计算数值
        BigDecimal DISH_STOCK_TURNOVER_DAYS = new BigDecimal(30);
    }
}

package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.dto.querydto.ScmQueryDto;
import com.njwd.entity.reportdata.dto.scm.SimpleFoodDto;
import com.njwd.entity.reportdata.vo.FoodGrossAnalysisVo;
import com.njwd.entity.reportdata.vo.RepPosDetailFoodVo;
import com.njwd.entity.reportdata.vo.scm.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @description 菜品分析
 * @author fancl
 * @date 2020/3/1
 * @param
 * @return
 */
public interface FootStyleAnalysisService {
    /**
     * @description 菜品毛利分析 本期
     * @author fancl
     * @date 2020/3/1
     * @param
     * @return
     */
    List<DishGrossProfitVo> getFoodGrossProfit(ScmQueryDto queryDto);
    
    /**
     * @description 导出菜品毛利分析
     * @author fancl
     * @date 2020/4/6
     * @param 
     * @return 
     */
    void exportFoodGrossProfit(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @description 同比
     * @author fancl
     * @date 2020/3/30
     * @param
     * @return
     */
    List<DishGrossProfitVo> getTheSame(ScmQueryDto queryDto);
    
    /**
     * @description 导出同比
     * @author fancl
     * @date 2020/4/6
     * @param 
     * @return 
     */
    void exportFoodGrossProfitTheSame(ExcelExportDto excelExportDto, HttpServletResponse response);

    /**
     * @description 环比
     * @author fancl
     * @date 2020/3/30
     * @param
     * @return
     */
    List<DishGrossProfitVo> getChain(ScmQueryDto queryDto);
    

    /**
     * @description 导出环比
     * @author fancl
     * @date 2020/4/6
     * @param 
     * @return 
     */
    void exportFoodGrossProfitChain(ExcelExportDto excelExportDto, HttpServletResponse response);


    /**
     * @description 得到菜品成本数据
     * @author fancl
     * @date 2020/3/28
     * @param
     * @return
     */
    List<DishGrossProfitVo> getDishBaseList(ScmQueryDto queryDto, List<RepPosDetailFoodVo> detailFoodList);



}

package com.njwd.reportdata.service;

import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.dto.querydto.ExcelExportDto;
import com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo;
import com.njwd.support.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author ZhuHC
 * @Date  2019/11/27 14:04
 * @Description
 */
public interface BaseShopService {

    /**
     * @Author ZhuHC
     * @Date  2019/11/27 14:15
     * @Param [baseShopDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.BaseShopVo>
     * @Description 查询门店相关信息
     */
    List<BaseShopVo> findShopInfo(BaseShopDto baseShopDto);

    //带门店面积的查询
    List<BaseShopVo> findShopInfoForArea(BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2020/4/1 10:43
     * @Param
     * @return
     * @Description 查询门店开业关停时间
     */
    List<BaseShopVo> findShopDate(BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2019/11/27 15:41
     * @Param [baseShopDto]
     * @return java.lang.Integer
     * @Description 更新门店 门店面积 状态等信息
     */
    Integer updateShopInfoById (@RequestBody BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 11:13
     * @Param
     * @return
     * @Description 禁用
     */
    Result<Integer> disableShopInfoById (@RequestBody BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 11:14
     * @Param
     * @return
     * @Description 反禁用
     */
    Result<Integer> enableShopInfoById (@RequestBody BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2019/11/29 11:21
     * @Param [baseShopDto, response]
     * @return void
     * @Description 导出
     */
    void exportExcel(BaseShopDto baseShopDto, HttpServletResponse response);

    /**
     * @Author ZhuHC
     * @Date  2019/12/2 17:32
     * @Param 
     * @return 
     * @Description 
     */
    Result importExcel(MultipartFile file);

    List<MembershipCardAnalysisVo> findShopDetail(MembershipCardAnalysisDto queryDto);

    /**
     * 查询门店
     * @Author ljc
     * @param queryDto
     * @return
     */
    String  findRouteShopName(BaseShopDto queryDto);

    /**
     * 获取组织明名称
     * @Author lj
     * @Date:15:15 2020/3/5
     * @param excelExportDto
     * @return java.lang.String
     **/
    String getOrgName(ExcelExportDto excelExportDto);
}

package com.njwd.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 产品相关页面的业务
 * @Date 2020/3/26 14:22
 * @Author 胡翔鸿
 */
public interface GoodsService {

    /**
     * @Description: 调用外部接口查询商品列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/26 14:33
     */
    List<GoodsReturnVO> findGoodsList(GoodsListDto goodsListDto);

    /**
     * @Description: 调用外部接口查询商品详情业务
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 14:09
     */
    GoodsDetailReturnVO findGoodsDetail(GoodsDetailDto goodsDetailDto);

    /**
     * @Description: 抽取下户线转驼峰的方法
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 14:09
     */
    String underlineToTump (String underLineString);

    /**
     * @Description: 修改产品点击量
     * @Param: DegreeDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/27 18:59
     */
    void doUpdateDegree(DegreeDto degreeDto);

    /**
     * @Description: 新增用户评论
     * @Param: EvaluateVo
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/27 19:40
     */
    void doAddEvalute(EvaluateDto evaluateDto);


    /**
     * @Description: 分页查询用户评价
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/28 15:31
     */
    Page<EvaluateVo> findEvaluatePage(@Param("param") PageEvaluateDto param);

    /**
     * @Description: 调用外部接口实现商品试用
     * @Param:  UseGoodsListDto
     * @return: GoodsProbationListVo
     * @Author: huxianghong
     * @Date: 2020/3/30 10:20
     */
    GoodsProbationListVo doAddForProbation(UseGoodsListDto useGoodsListDto);

    /**
     * @Description: 调用外部接口实现商品停用
     * @Param:  UseGoodsListDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/30 13:01
     */
    SimpleReturnVo doGoodsEnd(UseGoodsListDto useGoodsListDto);

    /**
     * @Description: 调用外部接口实现商品开通
     * @Param:  UseGoodsListDto
     * @return: GoodsOpenListVo
     * @Author: huxianghong
     * @Date: 2020/3/30 14:01
     */
    GoodsOpenListVo doAddForOpen(UseGoodsListDto useGoodsListDto);

    /**
     * @Description: 调用外部接口查询首页多种数据
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/30 10:01
     */
    IndexDataVo findIndexData(String crmUserId);

    /**
     * @Description: 查询商品的点击量和评价
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/30 10:01
     */
    TotalEvaluateVo findTotalEvaluate(GoodsDetailDto goodsDetailDto);
}

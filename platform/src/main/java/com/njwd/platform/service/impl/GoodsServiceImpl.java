package com.njwd.platform.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.*;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.mapper.DegreeMapper;
import com.njwd.platform.mapper.EvaluateMapper;
import com.njwd.platform.service.GoodsService;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.JsonUtils;
import com.njwd.utils.StringUtil;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Value("${njwdmss.server}")
    private String server;
    @Value("${njwdmss.url.list_goods}")
    private String listGoods;
    @Value("${njwdmss.url.find_goods}")
    private String findGoods;
    @Value("${njwdmss.url.probation_goods}")
    private String probationGoods;
    @Value("${njwdmss.url.end_goods}")
    private String endGoods;
    @Value("${njwdmss.url.open_goods}")
    private String openGoods;
    @Value("${njwdmss.url.index_data}")
    private String indexData;

    @Resource
    DegreeMapper degreeMapper;
    @Resource
    EvaluateMapper evaluateMapper;

    private static Pattern linePattern = Pattern.compile(PlatformContant.RegularString.UNDERLINE_SIGN);

    /**
     * @Description: 调用外部接口查询商品列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/26 14:33
     */
    @Override
    public List<GoodsReturnVO> findGoodsList(GoodsListDto goodsListDto) {
        Map<String, String> map =  JSONObject.parseObject(JSONObject.toJSONString(goodsListDto), Map.class);
        String params = "";
        for(Map.Entry entry:map.entrySet()){
            params = params+"&"+entry.getKey()+"="+entry.getValue();
        }
        String url = server+listGoods+params;
        String returnString  = HttpUtils.restPostJson(url,"");
//        String returnString  = ""; //一个死的返回值用于测试
        Matcher matcher = linePattern.matcher(returnString);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        String returnStringTwo = sb.toString();
        System.out.println("转换后JSON字符串为："+returnStringTwo);
        GoodsReturnVO goodsReturnVO = JSON.parseObject(returnStringTwo,GoodsReturnVO.class);
        //调整出参数据结构
        Set<Long> typeIdSet = new HashSet<Long>();
        for(GoodsVO goodsVO : goodsReturnVO.getListData()){
            typeIdSet.add(goodsVO.getTypeId());
        }
        List<GoodsReturnVO> goodsReturnList = new ArrayList<GoodsReturnVO>();
        for (Long typeId : typeIdSet){
            List<GoodsVO> goodsList = new ArrayList<GoodsVO>();
            GoodsReturnVO goodsReturn = new GoodsReturnVO();
            for(GoodsVO goodsVO : goodsReturnVO.getListData()){
                if(goodsReturn.getTypeId()==null){
                    BeanUtils.copyProperties(goodsReturnVO,goodsReturn);
                }
                if(typeId == goodsVO.getTypeId()){
                    goodsReturn.setTypeId(goodsVO.getTypeId());
                    goodsReturn.setTypeName(goodsVO.getTypeName());
                    goodsList.add(goodsVO);
                }
                goodsReturn.setListData(goodsList);
            }
            goodsReturnList.add(goodsReturn);
        }
        return goodsReturnList;
    }


    /**
     * @Description: 调用外部接口查询商品详情业务
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 14:09
     */
    @Override
    public GoodsDetailReturnVO findGoodsDetail(GoodsDetailDto goodsDetailDto) {

        Map<String, String> map =  JSONObject.parseObject(JSONObject.toJSONString(goodsDetailDto), Map.class);
        String params = "";
        for(Map.Entry entry:map.entrySet()){
            params = params+"&"+entry.getKey()+"="+entry.getValue();
        }
        String url = server+findGoods+params;
        String returnString  = HttpUtils.restPostJson(url,"");
        String returnTumpString = this.underlineToTump(returnString);
        GoodsDetailReturnVO goodsDetailReturnVO = JSON.parseObject(returnTumpString,GoodsDetailReturnVO.class);
        //查询点击量（关注度）
        DegreeDto degreeDto = new DegreeDto();
        degreeDto.setGoodsId(goodsDetailDto.getGoods_id());
        DegreeVo degreeVo = degreeMapper.selectDegree(degreeDto);
        //查询评价平均分
        EvaluateDto evaluateDto = new EvaluateDto();
        evaluateDto.setGoodsId(goodsDetailDto.getGoods_id());
        TotalEvaluateVo totalEvaluateVo = evaluateMapper.selectAvg(evaluateDto);
        totalEvaluateVo.setSumClicks(degreeVo.getClicks());
        goodsDetailReturnVO.getGoods().setTotalEvaluateVo(totalEvaluateVo);
        //查询评价列表
//        List<EvaluateVo> evaluateList = evaluateMapper.selectEvaluate(evaluateDto);
//        goodsDetailReturnVO.getGoods().setEvaluateList(evaluateList);
        return goodsDetailReturnVO;
    }


    /**
     * @Description: 抽取下户线转驼峰的方法
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/27 14:09
     */
    @Override
    public String underlineToTump(String underLineString) {
        System.out.println("转换前JSON字符串为："+underLineString);
        Matcher matcher = linePattern.matcher(underLineString);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        String TumpString = sb.toString();
        System.out.println("转换后JSON字符串为："+TumpString);
        return TumpString;
    }

    /**
     * @Description: 修改产品点击量
     * @Param: DegreeDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/27 18:59
     */
    @Override
    @Transactional
    public void doUpdateDegree(DegreeDto degreeDto) {
        //先查询有没有关于点击量的数据库条目
        DegreeVo degreeVo = degreeMapper.selectDegree(degreeDto);
        if(degreeVo==null){
            //没有相关数据库条目，新增一个条目
            degreeDto.setClicks(1L);
            Integer insertDegree = degreeMapper.insertDegree(degreeDto);
        }else {
            //有相关条目，修改其条目
            Integer UpdateDegree = degreeMapper.updateDegree(degreeDto);
        }
    }

    /**
     * @Description: 新增用户评论
     * @Param: EvaluateVo
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/27 19:40
     */
    @Override
    @Transactional
    public void doAddEvalute(EvaluateDto evaluateDto) {
        if(evaluateDto.getScore()>PlatformContant.Login.MAX_SCORE){
            throw new ServiceException(ResultCode.SCORE_TOO_BIG);
        }
        if(evaluateDto.getRemark().length()>=PlatformContant.Login.MAX_REMARK){
            throw new ServiceException(ResultCode.REMARK_TOO_LONG);
        }
        evaluateDto.setCreateTime(new Date());
        evaluateDto.setUpdateTime(new Date());
        Integer insertEvalute = evaluateMapper.insertEvalute(evaluateDto);
    }

    /**
     * @Description: 分页查询用户评价
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/28 15:31
     */
    @Override
    public Page<EvaluateVo> findEvaluatePage(PageEvaluateDto param) {

        Page<EvaluateVo> evaluateVoPage = evaluateMapper.selectEvaluateByPAge(param.getPage(),param);

        return evaluateVoPage;
    }

    /**
     * @Description: 调用外部接口实现商品试用
     * @Param:  UseGoodsListDto
     * @return: GoodsProbationListVo
     * @Author: huxianghong
     * @Date: 2020/3/30 10:20
     */
    @Override
    public GoodsProbationListVo doAddForProbation(UseGoodsListDto useGoodsListDto) {
        String jsonString = JSON.toJSONString(useGoodsListDto);
        //调用外部接口实现商品试用
        String returnString = HttpUtils.doRequest(server,probationGoods,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.PROBATION_GOODS_FAIL);
        }
        String returnTumString = this.underlineToTump(returnString);
        GoodsProbationListVo goodsProbationListVo = JSON.parseObject(returnTumString,GoodsProbationListVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(goodsProbationListVo.getStatus()))){
            throw new ServiceException(ResultCode.PROBATION_GOODS_FAIL);
        }
        return goodsProbationListVo;
    }

    /**
     * @Description: 调用外部接口实现商品停用
     * @Param:  UseGoodsListDto
     * @return: SimpleReturnVo
     * @Author: huxianghong
     * @Date: 2020/3/30 13:01
     */
    @Override
    public SimpleReturnVo doGoodsEnd(UseGoodsListDto useGoodsListDto) {
        String jsonString = JSON.toJSONString(useGoodsListDto);
        //调用外部接口实现商品停用
        String returnString = HttpUtils.doRequest(server,endGoods,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.END_GOODS_FAIL);
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(returnString,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.END_GOODS_FAIL);
        }
        return simpleReturnVo;
    }

    /**
     * @Description: 调用外部接口实现商品开通
     * @Param:  UseGoodsListDto
     * @return: GoodsOpenListVo
     * @Author: huxianghong
     * @Date: 2020/3/30 14:01
     */
    @Override
    public GoodsOpenListVo doAddForOpen(UseGoodsListDto useGoodsListDto) {
        String jsonString = JSON.toJSONString(useGoodsListDto);
        //调用外部接口实现商品开通
        String returnString = HttpUtils.doRequest(server,openGoods,PlatformContant.ReturnString.URL_PAT+jsonString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.OPEN_GOODS_FAIL);
        }
        String tumString = this.underlineToTump(returnString);
        GoodsOpenListVo goodsOpenListVo = JSON.parseObject(tumString,GoodsOpenListVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(goodsOpenListVo.getStatus()))){
            throw new ServiceException(ResultCode.OPEN_GOODS_FAIL);
        }
        return goodsOpenListVo;
    }

    /**
     * @Description: 调用外部接口查询首页多种数据
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/30 10:01
     */
    @Override
    public IndexDataVo findIndexData(String crmUserId) {
        String url = server+indexData+PlatformContant.ReturnString.URL_CUSTOMER_ID+crmUserId;
        String returnString = HttpUtils.restPostJson(url,"");
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_INDEX_DATA_FAIL);
        }
        IndexDataVo indexDataVo = JSON.parseObject(returnString,IndexDataVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(indexDataVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_INDEX_DATA_FAIL);
        }
        return indexDataVo;
    }

    @Override
    public TotalEvaluateVo findTotalEvaluate(GoodsDetailDto goodsDetailDto) {
        //查询点击量（关注度）
        DegreeDto degreeDto = new DegreeDto();
        degreeDto.setGoodsId(goodsDetailDto.getGoods_id());
        DegreeVo degreeVo = degreeMapper.selectDegree(degreeDto);
        //查询评价平均分
        EvaluateDto evaluateDto = new EvaluateDto();
        evaluateDto.setGoodsId(goodsDetailDto.getGoods_id());
        TotalEvaluateVo totalEvaluateVo = evaluateMapper.selectAvg(evaluateDto);
        totalEvaluateVo.setSumClicks(degreeVo.getClicks());
        //查询评价列表
//        List<EvaluateVo> evaluateList = evaluateMapper.selectEvaluate(evaluateDto);
//        goodsDetailReturnVO.getGoods().setEvaluateList(evaluateList);
        return totalEvaluateVo;
    }
}

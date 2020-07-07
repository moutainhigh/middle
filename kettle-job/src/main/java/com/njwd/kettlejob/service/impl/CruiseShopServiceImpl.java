package com.njwd.kettlejob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.njwd.common.Constant;
import com.njwd.common.ScheduleConstant;
import com.njwd.entity.kettlejob.dto.BaseShopRelaDto;
import com.njwd.entity.kettlejob.dto.PsItemDto;
import com.njwd.entity.kettlejob.dto.PsItemScoreDto;
import com.njwd.entity.kettlejob.vo.BaseShopRelaVo;
import com.njwd.entity.kettlejob.vo.PsItemScoreVo;
import com.njwd.entity.schedule.vo.TaskParamVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.kettlejob.service.CruiseShopService;
import com.njwd.kettlejob.service.TaskParamService;
import com.njwd.mapper.BaseShopRelaMapper;
import com.njwd.service.BaseShopRelaService;
import com.njwd.service.PsItemScoreService;
import com.njwd.service.PsItemService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.HttpUtils;
import com.njwd.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;


/**
 * @author jds
 * @Description 巡店
 * @Date 2019/11/6 14:12
 */
@Service
public class CruiseShopServiceImpl implements CruiseShopService {

    @Value("${cruiseShop.url.token}")
    private String token_url;
    @Value("${cruiseShop.url.item_list}")
    private String item_list;
    @Value("${cruiseShop.url.item_score}")
    private String item_score;
    @Value("${cruiseShop.url.shop_list}")
    private String shop_list;

    @Resource
    private BaseShopRelaMapper baseShopRelaMapper;
    @Resource
    private BaseShopRelaService baseShopRelaService;
    @Resource
    private PsItemService psItemService;
    @Resource
    private PsItemScoreService psItemScoreService;
    @Resource
    private TaskParamService taskParamService;

    /**
     * @Description 同步巡店门店数据到rela表
     * @Author jds
     * @Date 2019/11/12 16:38
     * @Param [mapParam] enteId:企业ID  appId：appID  id:  password:  code:
     * @return int
     **/
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public String addOrUpdateShop(String appId,String enteId,Map<String, Object> mapParam) {
        JSONObject taskResJson = new JSONObject();
        try{
            //访问巡店数据
            String text = getCruiseShop(shop_list,mapParam);
            if (StringUtils.isNotBlank(text)) {
                //新增门店集合
                List<BaseShopRelaDto> addList=new ArrayList<>();
                //修改门店集合
                List<BaseShopRelaDto> updateList=new ArrayList<>();
                BaseShopRelaDto baseShopRela;
                JSONObject json = JSONObject.parseObject(text);
                //获取的所有门店和区域
                List<Map<String, Object>> jsonArray = (List<Map<String, Object>>) json.get("data");
                //循环获取门店
                for(Map<String, Object> map : jsonArray){
                    //groupType为store的数据为门店
                    if(("store").equals(map.get("groupType").toString())){
                        baseShopRela=new BaseShopRelaDto();
                        setBaseShopRelaDto(appId,enteId,baseShopRela,map);
                        addList.add(baseShopRela);
                    }
                }
                //设置已有门店查询条件
                BaseShopRelaDto baseShopRelaDto=new BaseShopRelaDto();
                baseShopRelaDto.setAppId(appId);
                baseShopRelaDto.setEnteId(enteId);
                //查询的所有已有门店
                List<BaseShopRelaVo> existShopList=baseShopRelaMapper.findBaseShopRelaBatch(baseShopRelaDto);
                if(existShopList!=null && existShopList.size()> Constant.Number.ZERO){
                    Map<String, BaseShopRelaVo> existShopMap = new HashMap<>();
                    for(BaseShopRelaVo baseShopRelaVo:existShopList){
                        String third_shop_id = baseShopRelaVo.getThirdShopId();
                        existShopMap.put(third_shop_id,baseShopRelaVo);
                    }
                    List<BaseShopRelaDto> shopRelaList=new ArrayList<>();
                    shopRelaList.addAll(addList);
                    //判断门店数据是否已经存在  已存在数据是否变更
                    for(BaseShopRelaDto shopRelaDto : shopRelaList){
                        String third_shop_id = shopRelaDto.getThirdShopId();
                        //判断第三方id是否存在
                        if(existShopMap.containsKey(third_shop_id)&&existShopMap.get(third_shop_id)!=null){
                            //去除新增门店中ID相同的数据
                            addList.remove(shopRelaDto);
                            //id相同编码和名称不同添加修改门店
                            updateList.add(shopRelaDto);
                        }
                    }
                }
                if(updateList.size()> Constant.Number.ZERO){
                    //批量修改
                    baseShopRelaMapper.updateBaseShopRela(updateList);
                }
                if(addList.size()> Constant.Number.ZERO){
                    //批量新增
                    baseShopRelaMapper.addBaseShopRela(addList);
                }
            }
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        taskResJson.put(Constant.TaskResult.PARAM,Constant.Character.NULL_VALUE);
        taskResJson.put(Constant.TaskResult.MSG,Constant.Character.NULL_VALUE);
        return taskResJson.toString();
    }


    /**
     * @Description 同步巡店项目数据
     * @Author jds
     * @Date 2019/11/13 11:46
     * @Param [mapParam]
     * @return int
     **/
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public  String addOrUpdateItem(String appId,String enteId,Map<String, Object> mapParam) {
        JSONObject taskResJson = new JSONObject();
        try{
            String text = getCruiseShop(item_list,mapParam);
            System.out.println("----------------test="+text);
            if (StringUtils.isNotBlank(text)) {
                //新增集合
                List<PsItemDto> replaceList=new ArrayList<>();
                PsItemDto psItem;
                JSONObject json = JSONObject.parseObject(text);
                JSONObject jsonList =JSONObject.parseObject(json.get("data").toString());
                //获取的所有门店和区域
                List<Map<String, Object>> jsonArray = (List<Map<String, Object>>) jsonList.get("items");
                if(jsonArray.size()>Constant.Number.ZERO){
                    for(Map<String, Object>map:jsonArray) {
                        //设置数据
                        psItem = new PsItemDto();
                        setPsItemDto(appId,enteId,psItem, map);
                        replaceList.add(psItem);
                    }
                }
                if(replaceList.size()> Constant.Number.ZERO){
                    //批量新增修改
                    psItemService.replacePsItem(replaceList);
                }
            }
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        taskResJson.put(Constant.TaskResult.PARAM,Constant.Character.NULL_VALUE);
        taskResJson.put(Constant.TaskResult.MSG,Constant.Character.NULL_VALUE);
        return taskResJson.toString();
    }



    /**
     * @Description //同步巡店项目得分
     * @Author jds
     * @Date 2019/11/14 14:42
     * @Param [mapParam]
     * @return int
     **/
    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public  String addOrUpdateItemScore(String appId,String enteId,Map<String, Object> mapParam) {
        JSONObject taskResJson = new JSONObject();
        try{
            PsItemScoreDto psItemScoreDto = new PsItemScoreDto();
            psItemScoreDto.setAppId(appId);
            psItemScoreDto.setEnteId(enteId);
//            String currentTime = mapParam.containsKey(ScheduleConstant.AppInterface.STRATTIME)&&StringUtil.isNotBlank(mapParam.get(ScheduleConstant.AppInterface.STRATTIME).toString())
//                    ?mapParam.get(ScheduleConstant.AppInterface.STRATTIME).toString():DateUtils.getCurrentDate(DateUtils.PATTERN_MINUTE);
            String currentTime =DateUtils.getCurrentDate(DateUtils.PATTERN_MINUTE);
            //设置开始、结束时间
            Date currentDate = DateUtils.parseDate(currentTime,DateUtils.PATTERN_DAY);
            //开始时间为当前日期的前15天
            Date startDate = DateUtils.subDays(currentDate,15);
            String startTime = DateUtils.format(startDate,DateUtils.PATTERN_DAY);
            mapParam.put(Constant.CruiseShop.START,startTime);
            mapParam.put(Constant.CruiseShop.END,currentTime);
            //访问巡店数据
            String text = getCruiseShop(item_score,mapParam);
            System.out.println("---------test="+text);
            if(StringUtils.isNotBlank(text)) {
                List<PsItemScoreDto> list = new ArrayList<>();
                PsItemScoreDto psItemScore;
                JSONObject json = JSONObject.parseObject(text);
                JSONObject jsonList = JSONObject.parseObject(json.get("data").toString());
                //获取的所有门店数据
                List<Map<String, Object>> jsonArray = (List<Map<String, Object>>) jsonList.get("items");
                if(jsonArray.size()>Constant.Number.ZERO){

                    psItemScoreDto.setStartDay(startTime);
                    psItemScoreDto.setEndDay(currentTime);
                    List<PsItemScoreVo> psItemScoreList = psItemScoreService.findPsItemScoreBatch(psItemScoreDto);
                    Map<String,PsItemScoreVo> itemScoreMap = new HashMap<>();
                    if(psItemScoreList!=null&&psItemScoreList.size()>Constant.Number.ZERO){
                        for(PsItemScoreVo itemScoreVo : psItemScoreList){
                            itemScoreMap.put(itemScoreVo.getItemId(),itemScoreVo);
                        }
                    }

                    for (Map<String, Object> map : jsonArray) {
                        //设置数据
                        psItemScore = new PsItemScoreDto();
                        String itemScoreId =  map.get("_id").toString();
                        //原数据shopId赋值给新的数据（replace into 会delete再insert,此该步操作使已清洗的数据不会丢失）
                        if(itemScoreMap.size()<Constant.Number.ZERO){
                            PsItemScoreVo psItemScoreOld = itemScoreMap.get(itemScoreId);
                            if(psItemScoreOld!=null&&psItemScoreOld.getShopId()!=null){
                                psItemScore.setShopId(psItemScoreOld.getShopId());
                            }
                        }
                        setPsItemScoreDto(appId,enteId,psItemScore, map);
                        list.add(psItemScore);
                    }
                }
                if(list.size()>Constant.Number.ZERO){
                    //新增并修改
                    psItemScoreService.replaceScore(list);
                }
                //时间转换为 yyyy-MM-dd HH:mm
                String last_udpate_time = psItemScoreService.findMaxLastUpdateTime(psItemScoreDto);
                Date last_udpate_date = DateUtils.parseDate(last_udpate_time,DateUtils.PATTERN_MINUTE);
                String clean_time = DateUtils.format(last_udpate_date,DateUtils.PATTERN_MINUTE);
                //当前任务类型
                mapParam.put(ScheduleConstant.AppInterface.BUSINESSTYPE,ScheduleConstant.BusinessType.PULL);
                //变更清洗消费流水任务的时间
                mapParam.put(ScheduleConstant.AppInterface.STRATTIME,clean_time);
                taskParamService.updateTaskParamBatch(appId,enteId,mapParam);
            }
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            taskResJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        taskResJson.put(Constant.TaskResult.PARAM,Constant.Character.NULL_VALUE);
        taskResJson.put(Constant.TaskResult.MSG,Constant.Character.NULL_VALUE);
        return taskResJson.toString();
    }

    /**
     * 清洗得分记录数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cleanItemScore(String appId,String enteId,Map<String, Object> mapParam){
        JSONObject resultJson = new JSONObject();
        try{
            //查询清洗数据任务的执行时间
            TaskParamVo taskParamVo = taskParamService.findTaskParam(enteId,mapParam);
            Date last_update_time = DateUtils.parseDate(taskParamVo.getParam(),DateUtils.PATTERN_DAY);
            //设置条件
            PsItemScoreDto psItemScoreDto=new PsItemScoreDto();
            psItemScoreDto.setEnteId(enteId);
            psItemScoreDto.setAppId(appId);
            psItemScoreDto.setLastUpdateTime(last_update_time);
            //洗数据
            psItemScoreService.cleanScore(psItemScoreDto);
            List<Map<String,Object>> unCleanCodeList = psItemScoreService.findUnCleanCode(psItemScoreDto);
            String unCleanCode = getUnCleanCode(unCleanCodeList);
            //存在未清洗干净的数据则返回未清洗的基础数据标识
//            resultJson.put(Constant.TaskResult.PARAM,unCleanCode);
//            resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
            //当前任务类型
            mapParam.put(ScheduleConstant.AppInterface.BUSINESSTYPE,ScheduleConstant.BusinessType.CLEAN);
            //全部清洗，则变更wd_task_param该条清洗记录的status为1，并变更迁移任务的时间
            mapParam.put(ScheduleConstant.AppInterface.UNCLEANCODE,unCleanCode);
            //变更迁移任务的时间
            String migrate_time = DateUtils.format(last_update_time,DateUtils.PATTERN_MINUTE);
            mapParam.put(ScheduleConstant.AppInterface.STRATTIME,migrate_time);
            //变更迁移任务参数
            taskParamService.updateTaskParamBatch(appId,enteId,mapParam);
            resultJson.put(Constant.TaskResult.PARAM,unCleanCode);
            resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.SUCCESS);
        }catch(Exception e){
            e.printStackTrace();
            resultJson.put(Constant.TaskResult.STATUS,Constant.ReqResult.FAIL);
        }
        resultJson.put(Constant.TaskResult.MSG,Constant.Character.NULL_VALUE);
        return resultJson.toString();

    }

    /**
     * @Description //登录获取巡店数据
     * @Author jds
     * @Date 2019/11/14 9:59
     * @Param [url, dataUrl, mapParam]
     * @return java.lang.String
     **/
    @SuppressWarnings("unchecked")
    private String getCruiseShop(String dataUrl,Map<String, Object> mapParam){
        String text=null;
        try {
            Map<String, String> param = new HashMap<>();
            param.put(Constant.CruiseShop.ID,mapParam.get(ScheduleConstant.AppInterface.USERNAME).toString());
            param.put(Constant.CruiseShop.PASSWORD, mapParam.get(ScheduleConstant.AppInterface.PASSWORD).toString());
            param.put(Constant.CruiseShop.CODE, mapParam.get(ScheduleConstant.AppInterface.CODE).toString());
            //获取cookie
            String tmpcookies = HttpUtils.restGetCookie(mapParam.get(Constant.CruiseShop.URL).toString()+token_url,String.class,param);
            //判断入参是否有时间范围
            JSONObject jsonParam =new JSONObject();
            if(StringUtil.isNotBlank(mapParam.get(Constant.CruiseShop.START))) {
                //设置入参时间范围  带条件查（获取巡店评分）
                jsonParam.put(Constant.CruiseShop.START, mapParam.get(Constant.CruiseShop.START).toString());
                jsonParam.put(Constant.CruiseShop.END, mapParam.get(Constant.CruiseShop.END).toString());
            }
            String can = jsonParam.toJSONString();
            //访问巡店信息
            text=HttpUtils.sendGet(mapParam.get(ScheduleConstant.AppInterface.URL).toString()+dataUrl+Constant.CruiseShop.CONDITION+ URLEncoder.encode(can,Constant.Character.UTF8),tmpcookies);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject resultJson = JSONObject.parseObject(text);
        if(resultJson.containsKey(Constant.Exception.ERROR)){
            throw new ServiceException(ResultCode.CRUISE_SHOP_PORT_ERROR,mapParam.get(ScheduleConstant.AppInterface.URL).toString()+dataUrl);
        }
        return text;
    }


    /**
     * @Description //设置巡店项目 PsItemDto数据
     * @Author jds
     * @Date 2019/11/18 15:23
     * @Param [psItemDto, mapParam, map]
     * @return void
     **/
    private void setPsItemDto(String appId,String enteId,PsItemDto psItemDto ,Map<String, Object>map){
        //设置数据
        psItemDto.setEnteId(enteId);
        psItemDto.setAppId(appId);
        psItemDto.setItemId(map.get("_id").toString());
        psItemDto.setItemName(map.get("name").toString());
        psItemDto.setActive(Boolean.parseBoolean(map.get("active").toString()));
        psItemDto.setTypeId(map.get("id").toString());
        psItemDto.setLastUpdateTime(DateUtils.parseDateUtc(map.get("updateAt").toString()));
    }


    /**
     * @Description //设置巡店分数 psItemScoreDto数据
     * @Author jds
     * @Date 2019/11/18 15:23
     * @Param [psItemDto, mapParam, map]
     * @return void
     **/
    private void setPsItemScoreDto(String appId,String enteId,PsItemScoreDto psItemScoreDto ,Map<String, Object>map){
            //设置数据
            psItemScoreDto.setEnteId(enteId);
            psItemScoreDto.setAppId(appId);
            psItemScoreDto.setItemScoreId(map.get("_id").toString());
            psItemScoreDto.setThirdShopId(map.get("store").toString());
            psItemScoreDto.setRealScore( new BigDecimal(map.get("realScore").toString()));
            psItemScoreDto.setScore(new BigDecimal(map.get("score").toString()));
            psItemScoreDto.setItemId(map.get("form").toString());
            psItemScoreDto.setItemName(map.get("name").toString());
            psItemScoreDto.setScoreDay(map.get("day").toString());
            psItemScoreDto.setLastUpdateTime(DateUtils.parseDateFormatUtc(map.get("updateAt").toString()));
    }


    /**
     * @Description //设置巡店门店 PsItemScoreDto数据
     * @Author jds
     * @Date 2019/11/18 15:23
     * @Param [psItemDto, mapParam, map]
     * @return void
     **/
    private void setBaseShopRelaDto(String appId,String enteId,BaseShopRelaDto baseShopRelaDto ,Map<String, Object>map){
        //设置数据
        baseShopRelaDto.setThirdShopId(map.get("_id").toString());
        baseShopRelaDto.setShopName(map.get("name").toString());
        baseShopRelaDto.setShopNo(map.get("outer").toString());
        baseShopRelaDto.setThirdEnteId(enteId);
        baseShopRelaDto.setEnteId(enteId);
        baseShopRelaDto.setAppId(appId);
    }

    /**
     * 未清洗标识
     * @param unCleanCodeList
     * @return
     */
    private String getUnCleanCode(List<Map<String,Object>> unCleanCodeList){
        JSONObject resParam = new JSONObject();
        for (Map<String,Object> map :unCleanCodeList){
            if(!"".equals(map.get("shop").toString())){
                resParam.put(Constant.TaskResult.SHOPCODE,Constant.Number.ONE);
            }
        }
        return resParam.toString();
    }


}

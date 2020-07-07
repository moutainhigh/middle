package com.njwd.reportdata.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.common.Constant;
import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.basedata.excel.ExcelError;
import com.njwd.entity.basedata.excel.ExcelRowData;
import com.njwd.entity.reportdata.dto.FinReportConfigDto;
import com.njwd.entity.reportdata.vo.SettingEntryFreeVo;
import com.njwd.entity.reportdata.vo.fin.FinReportConfigVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.fileexcel.check.SampleExcelCheck;
import com.njwd.report.mapper.FinReportConfigMapper;
import com.njwd.reportdata.controller.UserController;
import com.njwd.reportdata.service.FinReportConfigService;
import com.njwd.utils.DateUtils;
import com.njwd.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 财务报表配置ServiceImpl
 * @Author 周鹏
 * @Date 2020/04/20
 */
@Service
public class FinReportConfigServiceImpl implements FinReportConfigService {
    @Resource
    private FinReportConfigMapper finReportConfigMapper;

    @Resource
    private UserController userController;

    /**
     * 查询配置信息列表
     *
     * @param param
     * @return FinReportConfigVo
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @Override
    public Page<FinReportConfigVo> findFinReportConfigList(FinReportConfigDto param) {
        return finReportConfigMapper.findFinReportConfigList(param.getPage(), param);
    }

    /**
     * 根据id查询配置信息
     *
     * @param param
     * @return FinReportConfigVo
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @Override
    public FinReportConfigVo findFinReportConfigById(FinReportConfigDto param) {
        FinReportConfigVo finReportConfigVo = finReportConfigMapper.findFinReportConfigById(param);
        if (finReportConfigVo == null) {
            throw new ServiceException(ResultCode.RECORD_NOT_EXIST);
        }
        return finReportConfigVo;
    }

    /**
     * 新增配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addInfo(FinReportConfigDto param) {
        //校验是否重复
        if (!checkIfExist(param)) {
            throw new ServiceException(ResultCode.FIN_REPORT_CONFIG_EXIST);
        }
        List<FinReportConfigDto> list = new ArrayList<>();
        list.add(param);
        return finReportConfigMapper.addInfo(list);
    }

    /**
     * 根据id更新配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateInfoById(FinReportConfigDto param) {
        //校验是否重复
        if (!checkIfExist(param)) {
            throw new ServiceException(ResultCode.FIN_REPORT_CONFIG_EXIST);
        }
        return finReportConfigMapper.updateInfo(param);
    }

    /**
     * 批量删除配置信息
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    @Override
    public Integer deleteInfo(FinReportConfigDto param) {
        return finReportConfigMapper.deleteInfo(param);
    }

    /**
     * 校验重复数据
     *
     * @param param
     * @return Integer
     * @Author 周鹏
     * @Data 2020/04/20 15:46
     */
    private Boolean checkIfExist(FinReportConfigDto param) {
        Integer result = finReportConfigMapper.checkIfExist(param);
        if (result > 0) {
            return false;
        }
        return true;
    }

    /**
     * 校验导入的配置信息
     *
     * @param excelData
     * @return void
     * @Author 周鹏
     * @Data 2020/04/22 15:46
     */
    @Override
    public void checkData(ExcelData excelData) {
        //step1:校验空值、格式和长度
        SampleExcelCheck.checkNull(excelData, new int[]{0, 0, 0, 0, 0});
        SampleExcelCheck.checkFormat(excelData, new int[]{0, 0, 0, 0, 0});
        SampleExcelCheck.checkLength(excelData, new int[]{50, 50, 1000, 1000, 200});
        List<ExcelRowData> excelRowDataList = excelData.getExcelRowDataList();
        if (CollectionUtils.isNotEmpty(excelRowDataList)) {
            //step2:根据finGroup和finType校验excel数据是否有重复
            List<String> checkList = new ArrayList<>();
            List<String> duplicateList = new ArrayList<>();
            excelRowDataList.stream().filter(
                    v -> {
                        boolean flag = !checkList.contains(v.getExcelCellDataList().get(0).getData() + Constant.Character.COMMA + v.getExcelCellDataList().get(1).getData());
                        if (!flag) {
                            if (!duplicateList.contains(v.getExcelCellDataList().get(0).getData() + Constant.Character.COMMA + v.getExcelCellDataList().get(1).getData())) {
                                duplicateList.add(v.getExcelCellDataList().get(0).getData() + Constant.Character.COMMA + v.getExcelCellDataList().get(1).getData());
                            }
                        } else {
                            checkList.add(v.getExcelCellDataList().get(0).getData() + Constant.Character.COMMA + v.getExcelCellDataList().get(1).getData());
                        }
                        return flag;
                    }
            ).collect(Collectors.toList());
            //如果有重复数据,抛异常
            if (CollectionUtils.isNotEmpty(duplicateList)) {
                throw new ServiceException(ResultCode.FIN_REPORT_CONFIG_DUPLICATE, duplicateList);
            }
            //step3:根据finGroup和finType过滤已存在的数据
            List<ExcelRowData> existList = new ArrayList<>();
            FinReportConfigDto param = new FinReportConfigDto();
            ExcelError excelError = new ExcelError();
            excelError.setErrorMsg(ResultCode.FIN_REPORT_CONFIG_DUPLICATE.message);
            param.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
            excelRowDataList.forEach(excelRowData -> {
                param.setFinGroup(excelRowData.getExcelCellDataList().get(0).getData().toString());
                param.setFinType(excelRowData.getExcelCellDataList().get(1).getData().toString());
                Integer result = finReportConfigMapper.checkIfExist(param);
                if (result > 0) {
                    existList.add(excelRowData);
                    excelData.getExcelErrorList().add(excelError);
                }
            });
            excelRowDataList.removeAll(existList);
        }
    }

    /**
     * 批量导入配置信息
     *
     * @param excelData
     * @return void
     * @Author 周鹏
     * @Data 2020/04/22 15:46
     */
    @Override
    public Integer insertExcelBatch(ExcelData excelData) {
        List<FinReportConfigDto> dataList = new ArrayList<>();
        FinReportConfigDto data;
        List<ExcelCellData> cellDataList;
        for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
            cellDataList = excelData.getExcelRowDataList().get(i).getExcelCellDataList();
            data = new FinReportConfigDto();
            data.setEnteId(userController.getCurrLoginUserInfo().getRootEnterpriseId().toString());
            data.setFinGroup(cellDataList.get(0).getData().toString());
            data.setFinType(cellDataList.get(1).getData().toString());
            data.setCodes(cellDataList.get(2).getData().toString());
            data.setCodesType(cellDataList.get(3).getData().toString());
            data.setMem(cellDataList.get(4).getData().toString());
            dataList.add(data);
        }
        return finReportConfigMapper.addInfo(dataList);
    }
}

package com.njwd.admin.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.njwd.admin.mapper.MasterDataSwitchMapper;
import com.njwd.admin.service.MasterDataSwitchService;
import com.njwd.common.AdminConstant;
import com.njwd.common.Constant;
import com.njwd.entity.admin.DataMap;
import com.njwd.entity.admin.dto.MasterDataSwitchDto;
import com.njwd.entity.admin.dto.MasterDataUpdateDto;
import com.njwd.entity.admin.dto.PrimaryRelyDto;
import com.njwd.entity.admin.dto.TableBackupDto;
import com.njwd.entity.admin.vo.PrimaryRelyVo;
import com.njwd.exception.ResultCode;
import com.njwd.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 主数据切换
 *
 * @author XiaFq
 * @date 2020/1/3 4:21 下午
 */
@Service
public class MasterDataSwitchServiceImpl implements MasterDataSwitchService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MasterDataSwitchServiceImpl.class);

    @Resource
    private MasterDataSwitchMapper masterDataSwitchMapper;

    /**
     * 验证数据依赖
     *
     * @param masterDataSwitchDto
     * @return Result
     * @author XiaFq
     * @date 2020/1/3 4:23 下午
     */
    @Override
    public ResultCode checkDataRely(MasterDataSwitchDto masterDataSwitchDto) {
        String dataType = masterDataSwitchDto.getDataType();
        String baseTableName = AdminConstant.Character.BASE_TABLE_START + dataType;
        String relyTableName = AdminConstant.Character.BASE_TABLE_START + dataType + AdminConstant.Character.RELA_TABLE_END;
        masterDataSwitchDto.setBaseTableName(baseTableName);
        masterDataSwitchDto.setRelyTableName(relyTableName);
        String baseId = dataType + AdminConstant.Character.END_ID;
        masterDataSwitchDto.setBaseId(baseId);
        masterDataSwitchDto.setRelyId(baseId);
        // 切换主数据是否全匹配
        int countDataRelyBase = masterDataSwitchMapper.countDataRelyBase(masterDataSwitchDto);
        if (countDataRelyBase > 0) {
            return ResultCode.MASTER_DATA_RELY_ERROR;
        }
        // 主数据依赖是否全匹配
        PrimaryRelyDto primaryRelyDto = new PrimaryRelyDto();
        primaryRelyDto.setDataType(masterDataSwitchDto.getDataType());
        primaryRelyDto.setType(0);
        ResultCode resultCode = null;
        resultCode = isResult(masterDataSwitchDto, ResultCode.RELY_DATA_NOT_MATCH_BASE, primaryRelyDto);

        // 业务数据依赖
        if (resultCode == null) {
            primaryRelyDto.setType(1);
            resultCode = isResult(masterDataSwitchDto, ResultCode.BUS_DATA_NOT_MATCH_BASE, primaryRelyDto);
        } else {
            return resultCode;
        }

        // 数据映射依赖
        if (resultCode == null) {
            List<DataMap> dataMapList = masterDataSwitchMapper.getDataMapList(masterDataSwitchDto);
            if (!CollectionUtils.isEmpty(dataMapList)) {
                dataMapList.stream().forEach(dataMap -> {
                    masterDataSwitchDto.setRelyTableName(dataMap.getDatamapTable());
                    String sourceTableKey = dataMap.getSourceTableKey();
                    StringBuffer onCondition = new StringBuffer();
                    if (StringUtil.isNotEmpty(sourceTableKey)) {
                        String[] keys = sourceTableKey.split(Constant.Character.COMMA);
                        for (String key : keys) {
                            onCondition.append(AdminConstant.Character.B).append(Constant.Character.POINT).append(key).
                                    append(AdminConstant.Character.EQUALS_SPACE).append(AdminConstant.Character.C).
                                    append(Constant.Character.POINT).append(AdminConstant.Character.SOURCE).append(key).append(AdminConstant.Character.AND);
                        }
                        onCondition.append(AdminConstant.Character.TRUE_CONDITION);
                        masterDataSwitchDto.setOnCondition(onCondition.toString());
                    }
                });
                int dataMappingCount = masterDataSwitchMapper.countDataMappingRelyBase(masterDataSwitchDto);
                if (dataMappingCount > 0) {
                    resultCode = ResultCode.DATA_MAPPING_NOT_MATCH_BASE;
                }
            }
        }

        if (resultCode != null) {
            return resultCode;
        }
        return null;
    }

    /**
     * 检测主数据和业务数据的依赖
     *
     * @param masterDataSwitchDto
     * @param primaryRelyDto
     * @return boolean
     * @author XiaFq
     * @date 2020/1/4 3:31 下午
     */
    private ResultCode isResult(MasterDataSwitchDto masterDataSwitchDto, ResultCode resultCode, PrimaryRelyDto primaryRelyDto) {
        ResultCode returnCode = null;
        List<PrimaryRelyVo> primaryRelyVoList = masterDataSwitchMapper.getRelyData(primaryRelyDto);
        if (!CollectionUtils.isEmpty(primaryRelyVoList)) {
            for (PrimaryRelyVo vo : primaryRelyVoList) {
                masterDataSwitchDto.setRelyTableName(vo.getRelaTable());
                int count = masterDataSwitchMapper.countDataRelyBase(masterDataSwitchDto);
                if (count > 0) {
                    returnCode = resultCode;
                    break;
                }
            }
        }
        return returnCode;
    }

    /**
     * 通过中台id批量更新base表数据
     *
     * @param masterDataUpdateDto
     * @return ResultCode
     * @author XiaFq
     * @date 2020/1/4 1:32 下午
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public int batchUpdateByBaseId(MasterDataUpdateDto masterDataUpdateDto) {
        try {
            // 执行表备份
            TableBackupDto backupDto = new TableBackupDto();
            backupDto.setEnteId(masterDataUpdateDto.getEnteId());
            String dataType = masterDataUpdateDto.getDataType();
            String baseTableName = AdminConstant.Character.BASE_TABLE_START + dataType;
            String relyTableName = AdminConstant.Character.BASE_TABLE_START + dataType + AdminConstant.Character.RELA_TABLE_END;
            masterDataUpdateDto.setBaseTableName(baseTableName);
            masterDataUpdateDto.setRelyTableName(relyTableName);
            String baseId = dataType + AdminConstant.Character.END_ID;
            backupDto.setSourceTableName(baseTableName);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String backupTableName = baseTableName + Constant.Character.UNDER_LINE + sdf.format(new Date())
                    + Constant.Character.UNDER_LINE + masterDataUpdateDto.getOldAppId() + Constant.Character.UNDER_LINE + AdminConstant.Character.BAK;
            backupDto.setBackupTableName(backupTableName);
            masterDataUpdateDto.setBaseId(baseId);
            // 保证事务的一致性，利用cglib动态代理来调用
            ((MasterDataSwitchService) AopContext.currentProxy()).backupTable(backupDto);

            // 查询rely字段
            MasterDataSwitchDto dto = new MasterDataSwitchDto();
            dto.setBaseTableName(masterDataUpdateDto.getBaseTableName());
            String relyColumns = masterDataSwitchMapper.selectRelyColumns(dto);
            masterDataUpdateDto.setRelyColumns(relyColumns);
            // 查询需要更新的字段
            dto.setBaseId(masterDataUpdateDto.getBaseId());
            String updateColumns = masterDataSwitchMapper.selectUpdateColumns(dto);
            masterDataUpdateDto.setUpdateColumns(updateColumns);
            // 批量更新
            masterDataSwitchMapper.batchUpdateById(masterDataUpdateDto);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("batchUpdateByBaseId is error,{}", e.getMessage());
            return AdminConstant.Number.UPDATE_FAILED;
        }
        return AdminConstant.Number.UPDATE_SUCCESS;
    }

    /**
     * 表数据备份
     *
     * @param tableBackupDto
     * @return
     * @author XiaFq
     * @date 2020/1/6 10:38 上午
     */
    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    public void backupTable(TableBackupDto tableBackupDto) {
        // 备份表
        masterDataSwitchMapper.copyTable(tableBackupDto);
        masterDataSwitchMapper.copyData(tableBackupDto);
        // 为了保证数据的一致性 必须先备份完原始数据再做更新操作
//        ThreadPoolUtil.exec(new DoBackupThread(tableBackupDto));
    }


    /**
     * 重启线程执行备份
     *
     * @author XiaFq
     * @date 2020/1/6 10:26 上午
     */
    class DoBackupThread implements Runnable {
        private TableBackupDto tableBackupDto;

        public DoBackupThread(TableBackupDto tableBackupDto) {
            this.tableBackupDto = tableBackupDto;
        }

        @Override
        public void run() {
            masterDataSwitchMapper.copyTable(tableBackupDto);
            masterDataSwitchMapper.copyData(tableBackupDto);
        }
    }
}

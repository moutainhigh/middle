package com.njwd.service.impl;

import com.njwd.common.Constant;
import com.njwd.entity.basedata.KettleInfo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.service.KettleService;
import com.njwd.utils.FilePathUtil;
import com.njwd.utils.KettleUtils;
import com.njwd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2019/10/30 14:20
 */
@Service
public class KettleServiceImpl implements KettleService {

    @Value("${kettle.script.path}")
    private String dirPath;

    @Value("${kettle.script.propertiesFilePath}")
    private String propertiesFilePath;

    /**
     * 执行kettle文件
     * @param kettleInfo
     * @param kettleType
     * @return
     */
    @Override
    public String runKettle(KettleInfo kettleInfo,String kettleType) {
        if(null == kettleInfo ){
            throw new ServiceException(ResultCode.KETTLE_INFO_NOT_EXIST);
        }
        if(StringUtil.isBlank(kettleInfo.getFileName())){
            throw new ServiceException(ResultCode.KETTLE_FILE_NOT_EXIST);
        }
        String fileName = dirPath + File.separator + FilePathUtil.getRealFilePath(kettleInfo.getFileName());
        kettleInfo.setFileName(fileName);
        if(StringUtil.isNotBlank(propertiesFilePath)){
            kettleInfo.setPropertiesFilePath(FilePathUtil.getRealFilePath(propertiesFilePath));
        }
        String flag;
        if (Constant.KettleType.ktr.equals(kettleType)){
            flag = KettleUtils.runKettleTransfer(kettleInfo);
        }else{
            flag = KettleUtils.runKettleJob(kettleInfo);
        }
        return flag;
    }

}

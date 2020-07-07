package com.njwd.entity.basedata.excel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * excel校验或导入的结果
 *
 * @param:
 * @return:
 * @author: zhuzs
 * @date: 2019-12-17
 */
@Getter
@Setter
public class ExcelResult implements Serializable {

    /**
     * 是否全部校验或导入成功
     */
    private int isOk;
    private String message;
    private String uuid;

}

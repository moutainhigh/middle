package com.njwd.support;


import com.njwd.entity.basedata.ReferenceDescription;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description 批量处理返回实体
 * @Author 朱小明
 * @Date 2019/7/4 9:00
 **/
@Data
public class BatchResult {

    /**
     * 失败详情List
     **/
    private List<ReferenceDescription> failList = new ArrayList<>();

    /**
     * 成功详情List,默认为空
     **/
    private List<String> successList = Collections.emptyList();

    /**
     * 成功详情List
     */
    private List<ReferenceDescription> successDetailsList = new ArrayList<>();

}

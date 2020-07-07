package com.njwd.entity.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author XiaFq
 * @Description DataMatchBatchDto TODO
 * @Date 2019/11/21 10:45 上午
 * @Version 1.0
 */
@Data
public class DataMatchBatchDto {
    /**
     * appId
     */
    private String appId;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 中台id
     */
    private String midPlatId;

    /**
     * 第三方id
     */
    private String thirdId;

    /**
     * 匹配id数组
     */
    private List<DataMatchBatchIdsDto> addList;

    /**
     * 解绑id数组
     */
    private List<DataMatchBatchIdsDto> deleteList;

    /**
     * 是否更新标识 0:更新 1代表不更新
     */
    private String updateFlag;

    /**
     * 用户id
     */
    private String userId;

}

package com.njwd.entity.kettlejob.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *@description: 个性化实体
 *@author: fancl
 *@create: 2020-02-29 
 */
@Getter
@Setter
public class TransferPersonalDto implements Serializable {
    //科目code
    private String accountSubjectCode;
    //摘要内容
    private String content;

}

package com.njwd.admin.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 *@description:
 *@author: fancl
 *@create: 2020-01-01 
 */
@Data
public class PriceDto {
    //企业id
    String enterpriseId;
    //准则码
    String benchmarkCode;
}

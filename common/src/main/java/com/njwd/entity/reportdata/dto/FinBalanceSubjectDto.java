package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.vo.FinBalanceSubjectVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author lj
 * @Description 科目余额表dto
 * @Date:15:31 2020/1/10
 **/
@Getter
@Setter
public class FinBalanceSubjectDto extends FinBalanceSubjectVo {
    private List<String> subList;

    private String flag;
}

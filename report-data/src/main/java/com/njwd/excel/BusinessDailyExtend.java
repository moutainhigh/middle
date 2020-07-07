package com.njwd.excel;

import com.njwd.annotation.ExcelExtend;
import com.njwd.common.Constant;
import com.njwd.entity.reportdata.BusinessDailyIndic;
import com.njwd.fileexcel.check.CheckContext;
import com.njwd.fileexcel.extend.AddExtend;
import com.njwd.fileexcel.extend.CheckExtend;
import com.njwd.reportdata.mapper.BusinessDailyIndicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhuzs  TODO 该类为测试用途，后期需根据业务需求完善
 * @date 2019-12-17 17:48
 */
@Component
@ExcelExtend(type = Constant.TemplateType.BUSINESS_DAILY)
public class BusinessDailyExtend implements AddExtend<BusinessDailyIndic>, CheckExtend {
    @Autowired
    private BusinessDailyIndicMapper businessDailyIndicMapper;

    @Override
    public int addBatch(List<BusinessDailyIndic> datas) {

        for (BusinessDailyIndic ele : datas) {
            ele.setDailyIndicId(String.valueOf(Math.random()));
        }
        System.out.println(" datas:" + datas.toString());
        return businessDailyIndicMapper.addBatch(datas);
    }

    @Override
    public int add(BusinessDailyIndic data) {
        return 0;
    }

    @Override
    public void check(CheckContext checkContext) {



    }
}


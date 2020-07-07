package com.njwd.entity.reportdata;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
* @Description: 入库单明细
* @Author: LuoY
* @Date: 2020/3/27 16:32
*/
@Data
public class ScmInstockEntry {
    /**
    * 应用id
    */
    private String appId;

    /**
    * 企业id
    */
    private String enteId;

    /**
    * 分录Id
    */
    private String entryid;

    /**
    * 单据id(父)
    */
    private String instockId;

    /**
    * 物料id
    */
    private String materialId;

    /**
    * 单位id
    */
    private String unitId;

    /**
    * 应收数量
    */
    private BigDecimal mustqty;

    /**
    * 实收数量 
    */
    private BigDecimal realqty;

    /**
    * 仓库id
    */
    private String stockId;

    /**
    * 库存状态 
    */
    private Short stockstatusid;

    /**
    * 基本单位id
    */
    private String baseUnitId;

    /**
    * 基本单位数量
    */
    private BigDecimal baseunitqty;

    /**
    * 毛重
    */
    private BigDecimal grossweight;

    /**
    * 净重
    */
    private BigDecimal netweight;

    /**
    * 订单单号
    */
    private String poorderno;

    /**
    * 合同单号 
    */
    private String contractno;

    /**
    * 收货仓库id
    */
    private String receiveStockId;

    /**
    * 基本价格
    */
    private BigDecimal baseprice;

    /**
    * 关联应付数量（基本单位）
    */
    private BigDecimal baseapjoinqty;

    /**
    * 单价
    */
    private BigDecimal price;

    /**
    * 含税单价
    */
    private BigDecimal taxprice;

    /**
    * 计价单位
    */
    private String priceUnitId;

    /**
    * 计价数量
    */
    private BigDecimal priceunitqty;

    /**
    * 税组合
    */
    private Short taxcombination;

    /**
    * 税率
    */
    private BigDecimal taxrate;

    /**
    * 税额
    */
    private BigDecimal taxamount;

    /**
    * 税额（本位币）
    */
    private BigDecimal taxamountLc;

    /**
    * 价格系数
    */
    private BigDecimal pricecoefficient;

    /**
    * 折扣率
    */
    private BigDecimal discountrate;

    /**
    * 折扣额
    */
    private BigDecimal discount;

    /**
    * 整单费用分摊
    */
    private BigDecimal billcostapportion;

    /**
    * 含税净价
    */
    private BigDecimal taxnetprice;

    /**
    * 金额
    */
    private BigDecimal amount;

    /**
    * 金额（本位币）
    */
    private BigDecimal amountLc;

    /**
    * 价税合计
    */
    private BigDecimal allamount;

    /**
    * 价税合计（本位币）
    */
    private BigDecimal allamountLc;

    /**
    * 免费
    */
    private String isfree;

    /**
    * 基本单位单价
    */
    private BigDecimal baseunitprice;

    /**
    * 已开票数量 
    */
    private BigDecimal invoicedqty;

    /**
    * 开票结束状态
    */
    private String invoicedstatus;

    /**
    * 加工费
    */
    private BigDecimal processfee;

    /**
    * 材料成本
    */
    private BigDecimal materialcosts;

    /**
    * 最高价
    */
    private BigDecimal maxprice;

    /**
    * 最低价
    */
    private BigDecimal minprice;

    /**
    * 成本价
    */
    private BigDecimal costprice;

    /**
    * 成本价(本位币)
    */
    private BigDecimal costpriceLc;

    /**
    * 总成本
    */
    private BigDecimal costamount;

    /**
    * 总成本（本位币）
    */
    private BigDecimal costamountLc;

    /**
    * 系统定价
    */
    private BigDecimal sysprice;

    /**
    * 价格上限
    */
    private BigDecimal upprice;

    /**
    * 价格下限
    */
    private BigDecimal downprice;

    /**
    * 已开票关联数量
    */
    private BigDecimal invoicedjoinqty;

    /**
    * 计价基本数量
    */
    private BigDecimal pricebaseqty;

    /**
    * 定价单位
    */
    private Short setpriceUnitId;

    /**
    * 剩余入库数量
    */
    private BigDecimal remaininstockqty;

    /**
    * 剩余入库基本数量
    */
    private BigDecimal remaininstockbaseqty;

    /**
    * 剩余入库单位id
    */
    private String remainInstockUnitId;

    /**
    * 未关联应付数量（计价单位）
    */
    private BigDecimal apnotjoinqty;

    /**
    * 关联应付金额
    */
    private BigDecimal apjoinamount;

    /**
    * 制单日期
    */
    private Date createdate;

    /**
    * 修改日期（入库单）
    */
    private Date modifydate;

    /**
    * 最近拉取日期
    */
    private Date latelypulldate;

    /**
    * 门店id
    */
    private String shopId;
}
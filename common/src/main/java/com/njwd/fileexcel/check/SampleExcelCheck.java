package com.njwd.fileexcel.check;

import com.alibaba.nacos.client.utils.StringUtils;
import com.njwd.common.Constant;
import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.basedata.excel.ExcelError;
import com.njwd.entity.basedata.excel.ExcelRowData;
import com.njwd.entity.reportdata.ConvertData;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.DateUtils;
import com.njwd.utils.StringUtil;

import java.util.*;

/**
 * @Description 简单的EXCEL校验工具
 * @Date 2020/3/4 20:03
 * @Author 郑勇浩
 */
public class SampleExcelCheck {

	/**
	 * @Description 非空校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 20:06
	 * @Param [excelData, notNullList]
	 * @return void
	 */
	public static void checkNull(ExcelData excelData, int[] checkIndex) {
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}

		List<ExcelRowData> excelRowData = excelData.getExcelRowDataList();
		ExcelRowData rowData;
		ExcelCellData cellData;
		//非空验证
		for (int i = 0; i < excelRowData.size(); i++) {
			rowData = excelRowData.get(i);
			//列
			for (int j = 0; j < rowData.getExcelCellDataList().size(); j++) {
				if (checkIndex[j] == 1) {
					continue;
				}
				cellData = rowData.getExcelCellDataList().get(j);
				if (cellData.getData() == null || StringUtil.isEmpty(cellData.getData().toString())) {
					excelData.getExcelErrorList().add(excelError(rowData, cellData, "值不能为空"));
					excelData.getExcelRowDataList().remove(i);
					i--;
					break;
				}
			}
		}
	}

	/**
	 * @Description 格式校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 20:06
	 * @Param [excelData, notNullList]
	 * @return void
	 */
	public static void checkFormat(ExcelData excelData, int[] checkIndex) {
		// 0 字符 1 整数 2 小数 3 日期 4 时间间隔 5 百分比 6 月份判断
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}
		int type;
		List<ExcelRowData> excelRowData = excelData.getExcelRowDataList();
		ExcelRowData rowData;
		ExcelCellData cellData;
		String data;
		//行
		for (int i = 0; i < excelRowData.size(); i++) {
			rowData = excelRowData.get(i);
			//列
			for (int j = 0; j < rowData.getExcelCellDataList().size(); j++) {
				cellData = rowData.getExcelCellDataList().get(j);
				if (cellData.getData() == null) {
					continue;
				}
				data = rowData.getExcelCellDataList().get(j).getData().toString();
				type = checkIndex[j];

				if (type == 1) {
					//整数
					if (!StringUtil.isNumeric(data) || data.contains(".")) {
						excelData.getExcelErrorList().add(excelError(rowData, cellData, "值应为整数类型"));
						excelData.getExcelRowDataList().remove(i);
						i--;
						break;
					}
				} else if (type == 2) {
					//小数
					if (!StringUtil.isNumeric(data) || data.contains("%")) {
						excelData.getExcelErrorList().add(excelError(rowData, cellData, "值应为数字类型"));
						excelData.getExcelRowDataList().remove(i);
						i--;
						break;
					}
				} else if (type == 3) {
					//日期
					if (!DateUtils.isValidDate(data) || !data.contains("-")) {
						excelData.getExcelErrorList().add(excelError(rowData, cellData, "值应为正确范围内的日期类型(如：2019-01-02)"));
						excelData.getExcelRowDataList().remove(i);
						i--;
						break;
					}
				} else if (type == 4) {
					//时间间隔 前后都是时间间隔才行
					if (j == checkIndex.length - 1) {
						continue;
					}
					if (checkIndex[j + 1] != 4) {
						continue;
					}
					String nextDate = rowData.getExcelCellDataList().get(j + 1).getData().toString();
					//如果时间验证不通过
					if (!DateUtils.isValidDate(data) || !data.contains("-")) {
						excelData.getExcelErrorList().add(excelError(rowData, cellData, "值应为正确范围内的日期类型(如：2019-01-02)"));
						excelData.getExcelRowDataList().remove(i);
						i--;
						break;
					}
					if (!DateUtils.isValidDate(nextDate) || !nextDate.contains("-")) {
						excelData.getExcelErrorList().add(excelError(rowData, rowData.getExcelCellDataList().get(j + 1), "值应为正确范围内的日期类型(如：2019-01-02)"));
						excelData.getExcelRowDataList().remove(i);
						i--;
						break;
					}
					//如果范围不对
					if (DateUtils.compareDate(data, nextDate, DateUtils.PATTERN_DAY) != -1) {
						excelData.getExcelErrorList().add(excelError(rowData, cellData, "请输入正确的时间范围"));
						excelData.getExcelRowDataList().remove(i);
						i--;
						break;
					}
				} else if (type == 5) {
					//百分号验证
					if (data.contains("%")) {
						data = data.replace("%", "");
						rowData.getExcelCellDataList().get(j).setOldData(Constant.Number.ONE);
					} else {
						rowData.getExcelCellDataList().get(j).setOldData(Constant.Number.ZERO);
					}
					rowData.getExcelCellDataList().get(j).setData(data);
					//小数
					if (!StringUtil.isNumeric(data)) {
						excelData.getExcelErrorList().add(excelError(rowData, cellData, "值应为(0-100)数字类型"));
						excelData.getExcelRowDataList().remove(i);
						i--;
						break;
					}
//					double value = Double.parseDouble(data);
//					if (value < 0 || value > 100) {
//						excelData.getExcelErrorList().add(excelError(rowData, cellData, "值应为(0-100)数字类型"));
//						excelData.getExcelRowDataList().remove(i);
//						i--;
//						break;
//					}
				} else if (type == 6) {
					//转化为日期格式
					if (DateUtils.isValidDate(data + "-01") && data.contains("-")) {
						Date date;
						date = DateUtils.parseDate(data + "-01", DateUtils.PATTERN_DAY);
						if (date != null) {
							rowData.getExcelCellDataList().get(j).setData(DateUtils.getPeriodYearNum(DateUtils.format(date, DateUtils.PATTERN_DAY)));
							rowData.getExcelCellDataList().get(j).setOldData(DateUtils.getPeriodYearNum(DateUtils.format(date, DateUtils.PATTERN_DAY)));
							continue;
						}
					}
					excelData.getExcelErrorList().add(excelError(rowData, cellData, "值应为年月格式(如：2019-01)"));
					excelData.getExcelRowDataList().remove(i);
					i--;
					break;
				}
			}
		}
	}

	/**
	 * @Description 长度校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 20:06
	 * @Param [excelData, notNullList]
	 * @return void
	 */
	public static void checkLength(ExcelData excelData, int[] checkList) {
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}

		List<ExcelRowData> excelRowData = excelData.getExcelRowDataList();
		ExcelRowData rowData;
		String data;
		//非空验证
		for (int i = 0; i < excelRowData.size(); i++) {
			rowData = excelRowData.get(i);
			//列
			for (int j = 0; j < rowData.getExcelCellDataList().size(); j++) {
				if (checkList[j] == -1) {
					continue;
				}
				if (rowData.getExcelCellDataList().get(j).getData() == null) {
					continue;
				}
				data = rowData.getExcelCellDataList().get(j).getData().toString();
				if (data.length() > checkList[j]) {
					excelData.getExcelErrorList().add(excelError(rowData, rowData.getExcelCellDataList().get(j), "最多输入" + checkList[j] + "个字符"));
					excelData.getExcelRowDataList().remove(i);
					i--;
					break;
				}
			}
		}
	}

	/**
	 * @Description 重复性校验
	 * @Author 郑勇浩
	 * @Data 2020/3/12 14:37
	 * @Param [excelData, checkList, data]
	 * @return void
	 */
	public static void checkExcelDuplicate(ExcelData excelData, int[] checkList, int[] date) {
		if (excelData.getExcelRowDataList().size() <= 1) {
			return;
		}
		List<ExcelRowData> excelRowData = excelData.getExcelRowDataList();
		ExcelRowData rowData;
		ExcelRowData compareData;

		//重复的列
		List<Integer> duplicateList;
		boolean duplicate;
		//循环行
		for (int i = 0; i < excelData.getExcelRowDataList().size() - 1; i++) {
			duplicateList = new ArrayList<>();
			duplicateList.add(i);

			rowData = excelData.getExcelRowDataList().get(i);

			//与当前列的下面所有行进行对比
			for (int j = (i + 1); j < excelData.getExcelRowDataList().size(); j++) {
				compareData = excelData.getExcelRowDataList().get(j);
				duplicate = true;

				if (compareData == null) {
					continue;
				}
				// 所有要对比的列
				for (Integer cellIndex : checkList) {
					//如果有不同就跳出
					if (!rowData.getExcelCellDataList().get(cellIndex).getData().equals(compareData.getExcelCellDataList().get(cellIndex).getData())) {
						duplicate = false;
						break;
					}
				}
				// 有重复且在有效期范围重叠
				if (duplicate && date != null) {
					if (!DateUtils.isDateCross(
							rowData.getExcelCellDataList().get(date[0]).getData().toString(),
							rowData.getExcelCellDataList().get(date[1]).getData().toString(),
							compareData.getExcelCellDataList().get(date[0]).getData().toString(),
							compareData.getExcelCellDataList().get(date[1]).getData().toString())) {
						duplicate = false;
					}
				}
				// 如果有重复的则添加至重复列
				if (duplicate) {
					duplicateList.add(j);
				}
			}
			//如果重复列大于1 则删除并且记录所有重复列
			if (duplicateList.size() > 1) {
				for (int j = duplicateList.size() - 1; j >= 0; j--) {
					excelData.getExcelErrorList().add(excelError(excelRowData.get(duplicateList.get(j)), null, "该行与表格内其他数据存在重复或有效日期存在交集"));
					excelRowData.remove(excelRowData.get(duplicateList.get(j)));
				}
				i--;
			}
		}
	}

	/**
	 * @Description 值转化校验
	 * @Author 郑勇浩
	 * @Data 2020/2/26 22:48
	 * @Param []
	 * @return void
	 */
	public static void checkConvert(ExcelData excelData, int[] checkList, List<ConvertData> DataBaseData) {
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}

		// 根据下标判断是否存在，存在则转化
		List<ExcelRowData> excelRowData = excelData.getExcelRowDataList();
		ExcelRowData rowData;
		ExcelCellData cellData;
		String value;
		boolean ok;

		// 根据列下标划分对应的值
		Map<Integer, List<ConvertData>> convertDataMap = new HashMap<>();
		for (int index : checkList) {
			convertDataMap.put(index, new ArrayList<>());
		}
		for (ConvertData convertData : DataBaseData) {
			convertDataMap.get(convertData.getIndex()).add(convertData);
		}

		// 行
		for (int i = 0; i < excelRowData.size(); i++) {
			rowData = excelRowData.get(i);
			//转化的列
			for (Integer cellIndex : checkList) {
				ok = false;
				cellData = rowData.getExcelCellDataList().get(cellIndex);
				if (cellData.getData() == null || StringUtils.isEmpty(cellData.getData().toString())) {
					continue;
				}
				value = cellData.getData().toString();
				//查询对应的值是否有匹配的
				for (ConvertData convertData : convertDataMap.get(cellIndex)) {
					if (convertData.getOldData().trim().equals(value.trim())) {
						// 转化值
						ok = true;
						cellData.setOldData(convertData.getConvertData());
						break;
					}
				}
				//如果失败
				if (!ok) {
					excelData.getExcelErrorList().add(excelError(rowData, cellData, "系统中不存在该值请检查是否输入错误"));
					excelData.getExcelRowDataList().remove(i);
					i--;
					break;
				}
			}
		}
	}


	/**
	 * @Description 数据库内重复性校验
	 * @Author 郑勇浩
	 * @Data 2020/3/4 21:19
	 * @Param [excelData]
	 * @return void
	 */
	public static void checkDataBaseDuplicate(ExcelData excelData, int[] checkList, int[] date, List<Map<String, String>> dataList) {
		// 所有的类型
		if (excelData.getExcelRowDataList().size() == 0) {
			return;
		}
		// 获取需要匹配的值
		ExcelRowData rowData;
		//匹配重复
		boolean duplicate;
		ExcelCellData cellData = null;
		for (int i = 0; i < excelData.getExcelRowDataList().size(); i++) {
			rowData = excelData.getExcelRowDataList().get(i);
			//进行行匹配
			for (Map<String, String> compareData : dataList) {
				duplicate = true;

				//需要匹配的列都进行匹配
				for (Integer cellIndex : checkList) {
					cellData = rowData.getExcelCellDataList().get(cellIndex);
					if (cellData.getData() == null || StringUtils.isEmpty(cellData.getData().toString())) {
						continue;
					}
					//只要有不相同的则不匹配
					if (cellData.getOldData() != null) {
						if (!String.valueOf(cellData.getOldData()).equals(compareData.get(cellIndex.toString()))) {
							duplicate = false;
							break;
						}
					}
				}

				// 有重复且在有效期范围重叠
				if (duplicate && date != null && compareData.get("beginDate") != null && compareData.get("endDate") != null) {
					if (!DateUtils.isDateCross(
							rowData.getExcelCellDataList().get(date[0]).getData().toString(),
							rowData.getExcelCellDataList().get(date[1]).getData().toString(),
							compareData.get("beginDate"),
							compareData.get("endDate"))) {
						duplicate = false;
					}
				}

				//完全匹配
				if (duplicate) {
					excelData.getExcelErrorList().add(excelError(rowData, cellData, "该行与表格内其他数据存在重复或有效日期存在交集"));
					excelData.getExcelRowDataList().remove(i);
					i--;
					break;
				}
			}
		}
	}

	/**
	 * @Description 错误信息
	 * @Author 郑勇浩
	 * @Data 2020/2/14 16:48
	 * @Param [rowData, cellData, message]
	 * @return com.njwd.entity.basedata.excel.ExcelError
	 */
	public static ExcelError excelError(ExcelRowData rowData, ExcelCellData cellData, String message) {
		ExcelError excelError = new ExcelError();
		excelError.setSheetName(rowData.getSheetName());
		excelError.setRowNum(rowData.getSheetNum());
		excelError.setRowNum(rowData.getRowNum());
		if (cellData == null) {
			excelError.setCellNum(0);
		} else {
			excelError.setCellNum(cellData.getCellNum());
			excelError.setData(cellData.getData());
		}
		excelError.setErrorMsg(message);
		return excelError;
	}

}

package com.njwd.fileexcel.read;

import com.njwd.entity.basedata.excel.ExcelCellData;
import com.njwd.entity.basedata.excel.ExcelData;
import com.njwd.entity.basedata.excel.ExcelRowData;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.utils.ExcelUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @Description ExcelRead
 * @Date 2020/2/13 15:00
 * @Author 郑勇浩
 */
public class SampleExcelRead extends AbstractExcelRead {

	@Override
	public void read0(InputStream inputStream, ExcelData excelData) {
		excelData.setExcelRowDataList(new ArrayList<>());
		excelData.setExcelErrorList(new ArrayList<>());
		// 读取
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(inputStream);
		} catch (IOException | InvalidFormatException e) {
			throw new ServiceException(ResultCode.EXCEL_PARSE_CORRECT);
		}
		if (workbook == null) {
			throw new ServiceException(ResultCode.EXCEL_NOT_CORRECT);
		}

		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		Sheet sheet = workbook.getSheetAt(0);
		//读取excel数据
		int colNum = 0;
		for (Row row : sheet) {
			if (row.getRowNum() == 0) {
				colNum = row.getLastCellNum();
				continue;
			}
			ExcelRowData excelRowData = new ExcelRowData();
			excelRowData.setRowNum(row.getRowNum());
			excelRowData.setExcelCellDataList(new ArrayList<>());
			if (row.getLastCellNum() <= 0) {
				continue;
			}
			for (int i = 0; i < colNum; i++) {
				Object value = null;
				Cell cell = row.getCell(i);
				if (cell != null) {
					evaluator.evaluateInCell(cell);
					value = ExcelUtil.getCellValue(cell);
				}
				ExcelCellData cellData = new ExcelCellData();
				cellData.setCellNum(i);
				cellData.setData(value);
				excelRowData.getExcelCellDataList().add(cellData);
			}
			excelData.getExcelRowDataList().add(excelRowData);
		}
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.debug("读取完第{}条数据", excelData.getExcelRowDataList());
	}

}

package com.alibaba.excel.handler;

import com.alibaba.excel.event.WriteHandler;
import org.apache.poi.ss.usermodel.*;

/**
 * @Description 设置模板样式
 * @Date 2020/3/3 18:10
 * @Author 郑勇浩
 */
public class AfterWriteHandlerImpl implements WriteHandler {

	@Override
	public void sheet(int sheetNo, Sheet sheet) {
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		cellStyle.setBottomBorderColor((short) 0);
		DataFormat format = sheet.getWorkbook().createDataFormat();
		cellStyle.setDataFormat(format.getFormat("@"));
		for (int i = 0; i < 1000; i++) {
			sheet.setDefaultColumnStyle(i, cellStyle);
		}
	}

	@Override
	public void row(int rowNum, Row row) {
	}

	@Override
	public void cell(int cellNum, Cell cell) {
	}
}

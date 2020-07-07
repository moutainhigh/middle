package com.njwd.excel.extend;

import com.alibaba.excel.event.WriteHandler;
import org.apache.poi.ss.usermodel.*;

public class StyleExcelAutoHandler implements WriteHandler {

	private Workbook workbook;
	private CellStyle cellStyle;
	private int styleStartIndex = 0;

	public StyleExcelAutoHandler() {

	}

	public StyleExcelAutoHandler(int styleStartIndex) {
		this.styleStartIndex = styleStartIndex;
	}

	@Override
	public void sheet(int sheetNo, Sheet sheet) {
		this.workbook = sheet.getWorkbook();
		this.cellStyle = createStyle(this.workbook);
	}

	@Override
	public void row(int rowNum, Row row) {

	}

	@Override
	public void cell(int cellNum, Cell cell) {
		if (cell.getRowIndex() >= styleStartIndex) {
			cell.getRow().getCell(cellNum).setCellStyle(cellStyle);
		}

	}

	private CellStyle createStyle(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		// 水平对齐方式
		cellStyle.setAlignment(HorizontalAlignment.LEFT);
		// 垂直对齐方式
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		DataFormat dataFormat = workbook.createDataFormat();
		// 设置千位分隔符
		cellStyle.setDataFormat(dataFormat.getFormat("###,##0.00"));
		return cellStyle;
	}
}

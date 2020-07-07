package com.njwd.excel.extend;

import com.alibaba.excel.event.WriteHandler;
import org.apache.poi.ss.usermodel.*;

public class StyleExcelTwoHandler implements WriteHandler {

    private Workbook workbook;
    private CellStyle cellStyle;
    /**
     * Custom operation after creating each sheet
     *
     * @param sheetNo
     * @param sheet
     */
    @Override
    public void sheet(int sheetNo, Sheet sheet) {
        this.workbook = sheet.getWorkbook();
        this.cellStyle = createStyle(this.workbook);
    }

    /**
     * Custom operation after creating each row
     *
     * @param rowNum
     * @param row
     */
    @Override
    public void row(int rowNum, Row row) {

    }

    /**
     * Custom operation after creating each cell
     *
     * @param cellNum
     * @param cell
     */
    @Override
    public void cell(int cellNum, Cell cell) {
        if (cell.getRowIndex() >= 9) {
            cell.getRow().getCell(cellNum).setCellStyle(cellStyle);
        }

    }

    /**
     * 实际中如果直接获取原单元格的样式进行修改, 最后发现是改了整行的样式, 因此这里是新建一个样* 式
     */
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

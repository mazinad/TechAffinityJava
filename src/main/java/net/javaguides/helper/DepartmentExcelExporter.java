package net.javaguides.helper;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.javaguides.springboot.model.Department;

public class DepartmentExcelExporter {
    private XSSFWorkbook workbook;
    private List<Department> listUsers;
    private XSSFSheet sheet;
    public DepartmentExcelExporter(List<Department> listUsers) {
        this.listUsers = listUsers;
        workbook = new XSSFWorkbook();
    }
    public void writeHeader() {
        sheet = workbook.createSheet("Users");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "DeptID", style);
        createCell(row, 1, "DeptName", style);
    }
    public void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }else if(value instanceof String){
            cell.setCellValue((String) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    public void writeDataLines() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        for (Department users : listUsers) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, users.getDepartment_id(), style);
            createCell(row, columnCount++, users.getDepartment_name(), style);
        }
    }
    public void generateExcelFile(HttpServletResponse response) throws IOException {
        writeHeader();
        writeDataLines();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}

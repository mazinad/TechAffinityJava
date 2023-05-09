package net.javaguides.helper;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import net.javaguides.springboot.model.Department;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.UserRepository;

public class UserExcelExporter {
    private XSSFWorkbook workbook;
    private List<User> listUsers;
    private XSSFSheet sheet;
    @Autowired
    private UserRepository userRepository;
    public UserExcelExporter(List<User> listUsers) {
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
        createCell(row, 0, "User ID", style);
        createCell(row, 1, "First Name", style);
        createCell(row, 2, "Last Name", style);
        createCell(row, 3, "Email", style);
        createCell(row, 4, "Password", style);
        createCell(row, 5, "Department", style);
    }
    public void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Department) {
            cell.setCellValue(((Department) value).getDepartmentName());
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
    
        for (User user : listUsers) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            Long userId = user.getId();
            System.out.println("User ID: " + userId);
            createCell(row, columnCount++, userId, style);
            createCell(row, columnCount++, user.getFirstName(), style);
            createCell(row, columnCount++, user.getLastName(), style);
            createCell(row, columnCount++, user.getEmail(), style);
            createCell(row, columnCount++, user.getPassword(), style);
            String departmentNames = "";
            Set<Department> departments = user.getDepartment();
            System.out.println("Departments ABC: " + departments);
            for (Department department : departments) {
            departmentNames += department.getDepartmentName() + ", ";
}
            departmentNames = departmentNames.substring(0, departmentNames.length() - 2);
            createCell(row, columnCount++, departmentNames, style);


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

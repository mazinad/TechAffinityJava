package net.javaguides.springboot.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.javaguides.helper.DepartmentExcelExporter;
import net.javaguides.springboot.model.Department;
import net.javaguides.springboot.repository.DepartmentRepository;
import net.javaguides.springboot.service.DepartmentService;
@Controller
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @GetMapping("/department")
    public String listDepartmentHomePage(Model model){
        System.out.println("Get /department");
        System.out.println(departmentService.getAllDepartments());
        model.addAttribute("listDepartments", departmentService.getAllDepartments());
        return "departments";
    }
    @PostMapping("/saveDepartments")
    public String saveDepartment(Department department){
        departmentService.createDepartment(department);
        return "redirect:/department";
    }
    @GetMapping("/showNewDepartmentForm")
    public String showNewDepartmentForm(Model model){
        Department department = new Department();
        model.addAttribute("department", department);
        return "new_department";
    }
    @GetMapping("/editDepartment/{id}")
    public String showFormForUpdateDepartment(@PathVariable(value = "id") long id, Model model){
        Department department = departmentService.getDepartmentById(id);
        model.addAttribute("department", department);
        return "update_department";
    }
    @GetMapping("/deleteDepartment/{id}")
    public String deleteDepartment(@PathVariable(value = "id") long id){
        this.departmentService.deleteDepartmentById(id);
        return "redirect:/department";
    }
    @GetMapping("/export-to-excel1")
    public void exportIntoExcelFile(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename= user" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
		List<Department> listUsers = departmentService.getAllDepartments();
		DepartmentExcelExporter excelExporter = new DepartmentExcelExporter(listUsers);
		excelExporter.generateExcelFile(response);
	}
//     @PostMapping("/upload-departments")
//     public String uploadDepartments(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws EncryptedDocumentException, IOException {
//     System.out.println("file: " + file);
//     if (file.isEmpty()) {
//         redirectAttributes.addAttribute("error", "No file selected");
//         return "redirect:/";
//     }
//     // Read Excel file using Apache POI
//     Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(file.getBytes()));
//     Sheet sheet = workbook.getSheetAt(0);

//     // Parse data and save into database using ORM
//     for (Row row : sheet) {
//         if (row.getRowNum() < sheet.getFirstRowNum() + 1) {
//             continue; // Skip header row
//         }
//         if (isRowEmpty(row)) {
//             redirectAttributes.addAttribute("error", "Empty row found at row number " + (row.getRowNum() + 1));
//             return "redirect:/";
//         }
//         String name = row.getCell(0).getStringCellValue();
//         Department department;
//         if (departmentRepository.existsByDepartment_name(name)) {
//             System.out.println("Updating department: " + name);
//             department = departmentRepository.findByDepartment_name(name);
//         } else {
//             department = new Department();
//             department.setDepartment_name(name);
//         }
//         departmentRepository.save(department);
//     }

//     redirectAttributes.addAttribute("success", "File uploaded successfully");
//         return "redirect:/";
// }
// private boolean isRowEmpty(Row row) {
//     boolean isEmpty = true;
//     for (int cellNum = row.getFirstCellNum(); cellNum <= row.getLastCellNum(); cellNum++) {
//         Cell cell = row.getCell(cellNum);
//         if (cell != null && cell.getCellType() != CellType.BLANK) {
//             isEmpty = false;
//             break;
//         }
//     }
//     return isEmpty;
// }
}

package net.javaguides.springboot.web;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.javaguides.helper.UserExcelExporter;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.service.UserService;

@Controller
public class MainController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("listusers", userService.findAll());
		return "index";
	}
	@GetMapping("/export-to-excel")
    public void exportIntoExcelFile(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename= user" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
		List<User> listUsers = userService.findAll();
		UserExcelExporter excelExporter = new UserExcelExporter(listUsers);

		excelExporter.generateExcelFile(response);
	}
    @PostMapping("/upload")
public String uploadFile(@RequestParam("file") MultipartFile file) throws EncryptedDocumentException, IOException {
    System.out.println("file: " + file);
    // Read Excel file using Apache POI
    Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(file.getBytes()));
    Sheet sheet = workbook.getSheetAt(0);

    // Parse data and save into database using ORM
    for (Row row : sheet) {
        if (row.getRowNum() < sheet.getFirstRowNum() + 1) {
            continue; // Skip header row
        }
        String email = row.getCell(3).getStringCellValue();
        User user = userRepository.findByEmail(email);
        System.out.println("user Fetched-->: " + user);
        if (user == null) {
            // User does not exist, create a new user
            user = new User();
            user.setEmail(email);
        }
        user.setFirstName(row.getCell(1).getStringCellValue());
        user.setLastName(row.getCell(2).getStringCellValue());
        user.setPassword(passwordEncoder.encode(row.getCell(4).getStringCellValue()));
        userRepository.save(user);
    }

    return "redirect:/";
}







}

package net.javaguides.springboot.web;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.javaguides.helper.UserExcelExporter;
import net.javaguides.springboot.model.Department;
import net.javaguides.springboot.model.Role;
import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.DepartmentRepository;
import net.javaguides.springboot.repository.RoleRepository;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.service.DepartmentService;
import net.javaguides.springboot.service.UserService;

@Controller
// @RestController
public class MainController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private RoleRepository roleRepository;
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
    @GetMapping("/getUsers")
    @ResponseBody
    public List<User> getUsers(){
        return userService.findAll();
    }
    @PostMapping("/upload")
public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws EncryptedDocumentException, IOException {
    System.out.println("file: " + file);

    // Check if file is empty
    if (file.isEmpty()) {
        redirectAttributes.addAttribute("error", "No file selected");
        return "redirect:/";
    }

    // Read Excel file using Apache POI
    Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(file.getBytes()));
    Sheet sheet = workbook.getSheetAt(0);

    // Map department name to department object
    Map<String, Department> departmentMap = new HashMap<>();
    List<Department> departments = departmentRepository.findAll();
    for (Department department : departments) {
        departmentMap.put(department.getDepartmentName(), department);
    }

    // Map role name to role object
    Map<String, Role> roleMap = new HashMap<>();
    List<Role> roles = roleRepository.findAll();
    for (Role role : roles) {
        roleMap.put(role.getName(), role);
    }

    // Parse data and save into database using ORM
    for (Row row : sheet) {
        if (row.getRowNum() < sheet.getFirstRowNum() + 1) {
            continue; // Skip header row
        }
        if (isRowEmpty(row)) {
            redirectAttributes.addAttribute("error", "Empty row found at row number " + (row.getRowNum() + 1));
            return "redirect:/";
        }
        String email = null;
        Cell emailCell = row.getCell(3);
        if (emailCell != null && emailCell.getColumnIndex() >= 0) {
            if (emailCell.getCellType() == CellType.STRING) {
                email = emailCell.getStringCellValue();
            } else if (emailCell.getCellType() == CellType.NUMERIC) {
                email = String.valueOf((int) emailCell.getNumericCellValue());
            }
        }
        if (email == null || !isValidEmail(email)) {
            redirectAttributes.addAttribute("error", "Invalid email found at row number " + (row.getRowNum() + 1));
            return "redirect:/";
        }
        User user = userRepository.findByEmail(email);
        System.out.println("user Fetched-->: " + user);
        if (user == null) {
            // User does not exist, create a new user
            user = new User();
            user.setEmail(email);
        }
        user.setFirstName(row.getCell(1).getStringCellValue());
        user.setLastName(row.getCell(2).getStringCellValue());
        Cell passwordCell = row.getCell(4);
        if (passwordCell != null && passwordCell.getColumnIndex() >= 0) {
            user.setPassword(passwordEncoder.encode(passwordCell.getStringCellValue()));
        }
        String departmentNames = row.getCell(5).getStringCellValue();
        Set<Department> departments1 = new HashSet<>();
        for (String departmentName : departmentNames.split(",")) {
            Department department = departmentMap.get(departmentName.trim());
            if (department != null) {
                departments1.add(department);
            }
        }
        user.setDepartment(departments1);
        
        // Set user roles
        if (row.getCell(6) != null && row.getCell(6).getCellType() == CellType.STRING) {
            String roleName = row.getCell(6).getStringCellValue();
            Role role = roleMap.get(roleName);
            if (role != null) {
                user.setRoles(Arrays.asList(role));
            }
        }
        
        userRepository.save(user);
    }
    redirectAttributes.addAttribute("success", "Users uploaded successfully");
    return "redirect:/";
}

    private boolean isRowEmpty(Row row) {
    if (row == null) {
        return true;
    }
    for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
        Cell cell = row.getCell(i);
        if (cell != null && cell.getCellType() != CellType.BLANK) {
            return false;
        }
    }
    return true;
}
    
    private boolean isValidEmail(String email) {
        // Use a regex pattern to validate email addresses
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
    // delete the user from the database using the id
    this.userService.deleteByIds(id);

    // redirect to the list of users
    return "redirect:/";
}
       @GetMapping("/editUser/{id}")
        public String showFormForUpdate(@PathVariable(value = "id") long id, Model model) {
       try {
           User user = userService.findById(id);
		   model.addAttribute("listDepartments", departmentService.getAllDepartments());
           model.addAttribute("user", user);
       } catch (Exception e) {
           model.addAttribute("error", e.getMessage());
       }
       return "updateUser";
   }
      @PostMapping("/updateUser")
       public String updateUser(@ModelAttribute("user") User user, Model model) {
    try {
        userService.updateUser(user);
        return "redirect:/"; // Redirect to the user list page
    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        return "updateUser"; // Display the update form with the error message
    }
}

}

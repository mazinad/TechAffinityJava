package net.javaguides.springboot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import net.javaguides.springboot.model.Department;

public interface DepartmentService {
    Department createDepartment(Department department);
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    void deleteDepartmentById(long id);

}

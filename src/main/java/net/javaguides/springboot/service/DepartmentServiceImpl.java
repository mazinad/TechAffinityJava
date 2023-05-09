package net.javaguides.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.javaguides.springboot.model.Department;
import net.javaguides.springboot.repository.DepartmentRepository;
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Department createDepartment(Department department) {
        // TODO Auto-generated method stub
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartments() {
        // TODO Auto-generated method stub
       return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        // TODO Auto-generated method stub
        return departmentRepository.findById(id).get();
    }

    @Override
    public void deleteDepartmentById(long id) {
        // TODO Auto-generated method stub
        this.departmentRepository.deleteById(id);
    }

    @Override
    public Department getDepartmentById(int numericCellValue) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDepartmentById'");
    }
    
}

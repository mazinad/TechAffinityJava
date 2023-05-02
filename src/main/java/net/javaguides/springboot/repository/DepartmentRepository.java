package net.javaguides.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.javaguides.springboot.model.Department;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    
    // Department findByDepartment_name(String department_name);
    // boolean existsByDepartment_name(String departmentName);
    
}

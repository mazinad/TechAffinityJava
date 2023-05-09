package net.javaguides.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import net.javaguides.springboot.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByDepartmentName(String departmentName);
    Department findByDepartmentName(String departmentName);
    
}

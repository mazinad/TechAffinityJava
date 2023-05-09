package net.javaguides.springboot.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Department {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long departmentId;
   private String departmentName;
   public Long getDepartmentId() {
    return departmentId;
}
public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
}
public String getDepartmentName() {
    return departmentName;
}
public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
}   
}

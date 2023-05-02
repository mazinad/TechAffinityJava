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
   private Long department_id;
   private String department_name;
   @OneToMany(mappedBy = "department")
   private List<User> users;

   public Long getDepartment_id() {
        return department_id;
    }
    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }
    public String getDepartment_name() {
        return department_name;
    }
    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }
}

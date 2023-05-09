package net.javaguides.springboot.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import net.javaguides.springboot.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

    Role findByName(String name);
    
}

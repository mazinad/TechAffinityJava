package net.javaguides.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.javaguides.springboot.model.Role;
import net.javaguides.springboot.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT u.id, u.first_name, u.last_name, u.email, u.password, d.department_id, d.department_name " +
    "FROM user u " +
    "LEFT JOIN user_department ud ON u.id = ud.user_id " +
    "LEFT JOIN department d ON ud.department_id = d.department_id " +
    "WHERE u.id = :userId", nativeQuery = true)
    List<Object[]> findUserWithDepartmentById(Long userId);

    List<User> findByRolesIn(List<Role> singletonList);


}

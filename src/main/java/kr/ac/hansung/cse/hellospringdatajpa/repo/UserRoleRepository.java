package kr.ac.hansung.cse.hellospringdatajpa.repo;

import kr.ac.hansung.cse.hellospringdatajpa.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
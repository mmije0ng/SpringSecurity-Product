package kr.ac.hansung.cse.hellospringdatajpa.service;

import kr.ac.hansung.cse.hellospringdatajpa.entity.Role;
import kr.ac.hansung.cse.hellospringdatajpa.entity.RoleType;
import kr.ac.hansung.cse.hellospringdatajpa.entity.User;
import kr.ac.hansung.cse.hellospringdatajpa.repo.RoleRepository;
import kr.ac.hansung.cse.hellospringdatajpa.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String email, String password, RoleType roleType) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // 기본 역할 부여
        Role mainRole = roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> new RuntimeException(roleType + " 권한이 존재하지 않습니다."));
        user.addRole(mainRole);

        // 관리자라면 사용자 권한도 함께 부여
        if (roleType == RoleType.ROLE_ADMIN) {
            Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ROLE_USER 권한이 존재하지 않습니다."));
            user.addRole(userRole);
        }

        return userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User setAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        if (user.hasRole(RoleType.ROLE_ADMIN)) {
            throw new RuntimeException("이미 관리자입니다.");
        }

        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN 권한이 존재하지 않습니다."));
        user.addRole(adminRole);

        return userRepository.save(user);
    }

    @Transactional
    public User removeAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 먼저 Role 엔티티를 조회
        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN 권한이 존재하지 않습니다."));

        // Role 엔티티를 넘겨서 삭제
        user.removeRole(adminRole);

        // 사용자 역할이 비어있으면 기본 사용자 권한 부여
        if (user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ROLE_USER 권한이 존재하지 않습니다."));
            user.addRole(userRole);
        }

        return userRepository.save(user);
    }

    public long countAdminUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.hasRole(RoleType.ROLE_ADMIN))
                .count();
    }

    public long countUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.hasRole(RoleType.ROLE_USER))
                .count();
    }

    public boolean isExistsById(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
}

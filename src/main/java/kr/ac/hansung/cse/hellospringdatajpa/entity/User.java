package kr.ac.hansung.cse.hellospringdatajpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "유효한 이메일 주소를 입력하세요")
    @NotBlank(message = "이메일은 필수입니다")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> roles = new ArrayList<>();

    public void addRole(Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(this);
        userRole.setRole(role);
        this.roles.add(userRole);
    }

    public void removeRole(Role role) {
        roles.removeIf(r -> r.getRole().equals(role));
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(r -> r.getRole().getRoleType().name().equalsIgnoreCase(roleName));
    }

    public boolean hasRole(RoleType roleType) {
        return roles.stream()
                .anyMatch(r -> r.getRole().getRoleType() == roleType);
    }
}

package kr.ac.hansung.cse.hellospringdatajpa.security;

import kr.ac.hansung.cse.hellospringdatajpa.entity.Role;
import kr.ac.hansung.cse.hellospringdatajpa.entity.User;
import kr.ac.hansung.cse.hellospringdatajpa.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service // Spring Bean으로 등록
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // 사용자 정보를 조회하기 위한 레포지토리

    /**
     * Spring Security에서 사용자 인증 시 호출되는 메서드
     * 사용자의 이메일(또는 username)로 DB에서 사용자 정보를 조회하여 UserDetails로 변환
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 이메일로 사용자 조회, 없을 경우 예외 발생
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // Spring Security의 UserDetails 구현체(org.springframework.security.core.userdetails.User)로 반환
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),              // 사용자 이메일 (username)
                user.getPassword(),          // 사용자 비밀번호
                mapRolesToAuthorities(user)  // 사용자 권한 목록
        );
    }

     /**
     * 사용자 객체의 역할(Role)을 Spring Security의 GrantedAuthority 객체로 변환
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(User user) {
        return user.getRoles().stream()
                .map(userRole -> {
                    Role role = userRole.getRole();
                    if (role != null && role.getRoleType() != null) {
                        return new SimpleGrantedAuthority(role.getRoleType().name());
                    } else {
                        throw new IllegalStateException("권한 정보가 누락되었습니다.");
                    }
                })
                .collect(Collectors.toList());
    }
}
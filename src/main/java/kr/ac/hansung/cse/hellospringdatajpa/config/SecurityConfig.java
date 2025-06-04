package kr.ac.hansung.cse.hellospringdatajpa.config;

import kr.ac.hansung.cse.hellospringdatajpa.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@EnableMethodSecurity // Spring Security 6 이상 사용 시
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // 비밀번호 암호화를 위한 Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 해시 함수로 비밀번호 암호화
    }

    // 보안 필터 체인 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 인가 설정: URL 경로별 접근 권한 지정
                .authorizeHttpRequests(authz -> authz
                        // 인증 없이 접근 허용 (회원가입, 로그인, 정적 리소스 등)
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**").permitAll()
                        // 로그인한 사용자 중 USER 또는 ADMIN 역할만 접근 가능
                        .requestMatchers("/products").hasAnyRole("USER", "ADMIN")
                        // ADMIN 역할만 접근 가능
                        .requestMatchers("/products/new", "/products/edit/**", "/products/delete/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 위에서 명시한 경로 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인 페이지 경로
                        .defaultSuccessUrl("/products", true) // 로그인 성공 시 이동할 기본 페이지
                        .failureUrl("/login?error=true") // 로그인 실패 시 이동할 페이지
                        .permitAll() // 로그인 페이지는 모두 접근 가능
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL 경로
                        .logoutSuccessUrl("/login?logout=true") // 로그아웃 성공 후 이동 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .deleteCookies("JSESSIONID") // 쿠키 삭제
                        .permitAll() // 로그아웃은 모두 접근 가능
                )

                // 사용자 인증 정보 제공 서비스 등록
                .userDetailsService(userDetailsService);

        return http.build();
    }
}

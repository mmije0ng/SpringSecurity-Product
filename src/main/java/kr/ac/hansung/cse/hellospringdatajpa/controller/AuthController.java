package kr.ac.hansung.cse.hellospringdatajpa.controller;

import kr.ac.hansung.cse.hellospringdatajpa.entity.RoleType;
import kr.ac.hansung.cse.hellospringdatajpa.entity.User;
import kr.ac.hansung.cse.hellospringdatajpa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("/auth")
@Slf4j
public class AuthController {

    @Autowired private UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUserList(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/user_list";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "이메일 또는 비밀번호가 잘못되었습니다.");
        if (logout != null) model.addAttribute("message", "성공적으로 로그아웃되었습니다.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "role", required = false) String role,
                               Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("role", role); // 사용 목적 전달
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user,
                               @RequestParam(value = "role", required = false) String role,
                               RedirectAttributes redirectAttributes) {
        try {
            RoleType roleType = "admin".equalsIgnoreCase(role) ? RoleType.ROLE_ADMIN : RoleType.ROLE_USER;

            userService.registerUser(user.getEmail(), user.getPassword(), roleType);
            log.info("등록된 사용자 권한: " + roleType);

            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}

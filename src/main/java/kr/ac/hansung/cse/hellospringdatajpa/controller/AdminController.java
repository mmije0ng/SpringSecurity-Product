package kr.ac.hansung.cse.hellospringdatajpa.controller;

import kr.ac.hansung.cse.hellospringdatajpa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 전체 컨트롤러는 관리자만 접근 가능
public class AdminController {

    private final UserService userService;

    // 전체 사용자 목록 조회
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("totalUsers", userService.findAllUsers().size());
        model.addAttribute("adminCount", userService.countAdminUsers());
        model.addAttribute("userCount", userService.countUsers());
        return "admin/user_list"; // templates/admin/user_list.html
    }

    // ✅ 관리자 권한 부여
    @PostMapping("/set-admin/{userId}")
    public String setAdmin(@PathVariable Long userId,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.setAdmin(userId);
            redirectAttributes.addFlashAttribute("message", "관리자로 승격되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ✅ 관리자 권한 제거
    @PostMapping("/remove-admin/{userId}")
    public String removeAdmin(@PathVariable Long userId,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.removeAdminRole(userId);
            redirectAttributes.addFlashAttribute("message", "관리자 권한이 제거되었습니다.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}

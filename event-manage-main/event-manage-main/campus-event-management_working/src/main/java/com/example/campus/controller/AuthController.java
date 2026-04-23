package com.example.campus.controller;

import com.example.campus.entity.Admin;
import com.example.campus.entity.Student;
import com.example.campus.service.AdminService;
import com.example.campus.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    
    private final StudentService studentService;
    private final AdminService adminService;
    
    public AuthController(StudentService studentService, AdminService adminService) {
        this.studentService = studentService;
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/help")
    public String helpPage() { return "help"; }

    @GetMapping("/contact")
    public String contactPage() { return "contact"; }

    // STUDENT AUTH
    @GetMapping("/student/login")
    public String studentLoginPage() {
        return "student-login";
    }

    @PostMapping("/student/login")
    public String studentLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Student student = studentService.login(email, password);
        if (student != null) {
            session.setAttribute("studentId", student.getId());
            session.setAttribute("studentName", student.getName());
            return "redirect:/student/dashboard";
        }
        model.addAttribute("error", "Invalid email or password");
        return "student-login";
    }

    @GetMapping("/student/register")
    public String studentRegisterPage() {
        return "student-register";
    }

    @PostMapping("/student/register")
    public String studentRegister(@ModelAttribute Student student, Model model) {
        studentService.register(student);
        model.addAttribute("msg", "Registration successful. Please login.");
        return "student-login";
    }

    @GetMapping("/student/logout")
    public String studentLogout(HttpSession session) {
        session.removeAttribute("studentId");
        session.removeAttribute("studentName");
        return "redirect:/";
    }

    // ADMIN AUTH
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Admin admin = adminService.login(username, password);
        if (admin != null) {
            session.setAttribute("adminId", admin.getId());
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Invalid username or password");
        return "admin-login";
    }
    


    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("adminId");
        return "redirect:/";
    }
}

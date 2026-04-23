package com.example.campus.controller;

import com.example.campus.entity.Event;
import com.example.campus.entity.EventJoin;
import com.example.campus.entity.Student;
import com.example.campus.service.EventJoinService;
import com.example.campus.service.EventService;
import com.example.campus.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final EventService eventService;
    private final EventJoinService eventJoinService;
    private final StudentService studentService;

    public StudentController(EventService eventService, EventJoinService eventJoinService, StudentService studentService) {
        this.eventService = eventService;
        this.eventJoinService = eventJoinService;
        this.studentService = studentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/student/login";

        List<Event> events = eventService.getAllEvents();
        List<EventJoin> joinedEvents = eventJoinService.getByStudentId(studentId);
        List<Long> joinedEventIds = joinedEvents.stream().map(ej -> ej.getEvent().getId()).collect(Collectors.toList());

        model.addAttribute("events", events);
        model.addAttribute("joinedEventIds", joinedEventIds);
        return "student-dashboard";
    }

    @PostMapping("/join/{eventId}")
    public String joinEvent(@PathVariable Long eventId, HttpSession session, RedirectAttributes redirectAttrs) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/student/login";

        if (!eventJoinService.exists(studentId, eventId)) {
            Student student = studentService.getStudentById(studentId);
            Event event = eventService.getEventById(eventId);

            if (student != null && event != null) {
                EventJoin join = new EventJoin();
                join.setStudent(student);
                join.setEvent(event);
                eventJoinService.save(join);
                redirectAttrs.addFlashAttribute("msg", "Successfully joined the event!");
            }
        } else {
            redirectAttrs.addFlashAttribute("msg", "You have already joined this event!");
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/student/login";
        model.addAttribute("student", studentService.getStudentById(studentId));
        return "student-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute Student studentDetails,
                                @RequestParam(name = "profileImage", required = false) MultipartFile profileImage,
                                HttpSession session) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/student/login";
        Student student = studentService.getStudentById(studentId);
        if (student != null) {
            student.setName(studentDetails.getName());
            student.setEmail(studentDetails.getEmail());
            student.setDepartment(studentDetails.getDepartment());
            student.setNumber(studentDetails.getNumber());
            student.setBasicDetails(studentDetails.getBasicDetails());
            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    String base64Image = Base64.getEncoder().encodeToString(profileImage.getBytes());
                    String imageDataUrl = "data:" + profileImage.getContentType() + ";base64," + base64Image;
                    student.setImageDp(imageDataUrl);
                } catch (IOException e) {
                    // Keep existing image if upload fails
                }
            } else if (studentDetails.getImageDp() != null && !studentDetails.getImageDp().trim().isEmpty()) {
                student.setImageDp(studentDetails.getImageDp());
            }
            session.setAttribute("studentName", student.getName());
            studentService.updateStudent(student);
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/profile/delete")
    public String deleteProfile(HttpSession session) {
        Long studentId = (Long) session.getAttribute("studentId");
        if (studentId == null) return "redirect:/student/login";
        eventJoinService.deleteAllByStudentId(studentId);
        studentService.deleteStudent(studentId);
        session.invalidate();
        return "redirect:/";
    }
}

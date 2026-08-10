package vn.edu.crs.courseservice.controller;

import vn.edu.crs.courseservice.dto.CourseDTO;
import vn.edu.crs.courseservice.service.CourseService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/courses")
@RequiredArgsConstructor
public class InternalCourseController {

    private final CourseService courseService;


    // =========================================================
    // PATCH /internal/courses/{id}/reserve-seat
    // Khi đăng ký môn học -> giảm 1 chỗ
    // =========================================================
    @PatchMapping("/{id}/reserve-seat")
    public CourseDTO reserveSeat(
            @PathVariable Long id
    ) {

        return courseService.reserveSeat(id);
    }


    // =========================================================
    // PATCH /internal/courses/{id}/release-seat
    // Khi hủy đăng ký -> hoàn lại 1 chỗ
    // =========================================================
    @PatchMapping("/{id}/release-seat")
    public CourseDTO releaseSeat(
            @PathVariable Long id
    ) {

        return courseService.releaseSeat(id);
    }
}
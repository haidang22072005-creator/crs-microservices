package vn.edu.crs.courseservice.repository;

import vn.edu.crs.courseservice.entity.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository
        extends JpaRepository<Course, Long> {

    // Kiểm tra tên môn học đã tồn tại chưa
    boolean existsByTenMonHocIgnoreCase(String tenMonHoc);

    // BUỔI 3:
    // Tìm tên môn học có chứa keyword,
    // không phân biệt hoa thường và có phân trang
    Page<Course> findByTenMonHocContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );
}
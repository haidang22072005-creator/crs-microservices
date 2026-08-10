package vn.edu.crs.courseservice.service;

import vn.edu.crs.courseservice.dto.CourseDTO;
import vn.edu.crs.courseservice.entity.Course;
import vn.edu.crs.courseservice.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;


    // =========================================================
    // BUỔI 2 - LẤY TOÀN BỘ DANH SÁCH MÔN HỌC
    // =========================================================
    public List<CourseDTO> getAll() {

        return courseRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    // =========================================================
    // BUỔI 2 - LẤY MÔN HỌC THEO ID
    // =========================================================
    public CourseDTO getById(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Khong tim thay mon hoc id = " + id
                        )
                );

        return toDTO(course);
    }


    // =========================================================
    // BUỔI 2 - TẠO MÔN HỌC
    // =========================================================
    public CourseDTO create(CourseDTO dto) {

        // Kiểm tra tên môn học đã tồn tại chưa
        if (courseRepository.existsByTenMonHocIgnoreCase(
                dto.getTenMonHoc())) {

            throw new IllegalArgumentException(
                    "Ten mon hoc da ton tai"
            );
        }

        Course course = new Course();

        course.setTenMonHoc(
                dto.getTenMonHoc()
        );

        course.setSoTinChi(
                dto.getSoTinChi()
        );

        course.setSoChoToiDa(
                dto.getSoChoToiDa()
        );

        // Khi tạo môn mới:
        // số chỗ còn lại ban đầu = số chỗ tối đa
        course.setSoChoConLai(
                dto.getSoChoToiDa()
        );

        return toDTO(
                courseRepository.save(course)
        );
    }


    // =========================================================
    // BUỔI 2 - CẬP NHẬT MÔN HỌC
    // =========================================================
    public CourseDTO update(
            Long id,
            CourseDTO dto
    ) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Khong tim thay mon hoc id = " + id
                        )
                );

        course.setTenMonHoc(
                dto.getTenMonHoc()
        );

        course.setSoTinChi(
                dto.getSoTinChi()
        );

        course.setSoChoToiDa(
                dto.getSoChoToiDa()
        );

        return toDTO(
                courseRepository.save(course)
        );
    }


    // =========================================================
    // BUỔI 2 - XÓA MÔN HỌC
    // =========================================================
    public void delete(Long id) {

        if (!courseRepository.existsById(id)) {

            throw new NoSuchElementException(
                    "Khong tim thay mon hoc id = " + id
            );
        }

        courseRepository.deleteById(id);
    }


    // =========================================================
    // BUỔI 3 - TÌM KIẾM + PHÂN TRANG
    // =========================================================
    public Page<CourseDTO> search(
            String keyword,
            Pageable pageable
    ) {

        Page<Course> page;

        // Nếu không nhập từ khóa
        // -> lấy tất cả nhưng vẫn có phân trang
        if (keyword == null || keyword.isBlank()) {

            page = courseRepository.findAll(pageable);

        } else {

            // Có keyword
            // -> tìm theo tên môn học
            page =
                    courseRepository
                            .findByTenMonHocContainingIgnoreCase(
                                    keyword,
                                    pageable
                            );
        }

        // Chuyển Page<Course> thành Page<CourseDTO>
        return page.map(this::toDTO);
    }


    // =========================================================
    // BUỔI 3 - ĐĂNG KÝ MÔN -> TRỪ 1 CHỖ
    // =========================================================
    @Transactional
    public CourseDTO reserveSeat(Long courseId) {

        // Tìm môn học
        Course course =
                courseRepository.findById(courseId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Khong tim thay mon hoc id = "
                                                + courseId
                                )
                        );

        // Kiểm tra còn chỗ hay không
        if (course.getSoChoConLai() <= 0) {

            throw new IllegalStateException(
                    "Mon hoc da het cho, khong the dang ky"
            );
        }

        // Trừ 1 chỗ
        course.setSoChoConLai(
                course.getSoChoConLai() - 1
        );

        // Lưu lại DB
        return toDTO(
                courseRepository.save(course)
        );
    }


    // =========================================================
    // BUỔI 3 - HỦY ĐĂNG KÝ -> HOÀN LẠI 1 CHỖ
    // =========================================================
    @Transactional
    public CourseDTO releaseSeat(Long courseId) {

        // Tìm môn học
        Course course =
                courseRepository.findById(courseId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Khong tim thay mon hoc id = "
                                                + courseId
                                )
                        );

        // Chỉ tăng nếu số chỗ còn lại
        // vẫn nhỏ hơn số chỗ tối đa
        if (course.getSoChoConLai()
                < course.getSoChoToiDa()) {

            course.setSoChoConLai(
                    course.getSoChoConLai() + 1
            );
        }

        // Lưu lại DB
        return toDTO(
                courseRepository.save(course)
        );
    }


    // =========================================================
    // CHUYỂN ENTITY COURSE -> COURSEDTO
    // =========================================================
    private CourseDTO toDTO(Course course) {

        return new CourseDTO(
                course.getId(),
                course.getTenMonHoc(),
                course.getSoTinChi(),
                course.getSoChoToiDa(),
                course.getSoChoConLai()
        );
    }
}
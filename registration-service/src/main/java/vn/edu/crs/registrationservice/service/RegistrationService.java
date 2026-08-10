package vn.edu.crs.registrationservice.service;

import vn.edu.crs.registrationservice.client.CourseClient;
import vn.edu.crs.registrationservice.dto.RegistrationRequestDTO;
import vn.edu.crs.registrationservice.entity.Registration;
import vn.edu.crs.registrationservice.repository.RegistrationRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private static final String DA_DANG_KY =
            "DA_DANG_KY";

    private static final String DA_HUY =
            "DA_HUY";


    private final RegistrationRepository registrationRepository;

    private final CourseClient courseClient;


    public Registration register(
            RegistrationRequestDTO dto
    ) {

        // 1. Kiểm tra sinh viên đã đăng ký môn chưa
        boolean daDangKy =
                registrationRepository
                        .existsByStudentIdAndCourseIdAndTrangThai(
                                dto.getStudentId(),
                                dto.getCourseId(),
                                DA_DANG_KY
                        );

        if (daDangKy) {

            throw new IllegalStateException(
                    "Sinh vien da dang ky mon hoc nay roi"
            );
        }


        // 2. Yêu cầu course-service trừ 1 chỗ
        // Nếu lỗi tại đây thì KHÔNG lưu registration
        courseClient.reserveSeat(
                dto.getCourseId()
        );


        // 3. Tạo registration
        Registration registration =
                new Registration();

        registration.setStudentId(
                dto.getStudentId()
        );

        registration.setCourseId(
                dto.getCourseId()
        );

        registration.setTrangThai(
                DA_DANG_KY
        );

        registration.setNgayDangKy(
                LocalDateTime.now()
        );


        // 4. Lưu database
        return registrationRepository.save(
                registration
        );
    }


    public void cancel(Long registrationId) {

        // 1. Tìm registration
        Registration registration =
                registrationRepository
                        .findById(registrationId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Khong tim thay dang ky id = "
                                                + registrationId
                                )
                        );


        // 2. Đã hủy rồi thì không được hoàn chỗ lần nữa
        if (
                DA_HUY.equals(
                        registration.getTrangThai()
                )
        ) {

            throw new IllegalStateException(
                    "Dang ky nay da duoc huy truoc do"
            );
        }


        // 3. Hoàn lại chỗ cho course-service
        courseClient.releaseSeat(
                registration.getCourseId()
        );


        // 4. Đổi trạng thái
        registration.setTrangThai(
                DA_HUY
        );


        // 5. Lưu
        registrationRepository.save(
                registration
        );
    }
}

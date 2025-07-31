package com.medicon.server.dao.patient;

import com.medicon.server.dto.user.PatientDTO;
import java.util.List;

public interface PatientDAO {
    // 전체 환자 조회
    List<PatientDTO> findAllPatients();

    // 환자 이름으로 조회
    List<PatientDTO> findPatientByName(String name);

    // 환자 UID로 단일 조회
    PatientDTO findPatientByUid(String uid);

    // 신규 환자 저장
    void savePatient(PatientDTO patient);

    // 환자 정보 수정
    void updatePatient(PatientDTO patient);

    // 환자 삭제
    void deletePatient(String uid);
}
package com.medicon.server.service;

import com.google.firebase.auth.*;
import com.medicon.server.dto.auth.LoginResponse;
import com.medicon.server.dto.signup.DoctorSignupRequest;
import com.medicon.server.dto.signup.NurseSignupRequest;
import com.medicon.server.dto.signup.PatientSignupRequest;
import com.medicon.server.dto.auth.SignupRequest;
import com.medicon.server.dto.signup.SignupResponse;
import com.medicon.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserDataService userDataService;
    @Autowired private DoctorService doctorService;
    @Autowired private NurseService nurseService;
    // 추후 NurseService, PatientService 추가 예정

    // 일반 회원가입
    public LoginResponse signup(SignupRequest request) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance()
                    .createUser(new UserRecord.CreateRequest()
                            .setEmail(request.getEmail())
                            .setPassword(request.getPassword()));

            String jwt = jwtUtil.generateToken(userRecord.getUid(), userRecord.getEmail());
            return new LoginResponse(true, "회원가입 성공", jwt);

        } catch (FirebaseAuthException e) {
            return new LoginResponse(false, "회원가입 실패: " + e.getMessage(), null);
        }
    }

    // 로그인 (ID Token 기반)
    public LoginResponse loginWithIdToken(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            userDataService.createUserDocument(uid, email);

            String jwt = jwtUtil.generateToken(uid, email);
            return new LoginResponse(true, "로그인 성공", jwt, uid, email);

        } catch (FirebaseAuthException e) {
            return new LoginResponse(false, "로그인 실패: " + e.getMessage(), null);
        }
    }

    // 비밀번호 변경
    public LoginResponse resetPw(String idToken, String newPassword) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            FirebaseAuth.getInstance().updateUser(
                    new UserRecord.UpdateRequest(uid).setPassword(newPassword)
            );

            return new LoginResponse(true, "비밀번호 변경 성공");
        } catch (FirebaseAuthException e) {
            return new LoginResponse(false, "변경 실패: " + e.getMessage());
        }
    }

    // 회원 탈퇴
    public LoginResponse deleteUser(String uid) {
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
            return new LoginResponse(true, "회원 탈퇴 성공");
        } catch (FirebaseAuthException e) {
            return new LoginResponse(false, "탈퇴 실패: " + e.getMessage());
        }
    }

    public String getUidFromJwt(String jwt) {
        return jwtUtil.getUidFromToken(jwt);
    }

    // 의사 등록
    public SignupResponse signupDoctor(DoctorSignupRequest req) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance()
                    .createUser(new UserRecord.CreateRequest()
                            .setEmail(req.getEmail())
                            .setPassword(req.getPassword()));

            String uid = userRecord.getUid();
            return doctorService.registerDoctor(uid, req);

        } catch (FirebaseAuthException e) {
            return new SignupResponse(false, "Firebase 계정 생성 실패: " + e.getMessage(), null);
        }
    }

    // 추후 간호사 등록
    public SignupResponse signupNurse(NurseSignupRequest request) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance()
                    .createUser(new UserRecord.CreateRequest()
                            .setEmail(request.getEmail())
                            .setPassword(request.getPassword()));

            String uid = userRecord.getUid();
            nurseService.registerNurse(uid, request);

            String token = jwtUtil.generateToken(uid, request.getEmail());
            return new SignupResponse(true, "간호사 등록 성공", token);
        } catch (Exception e) {
            return new SignupResponse(false, "간호사 등록 실패: " + e.getMessage(), null);
        }
    }

    // 추후 환자 등록 (미구현)
    public SignupResponse signupPatient(PatientSignupRequest request) {
        return new SignupResponse(false, "환자 등록 미구현", null);
    }
}

package com.medicon.server.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import com.medicon.server.dto.LoginResponse;
import com.medicon.server.dto.SignupRequest;
import com.medicon.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDataService userDataService;

    // 회원가입
    public LoginResponse signup(SignupRequest request) {
        try {
            CreateRequest createRequest = new CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

            String jwt = jwtUtil.generateToken(userRecord.getUid(), userRecord.getEmail());
            return new LoginResponse(true, "회원가입 성공", jwt);

        } catch (FirebaseAuthException e) {
            return new LoginResponse(false, "회원가입 실패: " + e.getMessage(), null);
        }
    }

    // ID 토큰 로그인
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

    // 비밀번호 재설정
    public LoginResponse resetPw(String idToken, String newPassword) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            UpdateRequest update = new UpdateRequest(uid).setPassword(newPassword);
            FirebaseAuth.getInstance().updateUser(update);

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
}

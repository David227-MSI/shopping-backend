package tw.eeits.unhappy.eee.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;

@Service
@RequiredArgsConstructor
public class UserMemberService {
    private final UserMemberRepository userMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public UserMember findUserById(Integer id) {
        return userMemberRepository.findById(id).orElse(null);
    }

    // 驗證登入
    public ServiceResponse<UserMember> authenticate(String email, String rawPassword) {

        // PasswordEncoder encoder = new BCryptPasswordEncoder();
        // String encoded = encoder.encode("Test@1234");
        // System.out.println("加密後密碼: " + encoded);

        ErrorCollector ec = new ErrorCollector();

        UserMember foundUser = null;

        if(email == null) {
            ec.add("請輸入email");
        } else if(rawPassword == null) {
            ec.add("請輸入密碼");
        } else {
            // check userMember
            foundUser = userMemberRepository.findByEmail(email).orElse(null);
        }
        
        if (foundUser == null) {
            ec.add("信箱或密碼錯誤");
        } else {
            // check password
            if (!passwordEncoder.matches(rawPassword, foundUser.getPassword())) {
                ec.add("信箱或密碼錯誤");
            }
        }

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        return ServiceResponse.success(foundUser);
    }





}
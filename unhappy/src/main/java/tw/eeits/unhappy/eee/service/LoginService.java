// package tw.eeits.unhappy.eee.service;

// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Optional;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import tw.eeits.unhappy.eee.domain.UserMember;
// import tw.eeits.unhappy.eee.repository.UserMemberRepository;
// import tw.eeits.unhappy.eee.util.PasswordUtils;

// @Service
// public class LoginService {
//     @Autowired
//     private UserMemberRepository userMemberRepository;

//     private byte[] hashPassword(String password) {
//         try {
//             MessageDigest digest = MessageDigest.getInstance("SHA-256");
//             return digest.digest(password.getBytes(StandardCharsets.UTF_8));
//         } catch (NoSuchAlgorithmException e) {
//             return password.getBytes(StandardCharsets.UTF_8);
//         }
//     }

//     public UserMember login(String email, String password) {
//         if (email != null && password != null && !password.isEmpty()) {
//             Optional<UserMember> optional = this.userMemberRepository.findByEmail(email);
//             if (optional.isPresent()) {
//                 UserMember bean = optional.get();
//                 if (PasswordUtils.verifyPassword(password, bean.getPassword())) {
//                     return bean;
//                 }
//             }
//         }
//         return null;
//     }

//     public UserMember register(UserMember userMember) {
//         if (userMember.getEmail() == null || userMember.getEmail().isEmpty()) {
//             return null;
//         }
//         Optional<UserMember> existingCustomer = userMemberRepository.findByEmail(userMember.getEmail());
//         if (existingCustomer.isPresent()) {
//             return null;
//         }
//         if (userMember.getPassword() != null) {
//             byte[] hashedPassword = hashPassword(new String(userMember.getPassword(), StandardCharsets.UTF_8));
//             userMember.setPassword(hashedPassword);
//         } else {
//             return null; 
//         }
//         return this.userMemberRepository.save(userMember);
//     }

//     public UserMember updatePassword(String email, String newPassword) {
//         if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
//             return null;
//         }        
//         Optional<UserMember> optionalCustomer = userMemberRepository.findByEmail(email);
//         if (!optionalCustomer.isPresent()) {
//             return null;
//         }        
//         UserMember userMember = optionalCustomer.get();
//         byte[] hashedPassword = hashPassword(newPassword);
//         userMember.setPassword(hashedPassword);
//         return userMemberRepository.save(userMember);
//     }
//     public UserMember updateCustomer(UserMember userMember) {
//         if (userMember == null || userMember.getEmail() == null || userMember.getEmail().isEmpty()) {
//             return null;
//         }
//         return userMemberRepository.save(userMember);
//     }
//     public UserMember clearVerificationCode(String email) {
//         if (email == null || email.isEmpty()) {
//             return null;
//         }
//         Optional<UserMember> optional = userMemberRepository.findByEmail(email);
//         if (!optional.isPresent()) {
//             return null;
//         }
//         UserMember userMember = optional.get();
//         userMember.setEmailVerificationCode(null);
//         userMember.setVerificationCodeExpiresAt(null);
//         return userMemberRepository.save(userMember);
//     }
//     public UserMember findByEmail(String email) {
//         if (email == null || email.isEmpty()) {
//             return null;
//         }
//         Optional<UserMember> optional = this.userMemberRepository.findByEmail(email);
//         return optional.orElse(null);
//     }
//     public UserMember updateEmail(String oldEmail, String newEmail) {        
//         if (oldEmail == null || oldEmail.isEmpty() || newEmail == null || newEmail.isEmpty()) {
//             return null;
//         }
//         Optional<UserMember> optionalCustomer = userMemberRepository.findByEmail(oldEmail);
//         if (!optionalCustomer.isPresent()) {
//             return null;
//         }
//         Optional<UserMember> existingWithNewEmail = userMemberRepository.findByEmail(newEmail);
//         if (existingWithNewEmail.isPresent()) {
//             return null;
//         }
//         UserMember userMember = optionalCustomer.get();
//         userMember.setEmail(newEmail);
//         UserMember savedCustomer = userMemberRepository.save(userMember);        
//         return savedCustomer;
//     }
//     public boolean isEmailExists(String email) {
//         if (email == null || email.isEmpty()) {
//             return false;
//         }
//         Optional<UserMember> existingCustomer = userMemberRepository.findByEmail(email);
//         return existingCustomer.isPresent();
//     }
//     public UserMember registerCustomer(UserMember userMember, String rawPassword) {
//         if (userMember == null || rawPassword == null || rawPassword.isEmpty()) {
//             return null;
//         }
//         if (isEmailExists(userMember.getEmail())) {
//             return null;
//         }
//         byte[] hashedPassword = hashPassword(rawPassword);
//         userMember.setPassword(hashedPassword);

//         return userMemberRepository.save(userMember);
//     }
//     @Transactional
//     public void updateAccessToken(String email, String token) {
//         UserMember userMember = userMemberRepository.findByEmail(email).orElse(null);
//         if (userMember != null) {
//             userMember.setAccessToken(token);
//             userMemberRepository.save(userMember);
//         }
//     }
// }
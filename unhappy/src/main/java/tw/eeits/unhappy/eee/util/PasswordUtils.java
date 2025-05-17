// package tw.eeits.unhappy.eee.util;

// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Arrays;

// public class PasswordUtils {
    
//     public static byte[] hashPassword(String password) {
//         try {
//             MessageDigest digest = MessageDigest.getInstance("SHA-256");
//             return digest.digest(password.getBytes(StandardCharsets.UTF_8));
//         } catch (NoSuchAlgorithmException e) {
//             throw new RuntimeException("無法執行密碼加密", e);
//         }
//     }
    
//     public static boolean verifyPassword(String rawPassword, byte[] storedHash) {
//         byte[] inputHash = hashPassword(rawPassword);
//         return Arrays.equals(storedHash, inputHash);
//     }
// }
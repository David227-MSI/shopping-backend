// package tw.eeits.unhappy.ll.util;

// import io.jsonwebtoken.io.Encoders;
// import javax.crypto.KeyGenerator;
// import javax.crypto.SecretKey;

// public class JwtKeyGenerator {
//     public static void main(String[] args) throws Exception {
//         KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//         keyGen.init(256); // 明確指定為 256-bit
//         SecretKey key = keyGen.generateKey();

//         String encoded = Encoders.BASE64.encode(key.getEncoded());
//         System.out.println("Your new JWT Secret:");
//         System.out.println(encoded);
//     }
// }

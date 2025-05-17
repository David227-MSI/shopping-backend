package tw.eeits.unhappy.eee.jwt;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.annotation.PostConstruct;

@Component
public class JsonWebTokenUtility {
	@Value("${jwt.token.expire}")
	private long expire;
	private String issuer = "meeeee";
	private byte[] sharedKey;
	@PostConstruct
	public void init() {
		sharedKey = new byte[64];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(sharedKey);
	}
	public String createToken(String data) {
		Instant now = Instant.now();
		Instant expire = now.plusSeconds(this.expire * 60);
		try {
			JWSSigner signer = new MACSigner(sharedKey);
			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
					.issuer(issuer)
					.issueTime(Date.from(now))
					.expirationTime(Date.from(expire))
					.subject(data)
					.build();
			SignedJWT signedJWT = new SignedJWT(
					new JWSHeader(JWSAlgorithm.HS512),
					claimsSet);
			signedJWT.sign(signer);
			return signedJWT.serialize();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String validateToken(String token) {
		try {
			JWSVerifier verifier = new MACVerifier(sharedKey);
			SignedJWT signedJWT = SignedJWT.parse(token);
			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
			if(signedJWT.verify(verifier) && new Date().before(claimsSet.getExpirationTime())) {
				String subject = claimsSet.getSubject();
				return subject;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

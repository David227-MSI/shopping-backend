package tw.eeits.unhappy.eee.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_member")
@Getter
@Setter
@ToString
public class UserMember {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "level_id")
	private UserLevelBean userLevel;

	@Column(name = "line_id")
	private String lineId;

	@Column(name = "google_id")
	private String googleId;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private byte[] password;

	@Column(name = "user_name")
	private String username;

	@Column(name = "birthday")
	private java.time.LocalDate birth;

	@Column(name = "phone")
	private String phone;

	@Column(name = "address")
	private String address;

	@Column(name = "carrier")
	private String carrier;

	@Column(name = "bonus_point")
	private Integer bonusPoint = 0;

	@Column(name = "good_count")
	private Integer goodCount = 0;

	@Column(name = "bad_count")
	private Integer badCount = 0;

	@Column(name = "created_at")
	@CreationTimestamp
	private java.util.Date createdAt;

	@Column(name = "updated_at")
	@UpdateTimestamp
	private java.util.Date updatedAt;

	@Column(name = "access_token")
	private String accessToken;

	@Column(name = "access_token_expires_at")
	private java.util.Date accessTokenExpiresAt;

	@Column(name = "last_login")
	private java.util.Date lastLogin;

	@Column(name = "token_version")
	private Integer tokenVersion = 1;

	@Column(name = "token_updated_at")
	@UpdateTimestamp
	private java.util.Date tokenUpdatedAt;

	@Column(name = "email_verification_code")
	private String emailVerificationCode;

	@Column(name = "verification_code_expires_at")
	private java.util.Date verificationCodeExpiresAt;

	@Column(name = "email_verified")
	private Boolean emailVerified = false;

}

// package tw.eeits.unhappy.eeit198product.entity;

// import java.time.LocalDateTime;

// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @Entity
// @Table(name = "brand")
// public class Brand {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     @NotBlank(message = "品牌名稱不可為空")
//     @Column(name = "brand_name", nullable = false)
//     private String name;

//     @NotBlank(message = "品牌類型不可為空")
//     @Column(name = "brand_type", nullable = false)
//     private String type;

//     @NotBlank(message = "統一編號不可為空")
//     @Column(name = "tax_id", nullable = false, columnDefinition = "CHAR(8)")
//     private String taxId;

//     @NotBlank(message = "地址不可為空")
//     @Column(name = "address", nullable = false)
//     private String address;

//     @Column(name = "photo_url")
//     private String photoUrl;

//     @NotBlank(message = "Email不可為空")
//     @Column(name = "email", nullable = false, unique = true)
//     private String email;

//     @NotBlank(message = "電話不可為空")
//     @Column(name = "phone", nullable = false)
//     private String phone;

//     @Column(name = "fax")
//     private String fax;

//     @NotBlank(message = "聯絡人不可為空")
//     @Column(name = "contact_name", nullable = false)
//     private String contactName;

//     @NotBlank(message = "聯絡人Email不可為空")
//     @Column(name = "contact_email", nullable = false)
//     private String contactEmail;

//     @NotBlank(message = "聯絡人電話不可為空")
//     @Column(name = "contact_phone", nullable = false)
//     private String contactPhone;

//     @NotNull(message = "品牌狀態不可為空")
//     @Enumerated(EnumType.STRING)
//     @Column(name = "brand_status", nullable = false)
//     private BrandStatus status;

//     @Builder.Default
//     @Column(name = "valid_report_count", nullable = false)
//     private Integer validReportCount = 0;

//     @CreationTimestamp
//     @Column(name = "created_at", updatable = false)
//     private LocalDateTime createdAt;

//     @UpdateTimestamp
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;
// }

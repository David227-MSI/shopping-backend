// package tw.eeits.unhappy.eee.domain;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import lombok.Getter;
// import lombok.Setter;
// import lombok.ToString;


// @Entity
// @Table(name = "customer_service")
// @Getter
// @Setter
// @ToString
// public class CustomerServiceBean {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "id")
//     private Integer id;
    
//     @Column(name = "parent_id")
//     private Integer parentId;
    
//     @Column(name = "user_id")
//     private Integer userId;
    
//     @Column(name = "question_type")
//     private String questionType;
    
//     @Column(name = "purpose")
//     private String purpose;
    
//     @Column(name = "content")
//     private String content;
    
//     @Column(name = "admin_id")
//     private Integer adminId;
    
//     @Column(name = "contact_date")
//     private java.util.Date contactDate;
    
//     @Column(name = "case_status")
//     private String caseStatus;
// }

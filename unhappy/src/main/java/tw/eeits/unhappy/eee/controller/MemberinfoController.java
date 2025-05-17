// package tw.eeits.unhappy.eee.controller;

// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RestController;

// import tw.eeits.unhappy.eee.domain.UserMember;
// import tw.eeits.unhappy.eee.jwt.JsonWebTokenUtility;
// import tw.eeits.unhappy.eee.service.LoginService;

// @RestController
// public class MemberinfoController {
//     @Autowired
//     private LoginService loginService;
    
//     @Autowired
//     private JsonWebTokenUtility jsonWebTokenUtility;

//     @GetMapping("/ajax/secure/memberinfo")
//     public String getMemberInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
//         JSONObject responseBody = new JSONObject();
//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             responseBody.put("success", false);
//             responseBody.put("message", "用戶未登入或認證失敗");
//             return responseBody.toString();
//         }
//         String token = authHeader.substring(7); 
//         String email = null;
//         try {
//             System.out.println("嘗試驗證Token...");
//             email = jsonWebTokenUtility.validateToken(token);
            
//             if (email == null) {
//                 responseBody.put("success", false);
//                 responseBody.put("message", "無效的令牌");
//                 return responseBody.toString();
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//             responseBody.put("success", false);
//             responseBody.put("message", "令牌驗證失敗: " + e.getMessage());
//             return responseBody.toString();
//         }
//         System.out.println("根據email查詢用戶: " + email);
//         UserMember customer = loginService.findByEmail(email);
//         if (customer == null) {
//             responseBody.put("success", false);
//             responseBody.put("message", "找不到用戶資訊");
//             return responseBody.toString();
//         }
        
//         System.out.println("數據庫中的token: " + 
//             (customer.getAccessToken() != null ? 
//              customer.getAccessToken().substring(0, Math.min(customer.getAccessToken().length(), 20)) + "..." : "null"));
        
//         if (customer.getAccessToken() == null || !customer.getAccessToken().equals(token)) {
//             responseBody.put("success", false);
//             responseBody.put("message", "令牌已失效，請重新登入");
//             return responseBody.toString();
//         }
//         responseBody.put("success", true);
//         responseBody.put("message", "取得會員資訊成功");
//         JSONObject customerData = new JSONObject();
//         customerData.put("id", customer.getId());
//         customerData.put("email", customer.getEmail());
//         customerData.put("username", customer.getUsername());
//         customerData.put("phone", customer.getPhone());
//         customerData.put("address", customer.getAddress());
//         customerData.put("birth", customer.getBirth());
//         responseBody.put("data", customerData);
//         return responseBody.toString();
//     }
// }
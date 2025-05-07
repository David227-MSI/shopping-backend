// package tw.eeits.unhappy.eee.controller;

// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;

// import tw.eeits.unhappy.eee.domain.UserMember;
// import tw.eeits.unhappy.eee.jwt.JsonWebTokenUtility;
// import tw.eeits.unhappy.eee.service.LoginService;

// @RestController
// public class LoginController {

//     @Autowired
//     private LoginService loginService;
//     @Autowired
//     private JsonWebTokenUtility jsonWebTokenUtility;
//     @PostMapping("/ajax/secure/login")
//     public String login(@RequestBody String body) {
//         JSONObject responseBody = new JSONObject();
//         JSONObject obj = new JSONObject(body);
//         String email = obj.isNull("email") ? null : obj.getString("email");
//         String password = obj.isNull("password") ? null : obj.getString("password");
//         if(email == null || email.length()==0 || password == null || password.length()==0) {
//             responseBody.put("success", false);
//             responseBody.put("message", "請輸入帳號或密碼");
//             return responseBody.toString();
//         }
//         UserMember customer = loginService.login(email, password);
//         if(customer == null) {
//             responseBody.put("success", false);
//             responseBody.put("message", "帳號或密碼錯誤，請重新輸入");
//         } else {
//             responseBody.put("success", true);
//             responseBody.put("message", "登入成功");
//             String token = jsonWebTokenUtility.createToken(customer.getEmail());
//             System.out.println("token="+token);
//             loginService.updateAccessToken(customer.getEmail(), token);
//             responseBody.put("token", token);
//             responseBody.put("custid", customer.getId());
//             responseBody.put("email", customer.getEmail());
//         }
//         return responseBody.toString();
//     }

//     @GetMapping("/ajax/secure/logout")
//     public String logout() {
//         JSONObject responseBody = new JSONObject();
//         responseBody.put("success", true);
//         responseBody.put("message", "登出成功");
//         return responseBody.toString();
//     }
// }
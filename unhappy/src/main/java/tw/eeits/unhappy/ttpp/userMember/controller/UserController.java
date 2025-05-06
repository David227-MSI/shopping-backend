package tw.eeits.unhappy.ttpp.userMember.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.service.UserMemberService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserMemberService userMemberService;











}

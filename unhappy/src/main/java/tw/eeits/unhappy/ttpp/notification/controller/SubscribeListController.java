package tw.eeits.unhappy.ttpp.notification.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp._itf.SubscribeListService;

@RestController
@RequestMapping("/api/subscribs")
@RequiredArgsConstructor
public class SubscribeListController {
    private final SubscribeListService subscribeListService;






    
}

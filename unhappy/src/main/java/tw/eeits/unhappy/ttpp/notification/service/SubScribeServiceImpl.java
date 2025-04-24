package tw.eeits.unhappy.ttpp.notification.service;

import org.springframework.stereotype.Service;

import tw.eeits.unhappy.ttpp._itf.SubscribeListService;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;

@Service
public class SubScribeServiceImpl implements SubscribeListService {

    @Override
    public Boolean SubscribeProduct(SubscribeList subscribelist) {
        return false;
    }

    @Override
    public Boolean SubscribeBrand(SubscribeList subscribelist) {
        return false;
    }

    @Override
    public Boolean unSubscribeProduct(SubscribeList subscribelist) {
        return false;
    }

    @Override
    public Boolean unSubscribeBrand(SubscribeList subscribelist) {
        return false;
    }

}

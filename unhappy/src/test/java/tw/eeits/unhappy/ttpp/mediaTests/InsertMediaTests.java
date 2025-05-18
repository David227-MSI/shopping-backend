package tw.eeits.unhappy.ttpp.mediaTests;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.media.CouponEventMediaService;

@SpringBootTest
public class InsertMediaTests {
    @Autowired
    private CouponEventMediaService service;


    @Test
    public void insertCouponMedia() {

        List<String> paths = new ArrayList<>();
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_1_all.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_1_all.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_1_all.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_1_all.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_2_product.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_2_product.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_2_product.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_2_product.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_3_brand.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_3_brand.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_3_brand.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/coupon_3_brand.png");

        List<Integer> ids = new ArrayList<>();
        for(int i = 1; i < 13; i++) {ids.add(i);};

        try {
            service.insertCouponMedia(paths, ids);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void insertEventMedia() {

        List<String> paths = new ArrayList<>();
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_1_spring_shopping.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_2_may_food.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_3_mothers_day.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_4_midsummer_concert.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_5_autumn_travel.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_6_new_year.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_7_1111.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_8_chrismas.png");
        paths.add("c:/Users/user/Desktop/sql_tables_20250517/images/event_9_dragon_boat.png");


        List<Integer> ids = new ArrayList<>();
        for(int i = 1; i < 10; i++) {ids.add(i);};

        try {
            service.insertEventMedia(paths, ids);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}

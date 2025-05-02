package tw.eeits.unhappy.ttpp._itf;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.coupon.dto.CouponQuery;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;
import tw.eeits.unhappy.ttpp.media.dto.MediaRequest;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;
import tw.eeits.unhappy.ttpp.media.model.CouponMedia;

public interface CouponService {

    // 建立相關==========================================================
    /**
     * @brief          建立優惠券的模板
     * @param template 優惠券物件
     * @return         ServiceResponse封包
     */
    ServiceResponse<CouponTemplate> createTemplate(CouponTemplate template);

    /**
     * @brief                 發送優惠券給用戶
     * @param couponPublished 預計要發送的優惠券
     * @return                ServiceResponse封包
     */
    ServiceResponse<CouponPublished> publishCoupon(CouponPublished couponPublished);




    ServiceResponse<CouponMedia> addMediaToTemplate(MediaRequest request) throws IOException;
    // 建立相關==========================================================
    



    // 修改相關==========================================================
    /**
     * @brief                輸入受贈者email轉移優惠券持有權
     * @param couponId       欲轉移的優惠券ID
     * @param recipientMail  受贈用戶的email
     * @return               ServiceResponse封包
     */
    ServiceResponse<CouponPublished> couponTransfer(String couponId, String recipientMail);

    /**
     * @brief    將優惠券標記為已使用
     * @param id 優惠券ID (String UUID)
     * @return   ServiceResponse封包
     */
    ServiceResponse<CouponPublished> markCouponAsUsed(String id); 
    // 修改相關==========================================================





    // 基本查詢相關======================================================
    /**
     * @brief    以ID查詢優惠券的模板
     * @param id 模板 ID
     * @return   查詢成功: 模板 / 查詢失敗: Null
     */
    CouponTemplate findTemplateById(Integer id);

    /**
     * @brief  查詢所有模板
     * @return 查詢成功: 所有模板List
     */
    List<CouponTemplate> findAllTemplates();

    /**
     * @brief  用戶查詢擁有的優惠券
     * @return 查詢成功: 用戶的優惠券List
     */
    List<CouponPublished> findCouponsByUserId();  
    // 基本查詢相關======================================================





    // 條件查詢相關======================================================
    /**
     * @brief       根據條件查詢優惠券模板
     * @param query 查詢條件
     * @return      符合條件的優惠券模板
     */
    ServiceResponse<List<CouponTemplate>> findTemplatesByCriteria(CouponQuery query);
    // 條件查詢相關======================================================







    // 用戶操作相關======================================================
    /**
     * @brief       根據條件查詢用戶的優惠券
     * @param query 查詢條件
     * @return      符合條件的優惠券清單
     */
    ServiceResponse<List<CouponPublished>> findCouponsByCriteria(CouponQuery query);
    // 用戶操作相關======================================================




}

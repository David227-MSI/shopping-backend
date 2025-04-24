package tw.eeits.unhappy.ra.media.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ra.media.model.MediaType;
import tw.eeits.unhappy.ra.media.model.ProductMedia;

@SpringBootTest(
        properties = "AZURE_STORAGE_CONN=UseDevelopmentStorage=true")
class ProductMediaRepositoryTest {

    @Autowired
    private ProductMediaRepository repo;
    @Test
    void save_ok_when_product_exists() {
        ProductMedia pm = new ProductMedia();
        pm.setProductId(103);                 // FK 對應上
        pm.setMediaType(MediaType.IMAGE);
        pm.setMediaUrl("https://picsum.photos/seed/999/400/400");
        pm.setAltText("主圖");
        pm.setIsMain(true);
        pm.setMediaOrder(1);
        ProductMedia saved = repo.save(pm);
        assertNotNull(saved.getId());         // 寫入成功
        assertEquals(MediaType.IMAGE, repo.findById(saved.getId())
                                        .orElseThrow()
                                        .getMediaType());
    }
}

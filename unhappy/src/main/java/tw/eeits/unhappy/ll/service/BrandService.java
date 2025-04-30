package tw.eeits.unhappy.ll.service;

import java.util.List;

import tw.eeits.unhappy.ll.dto.BrandResponse;
import tw.eeits.unhappy.ll.model.Brand;

public interface BrandService {

    Brand create(Brand brand);

    Brand update(Integer id, Brand brand);

    BrandResponse findById(Integer id);

    List<BrandResponse> findAll();
}

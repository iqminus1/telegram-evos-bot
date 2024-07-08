package uz.pdp.telegramevosbot.service;

import uz.pdp.telegramevosbot.payload.ApiResultDTO;
import uz.pdp.telegramevosbot.payload.ProductDTO;

public interface ProductService {
    ApiResultDTO<ProductDTO> create(ProductDTO productDTO);
    ApiResultDTO<ProductDTO> read(Integer id);

    ApiResultDTO<ProductDTO> update(Integer id, ProductDTO productDTO);

    void delete(Integer id);
}

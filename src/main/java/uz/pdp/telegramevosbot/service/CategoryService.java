package uz.pdp.telegramevosbot.service;

import uz.pdp.telegramevosbot.payload.ApiResultDTO;
import uz.pdp.telegramevosbot.payload.CategoryDTO;

public interface CategoryService {
    ApiResultDTO<CategoryDTO> create(CategoryDTO categoryDTO);
    ApiResultDTO<CategoryDTO> read(Integer id);
    ApiResultDTO<CategoryDTO> update(Integer id,CategoryDTO categoryDTO);
    void delete(Integer id);
}

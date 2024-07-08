package uz.pdp.telegramevosbot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.pdp.telegramevosbot.payload.ApiResultDTO;
import uz.pdp.telegramevosbot.payload.CategoryDTO;
import uz.pdp.telegramevosbot.service.CategoryService;

@RequestMapping("/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ApiResultDTO<CategoryDTO> read(@PathVariable  Integer id){
        return categoryService.read(id);
    }
    @PostMapping
    public ApiResultDTO<CategoryDTO> create(@RequestBody @Valid CategoryDTO categoryDTO){
        return categoryService.create(categoryDTO);
    }
    @PutMapping("/{id}")
    public ApiResultDTO<CategoryDTO> update(@PathVariable Integer id,@RequestBody @Valid CategoryDTO categoryDTO){
        return categoryService.update(id,categoryDTO);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){
        categoryService.delete(id);
    }
}

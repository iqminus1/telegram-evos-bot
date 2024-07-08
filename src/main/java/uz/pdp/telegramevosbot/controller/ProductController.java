package uz.pdp.telegramevosbot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.pdp.telegramevosbot.payload.ApiResultDTO;
import uz.pdp.telegramevosbot.payload.ProductDTO;
import uz.pdp.telegramevosbot.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ApiResultDTO<ProductDTO> read(@PathVariable Integer id) {
        return productService.read(id);
    }

    @PostMapping
    public ApiResultDTO<ProductDTO> create(@RequestBody @Valid ProductDTO productDTO) {
        return productService.create(productDTO);
    }

    @PutMapping("/{id}")
    public ApiResultDTO<ProductDTO> update(@PathVariable Integer id, @RequestBody @Valid ProductDTO productDTO) {
        return productService.update(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        productService.delete(id);
    }
}

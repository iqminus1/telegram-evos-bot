package uz.pdp.telegramevosbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.telegramevosbot.entity.Product;
import uz.pdp.telegramevosbot.exceptions.AlreadyExistsException;
import uz.pdp.telegramevosbot.exceptions.NotFoundException;
import uz.pdp.telegramevosbot.payload.ApiResultDTO;
import uz.pdp.telegramevosbot.payload.ProductDTO;
import uz.pdp.telegramevosbot.repository.AttachmentRepository;
import uz.pdp.telegramevosbot.repository.CategoryRepository;
import uz.pdp.telegramevosbot.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    public ApiResultDTO<ProductDTO> create(ProductDTO productDTO) {
        Optional<Product> byNameAndCategoryId = productRepository.findByNameAndCategoryId(productDTO.getName(), productDTO.getCategoryId());

        if (byNameAndCategoryId.isPresent()) {
            throw new AlreadyExistsException("product already exists");
        }
        Product product = createOrUpdate(productDTO, new Product());

        productRepository.save(product);

        return success(product);
    }

    @Override
    public ApiResultDTO<ProductDTO> read(Integer id) {
        return success(findById(id));
    }

    @Override
    public ApiResultDTO<ProductDTO> update(Integer id, ProductDTO productDTO) {
        Optional<Product> byNameAndCategoryId = productRepository.findByNameAndCategoryId(productDTO.getName(), productDTO.getCategoryId());
        Product productById = findById(id);

        if (byNameAndCategoryId.isPresent()) {
            Product product = byNameAndCategoryId.get();
            if (!(product.getName().equals(productById.getName())
                    && product.getCategory().equals(productById.getCategory())))
                throw new AlreadyExistsException("product already exists this category");
            Product orUpdate = createOrUpdate(productDTO, productById);
            productRepository.save(orUpdate);
            return success(orUpdate);
        }
        Product orUpdate = createOrUpdate(productDTO, productById);
        productRepository.save(orUpdate);
        return success(orUpdate);
    }

    @Override
    public void delete(Integer id) {
        productRepository.deleteById(id);
    }

    private Product createOrUpdate(ProductDTO productDTO, Product product) {
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setAttachment(attachmentRepository.findById(productDTO.getAttachmentId()).orElseThrow());
        product.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElseThrow());
        return product;
    }

    private ApiResultDTO<ProductDTO> success(Product product) {
        return ApiResultDTO.success(toDTO(product));
    }

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getAttachment().getId(), product.getCategory().getId());
    }

    private Product findById(Integer id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id = " + id));
    }
}

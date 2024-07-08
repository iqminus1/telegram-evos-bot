package uz.pdp.telegramevosbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.telegramevosbot.entity.Category;
import uz.pdp.telegramevosbot.exceptions.AlreadyExistsException;
import uz.pdp.telegramevosbot.exceptions.NotFoundException;
import uz.pdp.telegramevosbot.payload.ApiResultDTO;
import uz.pdp.telegramevosbot.payload.CategoryDTO;
import uz.pdp.telegramevosbot.repository.AttachmentRepository;
import uz.pdp.telegramevosbot.repository.CategoryRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    public ApiResultDTO<CategoryDTO> create(CategoryDTO categoryDTO) {
        Optional<Category> byName = categoryRepository.findByNameAndParentCategoryId(categoryDTO.getName(), categoryDTO.getParentCategoryId());

        if (byName.isPresent())
            throw new AlreadyExistsException(categoryDTO.getName() + " category already exists");

        Category category = createOrUpdate(categoryDTO, new Category());
        categoryRepository.save(category);
        return success(category);
    }

    @Override
    public ApiResultDTO<CategoryDTO> read(Integer id) {
        return success(findById(id));
    }

    @Override
    public ApiResultDTO<CategoryDTO> update(Integer id, CategoryDTO categoryDTO) {
        Category category = findById(id);

        Optional<Category> byNameAndParentCategoryId = categoryRepository.findByNameAndParentCategoryId(categoryDTO.getName(), categoryDTO.getParentCategoryId());

        if (byNameAndParentCategoryId.isPresent())
            throw new AlreadyExistsException("category already exists");

        Category orUpdate = createOrUpdate(categoryDTO, category);

        categoryRepository.save(orUpdate);

        return success(orUpdate);
    }

    @Override
    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }

    private ApiResultDTO<CategoryDTO> success(Category category) {
        return ApiResultDTO.success(toDTO(category));
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        if (Objects.nonNull(category.getParentCategory())) {
            categoryDTO.setParentCategoryId(category.getParentCategory().getId());
        }
        categoryDTO.setAttachmentId(category.getAttachment().getId());
        return categoryDTO;
    }

    private Category createOrUpdate(CategoryDTO categoryDTO, Category category) {
        Category parentCategory = null;
        if (Objects.nonNull(categoryDTO.getParentCategoryId()))
            parentCategory = findById(categoryDTO.getParentCategoryId());
        category.setName(categoryDTO.getName());
        category.setParentCategory(parentCategory);
        category.setAttachment(attachmentRepository.findById(categoryDTO.getAttachmentId()).orElseThrow(() -> new NotFoundException("attachment not found with id")));
        return category;
    }

    private Category findById(Integer id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("category not found"));
    }

}

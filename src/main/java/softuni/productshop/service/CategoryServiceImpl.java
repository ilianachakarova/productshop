package softuni.productshop.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.productshop.domain.entities.Category;
import softuni.productshop.domain.models.service.CategoryServiceModel;
import softuni.productshop.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CategoryServiceModel> findAllCategories() {
        return this.categoryRepository.findAll()
                .stream()
                .map(c->this.modelMapper.map(c, CategoryServiceModel.class)).collect(Collectors.toList());
    }

    @Override
    public CategoryServiceModel createCategory(CategoryServiceModel categoryServiceModel) {
        Category category = this.modelMapper.map(categoryServiceModel, Category.class);
        return this.modelMapper.map(this.categoryRepository.saveAndFlush(category),CategoryServiceModel.class);
    }

    @Override
    public CategoryServiceModel findCategoryByName(String category) {
        Category category1 = this.categoryRepository.findByName(category);
        return this.modelMapper.map(category1,CategoryServiceModel.class);
    }

    @Override
    public void deleteCategory(String id) {
        Category category = this.categoryRepository.findById(id).orElse(null);

        if(category !=null){
            this.categoryRepository.delete(category);
        }else {
            throw new IllegalArgumentException("Category not found");
        }
    }
}

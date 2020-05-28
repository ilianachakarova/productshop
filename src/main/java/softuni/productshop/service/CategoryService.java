package softuni.productshop.service;

import softuni.productshop.domain.models.service.CategoryServiceModel;

import java.util.List;

public interface CategoryService {
    List<CategoryServiceModel>findAllCategories();
    CategoryServiceModel createCategory(CategoryServiceModel categoryServiceModel);
    CategoryServiceModel findCategoryByName(String category);
    void deleteCategory(String id);
}

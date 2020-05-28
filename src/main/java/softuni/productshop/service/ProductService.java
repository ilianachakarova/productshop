package softuni.productshop.service;

import softuni.productshop.domain.models.service.ProductServiceModel;
import softuni.productshop.domain.models.view.EditProductViewModel;

import java.util.List;

public interface ProductService {
    ProductServiceModel createProduct(ProductServiceModel productServiceModel);
    ProductServiceModel findProductById(String id);
    List<ProductServiceModel> findAllProducts();

    void editProduct(ProductServiceModel product, EditProductViewModel editProductViewModel);

    ProductServiceModel deleteProduct(String id);
}

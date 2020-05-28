package softuni.productshop.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import softuni.productshop.domain.entities.Category;
import softuni.productshop.domain.entities.Product;
import softuni.productshop.domain.models.service.ProductServiceModel;
import softuni.productshop.domain.models.view.EditProductViewModel;
import softuni.productshop.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository, CategoryService categoryService) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Override
    public ProductServiceModel createProduct(ProductServiceModel productServiceModel) {
        Product product = this.modelMapper.map(productServiceModel, Product.class);
       return this.modelMapper.map(this.productRepository.saveAndFlush(product), ProductServiceModel.class);
    }

    @Override
    public ProductServiceModel findProductById(String id) {
        Product product =
                this.productRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("No such user"));
        return this.modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public List<ProductServiceModel> findAllProducts() {
        return this.productRepository.findAll().stream()
                .map(p->this.modelMapper.map(p,ProductServiceModel.class)).collect(Collectors.toList());
    }

    @Override
    public void editProduct(ProductServiceModel product, EditProductViewModel editProductViewModel) {
        product.setName(editProductViewModel.getName());
        product.setPrice(editProductViewModel.getPrice());
        product.setDescription(editProductViewModel.getDescription());
        product.setCategory(this.categoryService.findCategoryByName(editProductViewModel.getCategory()));

        Product entity = this.modelMapper.map(product, Product.class);
        entity.setCategory(this.modelMapper.map(product.getCategory(), Category.class));

        this.productRepository.save(entity);
    }

    @Override
    public ProductServiceModel deleteProduct(String id) {
        ProductServiceModel product = this.findProductById(id);
        Product productToDelete = this.modelMapper.map(product,Product.class);
        productToDelete.setCategory(this.modelMapper.map(product.getCategory(),Category.class));

        this.productRepository.delete(productToDelete);

        return product;
    }
}

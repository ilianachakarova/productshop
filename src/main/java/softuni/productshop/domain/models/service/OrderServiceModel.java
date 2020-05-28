package softuni.productshop.domain.models.service;

import softuni.productshop.domain.entities.Product;
import softuni.productshop.domain.entities.User;

public class OrderServiceModel extends  BaseServiceModel{
    private User user;
    private Product product;

    public OrderServiceModel() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

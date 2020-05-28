package softuni.productshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.productshop.domain.entities.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
}

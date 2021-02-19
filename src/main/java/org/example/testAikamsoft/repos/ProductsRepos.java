package org.example.testAikamsoft.repos;

import org.example.testAikamsoft.domain.Product;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface ProductsRepos extends CrudRepository<Product, Integer> {
    Product findByProductName(String productName);
}

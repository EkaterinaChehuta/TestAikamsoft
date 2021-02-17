package org.example.testAikamsoft.repos;

import org.example.testAikamsoft.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductsRepos extends CrudRepository<Product, Integer> {
}

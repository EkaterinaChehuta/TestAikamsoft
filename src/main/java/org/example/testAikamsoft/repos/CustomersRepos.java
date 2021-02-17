package org.example.testAikamsoft.repos;

import org.example.testAikamsoft.domain.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomersRepos extends CrudRepository<Customer, Integer> {
}

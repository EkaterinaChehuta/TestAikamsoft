package org.example.testAikamsoft.repos;

import org.example.testAikamsoft.domain.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomersRepos extends CrudRepository<Customer, Integer> {
    List<Customer> findByLastname(String lastname);
}

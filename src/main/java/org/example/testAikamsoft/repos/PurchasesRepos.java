package org.example.testAikamsoft.repos;

import org.example.testAikamsoft.domain.Product;
import org.example.testAikamsoft.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PurchasesRepos extends JpaRepository<Purchase,Integer> {
    List<Purchase> findByProduct(Product product);
//    List<Purchase> findByDateBetween(Date startDate, Date endDate);
}

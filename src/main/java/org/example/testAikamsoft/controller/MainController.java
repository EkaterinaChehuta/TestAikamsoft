package org.example.testAikamsoft.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.testAikamsoft.domain.Customer;
import org.example.testAikamsoft.domain.Product;
import org.example.testAikamsoft.repos.CustomersRepos;
import org.example.testAikamsoft.repos.ProductsRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@RestController
public class MainController {
    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final CustomersRepos customersRepos;
    private final ProductsRepos productsRepos;

    @Autowired
    public MainController(CustomersRepos customersRepos, ProductsRepos productsRepos) {
        this.customersRepos = customersRepos;
        this.productsRepos = productsRepos;
    }

    @RequestMapping("saveCustomers/{url}")
    public void saveCustomers(@PathVariable("url") String string){
        URL url = this.getClass().getClassLoader().getResource(string);

        if(url != null){
            File jsonFile = new File(url.getFile());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Customer> customerList = objectMapper.readValue(jsonFile, new TypeReference<List<Customer>>() {});
                customersRepos.saveAll(customerList);
                logger.info("Save.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("url is null");
        }
    }

    @RequestMapping("saveProducts/{url}")
    public void saveProducts(@PathVariable("url") String string){
        URL url = this.getClass().getClassLoader().getResource(string);

        if(url != null){
            File jsonFile = new File(url.getFile());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Product> productList = objectMapper.readValue(jsonFile, new TypeReference<List<Product>>() {});
                productsRepos.saveAll(productList);
                logger.info("Save.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("url is null");
        }
    }
}


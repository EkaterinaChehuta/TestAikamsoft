package org.example.testAikamsoft.controller;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.testAikamsoft.domain.Customer;
import org.example.testAikamsoft.domain.Product;
import org.example.testAikamsoft.domain.Purchase;
import org.example.testAikamsoft.repos.CustomersRepos;
import org.example.testAikamsoft.repos.ProductsRepos;
import org.example.testAikamsoft.repos.PurchasesRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SearchController {
    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final CustomersRepos customersRepos;
    private final ProductsRepos productsRepos;
    private final PurchasesRepos purchasesRepos;

    public SearchController(CustomersRepos customersRepos, ProductsRepos productsRepos, PurchasesRepos purchasesRepos) {
        this.customersRepos = customersRepos;
        this.productsRepos = productsRepos;
        this.purchasesRepos = purchasesRepos;
    }

    @RequestMapping("search/{fileReadSearch}/{fileWriteSearch}")
    public void search(@PathVariable("fileReadSearch") String fileReadSearch,
                       @PathVariable("fileWriteSearch") String fileWriteSearch) {
        URL url = this.getClass().getClassLoader().getResource(fileReadSearch);

        if (url != null && fileWriteSearch != null) {
            File jsonFile = new File(url.getFile());
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode node = objectMapper.readValue(jsonFile, JsonNode.class);

                List<Map<String, Object>> criterias = new ArrayList<>();
                Map<String, Object> results = new LinkedHashMap<>();

                if (node.findValue("lastname").asText() != null) { //todo: при null падает
                    criterias.add(saveSearchLastname(node.findValue("lastname").asText()));
                }

                if (node.findValue("productName").asText() != null
                        && node.findValue("count").asInt() > 0) {
                    criterias.add(saveSearchProductNameAndCount(node.findValue("productName").asText(), node.findValue("count").asInt()));
                }

                if(node.findValue("minExpenses").asDouble() > 0){
                    criterias.add(saveRangeSearch(node.findValue("minExpenses").asDouble(), node.findValue("maxExpenses").asDouble()));
                }

                if(node.findValue("badCustomers").asInt() > 0){
                    criterias.add(saveSearchBadCustomers(node.findValue("badCustomers").asInt()));
                }

                results.put("type", "search");
                results.put("results", criterias);

                ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());

                File newFile = new File("C:\\Users\\katya\\IdeaProjects\\TestAikamsoft\\src\\main\\resources\\" + fileWriteSearch);

                writer.writeValue(newFile, results);

                logger.info("Save file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("FileRead or fileWriter is null");
        }
    }

    private Map<String, Object> saveSearchLastname(String lastname) {
        List<Customer> customers = customersRepos.findByLastname(lastname);

        Map<String, Object> map = new HashMap<>();
        map.put("lastname", lastname);

        Map<String, Object> criteria = new LinkedHashMap<>();
        criteria.put("criteria", map);
        criteria.put("result", customers);

        return criteria;
    }

    private Map<String, Object> saveSearchProductNameAndCount(String productName, int count) {
        List<Purchase> purchaseList = purchasesRepos.findByProduct(productsRepos.findByProductName(productName));

        Map<Customer, List<Purchase>> map1 = purchaseList.stream()
                .collect(Collectors.groupingBy(Purchase::getCustomer));

        List<Customer> customers = map1.entrySet().parallelStream()
                .filter(e -> e.getValue().size() >= count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("minTimes", count);
        map.put("productName", productName);

        Map<String, Object> criteria = new LinkedHashMap<>();
        criteria.put("criteria", map);
        criteria.put("result", customers);

        return criteria;
    }

    private Map<String, Object> saveRangeSearch(double minExpenses, double maxExpenses) {
        List<Purchase> purchases = purchasesRepos.findAll();

        Map<Customer, List<Product>> map1 = purchases.stream()
                .collect(Collectors.groupingBy(Purchase::getCustomer, Collectors.mapping(Purchase::getProduct, Collectors.toList())));

        List<Customer> customers = map1.entrySet().parallelStream()
                .filter(e -> e.getValue().stream()
                        .mapToDouble(Product::getPrice).sum() >= minExpenses &&
                        e.getValue().stream()
                                .mapToDouble(Product::getPrice).sum() <= maxExpenses)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("maxExpenses", maxExpenses);
        map.put("minExpenses", minExpenses);

        Map<String, Object> criteria = new LinkedHashMap<>();
        criteria.put("criteria", map);
        criteria.put("result", customers);

        return criteria;
    }

    private Map<String, Object> saveSearchBadCustomers(int count){
        List<Purchase> purchases = purchasesRepos.findAll();

        Map<Customer, List<Product>> map1 = purchases.stream()
                .collect(Collectors.groupingBy(Purchase::getCustomer,
                        Collectors.mapping(Purchase::getProduct, Collectors.toList())));

        List<Customer> customers = map1.entrySet().parallelStream()
                .sorted(Comparator.comparing(e -> e.getValue().stream()
                        .mapToDouble(Product::getPrice).sum()))
                .map(Map.Entry::getKey)
                .limit(count)
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("badCustomers", count);

        Map<String, Object> criteria = new LinkedHashMap<>();
        criteria.put("criteria", map);
        criteria.put("result", customers);

        return criteria;
    }
}

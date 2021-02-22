package org.example.testAikamsoft.controller;


import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.testAikamsoft.domain.Customer;
import org.example.testAikamsoft.domain.Product;
import org.example.testAikamsoft.domain.Purchase;
import org.example.testAikamsoft.repos.PurchasesRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class StatController {
    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final PurchasesRepos purchasesRepos;

    public StatController(PurchasesRepos purchasesRepos) {
        this.purchasesRepos = purchasesRepos;
    }

    @RequestMapping("stat/{fileReadStat}/{fileWriteStat}")
    public void stat(@PathVariable("fileReadStat") String fileReadStat,
                     @PathVariable("fileWriteStat") String fileWriteStat) {
        URL url = this.getClass().getClassLoader().getResource(fileReadStat);

        if (url != null && fileWriteStat != null) {
            File jsonFile = new File(url.getFile());
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode node = objectMapper.readValue(jsonFile, JsonNode.class);

                List<Map<String, Object>> customers = new ArrayList<>();

                Map<String, Object> results = new LinkedHashMap<>();

                int totalDays = 0;

                if (node.findValue("startDate").asText() != null &&
                        node.findValue("endDate").asText() != null) { //todo: при null падает

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    Date startDate = format.parse(node.findValue("startDate").asText());
                    Date endDate = format.parse(node.findValue("endDate").asText());

                    totalDays = (int) ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());

                    List<Purchase> purchasesList = purchasesRepos.findByPurchaseDateBetween(startDate, endDate);

                    Map<Customer, List<Purchase>> map = purchasesList.stream()
                            .collect(Collectors.groupingBy(Purchase::getCustomer));

                    for (Map.Entry<Customer, List<Purchase>> items : map.entrySet()) {
                        customers.add(saveStat(items));
                    }
                }

                results.put("type", "stat");
                results.put("totalDays", totalDays);
                results.put("customers", customers);

                ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());

                File newFile = new File("C:\\Users\\katya\\IdeaProjects\\TestAikamsoft\\src\\main\\resources\\" + fileWriteStat);

                writer.writeValue(newFile, results);

                logger.info("Save file.");
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("FileRead or fileWriter is null");
        }
    }

    private Map<String, Object> saveStat(Map.Entry<Customer, List<Purchase>> productsList) {
        String name = productsList.getKey().getFirstname().concat(" ").concat(productsList.getKey().getLastname());

        Map<Product, List<Purchase>> products = productsList.getValue().stream()
                .collect(Collectors.groupingBy(Purchase::getProduct));

        List<Object> purchases = new ArrayList<>();

        for (Map.Entry<Product, List<Purchase>> product : products.entrySet()) {
            Map<String, String> productMap = new LinkedHashMap<>();

            String productName = product.getKey().getProductName();
            double sum = product.getValue().stream().mapToDouble(e -> e.getProduct().getPrice()).sum();

            productMap.put("productName", productName);
            productMap.put("expenses", Double.toString(sum));
            purchases.add(productMap);
        }

        Map<String, Object> customers = new LinkedHashMap<>();
        customers.put("name", name);
        customers.put("purchases", purchases);

        return customers;
    }
}

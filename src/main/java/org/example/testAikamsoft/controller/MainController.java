package org.example.testAikamsoft.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.example.testAikamsoft.domain.Customer;
import org.example.testAikamsoft.domain.Product;
import org.example.testAikamsoft.domain.Purchase;
import org.example.testAikamsoft.repos.CustomersRepos;
import org.example.testAikamsoft.repos.ProductsRepos;
import org.example.testAikamsoft.repos.PurchasesRepos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class MainController {
    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final CustomersRepos customersRepos;
    private final ProductsRepos productsRepos;
    private final PurchasesRepos purchasesRepos;

    @Autowired
    public MainController(CustomersRepos customersRepos, ProductsRepos productsRepos, PurchasesRepos purchasesRepos) {
        this.customersRepos = customersRepos;
        this.productsRepos = productsRepos;
        this.purchasesRepos = purchasesRepos;
    }

    @RequestMapping("saveCustomers/{url}")
    public void saveCustomers(@PathVariable("url") String string) {
        URL url = this.getClass().getClassLoader().getResource(string);

        if (url != null) {
            File jsonFile = new File(url.getFile());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Customer> customerList = objectMapper.readValue(jsonFile, new TypeReference<List<Customer>>() {
                });
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
    public void saveProducts(@PathVariable("url") String string) {
        URL url = this.getClass().getClassLoader().getResource(string);

        if (url != null) {
            File jsonFile = new File(url.getFile());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Product> productList = objectMapper.readValue(jsonFile, new TypeReference<List<Product>>() {
                });
                productsRepos.saveAll(productList);
                logger.info("Save.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("url is null");
        }
    }

    @RequestMapping("savePurchases/{url}")
    public void savePurchases(@PathVariable("url") String string) {
        URL url = this.getClass().getClassLoader().getResource(string);

        if (url != null) {
            File jsonFile = new File(url.getFile());
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<Purchase> purchaseList = objectMapper.readValue(jsonFile, new TypeReference<List<Purchase>>() {
                });
                purchasesRepos.saveAll(purchaseList);
                logger.info("Save.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("url is null");
        }
    }

    @RequestMapping("saveResults/{url}")
    public void saveResults(@PathVariable("url") String string) {
        URL url = this.getClass().getClassLoader().getResource(string);

        if (url != null) {
            File jsonFile = new File(url.getFile());
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode node = objectMapper.readValue(jsonFile, JsonNode.class);

                List<Map<String, Object>> criterias = new ArrayList<>();
                Map<String, Object> results = new LinkedHashMap<>();

                if (node.findValue("lastname").asText() != null) {
                    criterias.add(saveCriteria1(node.findValue("lastname").asText()));
                }

                if (node.findValue("productName").asText() != null && node.findValue("count").asInt() != 0) {
                    criterias.add(saveCriteria2(node.findValue("productName").asText(), node.findValue("count").asInt()));
                }

                results.put("type", "search");
                results.put("results", criterias);

                ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());

                writer.writeValue(new File("C:\\Users\\katya\\IdeaProjects\\TestAikamsoft\\src\\main\\resources\\array.json"), results);

                logger.info("Save file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("url is null");
        }
    }

    private Map<String, Object> saveCriteria1(String lastname) {
        List<Customer> customers = customersRepos.findByLastname(lastname);

        Map<String, Object> map = new HashMap<>();
        map.put("lastname", lastname);

        Map<String, Object> criteria = new LinkedHashMap<>();
        criteria.put("criteria", map);
        criteria.put("result", customers);

        return criteria;
    }

    private Map<String, Object> saveCriteria2(String productName, int count) {
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
}

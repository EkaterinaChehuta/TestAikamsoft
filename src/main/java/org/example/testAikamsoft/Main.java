package org.example.testAikamsoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);

        /*ObjectMapper objectMapper = new ObjectMapper();

        Customer[] customers = new Customer[3];
        customers[1] = new Customer("Иванов", "Иван");
        customers[2] = new Customer("Иванова", "Ивана");

        String s = objectMapper.writeValueAsString(customers);
        System.out.println(s);*/
    }
}

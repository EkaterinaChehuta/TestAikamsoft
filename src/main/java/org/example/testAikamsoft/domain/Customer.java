package org.example.testAikamsoft.domain;

import javax.persistence.*;

@Entity
@Table(schema = "json", name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String lastname;
    private String firstname;

    public Customer() {
    }

    public Customer(String lastname, String firstname) {
        this.lastname = lastname;
        this.firstname = firstname;
    }

    public int getId() {
        return id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}

package de.etecture.opensource.dynamicrepositories.technologies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * represents a sample entity for test purposes.
 *
 * @author rhk
 */
public class Person {

    private String lastname;
    private String firstname;
    private int age;
    private List<Address> addresses = new ArrayList<>();

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }
}

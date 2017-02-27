package com.thirur.mergepatch.mediatypes;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation
public class Customer implements Identifiable<Long> {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

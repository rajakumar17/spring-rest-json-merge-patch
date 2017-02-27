package com.thirur.mergepatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirur.mergepatch.mediatypes.Customer;
import com.thirur.mergepatch.repository.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@ExposesResourceFor(Customer.class)
@RequestMapping(value = "/customers", produces = {MediaTypes.HAL_JSON_VALUE})
public class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private EntityLinks entityLinks;

    private ObjectMapper mapper = new ObjectMapper();


    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<Resources<Resource<Customer>>> getCustomers() {
        List<Resource> resources = new ArrayList<>();
        Iterable<com.thirur.mergepatch.domain.Customer> customers = repository.findAll();
        customers.forEach(custDomain -> {
            Customer custMT = new Customer();
            BeanUtils.copyProperties(custDomain, custMT);
            Resource<Customer> custResource = new Resource<Customer>(custMT);
            custResource.add(entityLinks.linkToSingleResource(custMT));
            resources.add(custResource);
       });
        Link customersLink = entityLinks.linkToCollectionResource(Customer.class).withSelfRel();
        return new ResponseEntity<>(new Resources(resources, customersLink), HttpStatus.OK);
    }

    @RequestMapping(value = "/{custId}", method = RequestMethod.GET)
    public HttpEntity<Resource<Customer>> getCustomer(@PathVariable Long custId) {
        com.thirur.mergepatch.domain.Customer custDomain = repository.findOne(custId);
        if(custDomain != null) {
            return getCustomerResourceHttpEntity(custDomain, false);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private HttpEntity<Resource<Customer>> getCustomerResourceHttpEntity(com.thirur.mergepatch.domain.Customer custDomain,
                                                                         boolean isCreate) {
        Customer custMT = new Customer();
        BeanUtils.copyProperties(custDomain, custMT);
        Resource<Customer> custResource = new Resource<Customer>(custMT);
        custResource.add(entityLinks.linkToSingleResource(custMT));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(entityLinks.linkToSingleResource(custMT).getHref()));
        HttpStatus status = HttpStatus.OK;
        if (isCreate) {
            status = HttpStatus.CREATED;
        }
        return new ResponseEntity<Resource<Customer>>(custResource, headers, status);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<Resource<Customer>> createCustomer(@RequestBody Customer custMT) {
        com.thirur.mergepatch.domain.Customer custDomain = new com.thirur.mergepatch.domain.Customer();
        BeanUtils.copyProperties(custMT, custDomain);
        com.thirur.mergepatch.domain.Customer newCustDomain = repository.save(custDomain);
        return getCustomerResourceHttpEntity(newCustDomain, true);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT, value = "/{custId}")
    public HttpEntity<Resource<Customer>> updateCustomer(@PathVariable Long custId, @RequestBody Customer custMT) {
        com.thirur.mergepatch.domain.Customer custDomain = repository.findOne(custId);
        if(custDomain != null) {
            BeanUtils.copyProperties(custMT, custDomain, "id");
            com.thirur.mergepatch.domain.Customer updatedCust = repository.save(custDomain);
            return getCustomerResourceHttpEntity(updatedCust, false);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/{custId}")
    public HttpEntity<?> deleteCustomer(@PathVariable Long custId) {
        if(custId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        com.thirur.mergepatch.domain.Customer cust = repository.findOne(custId);
        if(cust != null) {
            repository.delete(custId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PATCH, value = "/{custId}", consumes = {"application/merge-patch+json"})
    public HttpEntity<Resource<Customer>> patchCustomer(@PathVariable Long custId, @RequestBody String data) throws Exception {
        if(custId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        com.thirur.mergepatch.domain.Customer cust = repository.findOne(custId);
        if(cust != null) {
            cust = JsonMergePatchUtils.mergePatch(cust, data, com.thirur.mergepatch.domain.Customer.class);
            com.thirur.mergepatch.domain.Customer patchedCust = repository.save(cust);
            return getCustomerResourceHttpEntity(patchedCust, false);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}

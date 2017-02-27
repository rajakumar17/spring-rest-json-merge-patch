package com.thirur.mergepatch.controller;

import com.thirur.mergepatch.mediatypes.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.thirur.mergepatch.controller.TestUtils.STANDARD_HEADERS;
import static com.thirur.mergepatch.controller.TestUtils.getObjectFromResult;
import static com.thirur.mergepatch.controller.TestUtils.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomerControllerTest {

    private MockMvc mockMvc;

    private String payload;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        String path = "C:\\temp\\spring-rest-json-merge-patch\\src\\test\\resources\\testdata.json";
        payload = readFromFile(path);
    }

    private String readFromFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    @Test
    public void customerTest() throws Exception {
        MvcResult result = mockMvc.perform(post("/customers").headers(STANDARD_HEADERS).content(payload)).
                andExpect(status().isCreated()).andReturn();
        Customer custMT = getObjectFromResult(result, Customer.class);
        System.out.println("Customer created :" + custMT.toString());

        //get
        result = mockMvc.perform(get("/customers/1").headers(STANDARD_HEADERS)).
                andExpect(status().isOk()).andReturn();
        custMT = getObjectFromResult(result, Customer.class);
        Assert.assertEquals("Incorrect Customer fetched", custMT.getId().longValue(), 1l);

        //update
        custMT.setEmail("rthiruva@opentext.com");
        result = mockMvc.perform(put("/customers/1").headers(STANDARD_HEADERS).content(toJson(custMT))).
                andExpect(status().isOk()).andReturn();
        custMT = getObjectFromResult(result, Customer.class);
        Assert.assertEquals("Customer updated incorrectly", custMT.getEmail(), "rthiruva@opentext.com");

        //patch
        String data = readFromFile("C:\\temp\\spring-rest-json-merge-patch\\src\\test\\resources\\patchtestdata.json");
        result = mockMvc.perform(patch("/customers/1").header(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON.toString())
                .header(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json").content(data))
                .andExpect(status().isOk()).andReturn();
        custMT = getObjectFromResult(result, Customer.class);
        Assert.assertEquals("Customer updated incorrectly", custMT.getEmail(), "thirur@dell.com");

        //delete
        mockMvc.perform(delete("/customers/1").headers(STANDARD_HEADERS)).andExpect(status().isOk());
        mockMvc.perform(get("/customers/1").headers(STANDARD_HEADERS)).andExpect(status().isNotFound());
    }

}

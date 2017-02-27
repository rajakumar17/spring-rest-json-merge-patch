package com.thirur.mergepatch.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Thirur on 9/1/2016.
 */
public class TestUtils {

    public static final HttpHeaders STANDARD_HEADERS = new HttpHeaders();

    static {
        STANDARD_HEADERS.add(HttpHeaders.ACCEPT, MediaTypes.HAL_JSON.toString());
        STANDARD_HEADERS.add(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON.toString());
    }


    public static String toJson(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            @SuppressWarnings("unchecked")
            Map<String, Object> beanMap = mapper.convertValue(obj, Map.class);

            // Filter out id property if exists:
//            if (beanMap.containsKey("id")) {
//                beanMap.remove("id");
//            }
            return mapper.writeValueAsString(beanMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T  getObjectFromResult(MvcResult result, Class<T> objectType) {
        String content = getResponseString(result);
        return getTfromJsonString(content, objectType);
    }

    public static <T> T getTfromJsonString(String jsonString, Class<T> objectType) {
        try {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            return mapper.readValue(jsonString, objectType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert json string '" + jsonString + "' to java object of type " + objectType.getName(), e);
        }
    }

    public static String getResponseString(MvcResult result) {
        try {
            return result.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to read response.", e);
        }
    }
}

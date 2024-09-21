package org.example;

import org.example.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class RestTemplateTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

    @Test
    void getDelayedData() {

        currentTenant.set("jozo1");

        System.out.println(Thread.currentThread().getName());
        UserResponse result = restTemplate.getForObject("https://reqres.in/api/users?delay=3", UserResponse.class);
        System.out.println(result.getData().get(0).getId());
        System.out.println(result.getData().get(0).getEmail());
        System.out.println(result.getData().get(0).getFirst_name());
        System.out.println(result.getData().get(0).getLast_name());
        System.out.println(Thread.currentThread().getName());

        System.out.print("Tenant: ");
        System.out.println(currentTenant.get());
    }
}

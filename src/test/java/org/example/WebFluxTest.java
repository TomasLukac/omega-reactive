package org.example;

import org.example.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebFluxTest {

    private final WebClient webClient = WebClient.builder().build();
    private static final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

    @Test
    void getDelayedDataNonBlocking() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        Mono<UserResponse> result = webClient
                .get()
                .uri("https://reqres.in/api/users?delay=3")
                .retrieve()
                .bodyToMono(UserResponse.class);
        result.doOnNext(data -> {
            System.out.println(Thread.currentThread().getName());
            System.out.print("Id: ");
            System.out.println(data.getData().get(0).getId());
            System.out.print("Email: ");
            System.out.println(data.getData().get(0).getEmail());
            System.out.print("First name: ");
            System.out.println(data.getData().get(0).getFirst_name());
            System.out.print("Last name: ");
            System.out.println(data.getData().get(0).getLast_name());
        }).subscribe();

        System.out.println(Thread.currentThread().getName());

        Thread.sleep(5000);
    }

    @Test
    void getDelayedDataBlocking() {
        System.out.println(Thread.currentThread().getName());
        UserResponse result = webClient
                .get()
                .uri("https://reqres.in/api/users?delay=3")
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
        System.out.println(Thread.currentThread().getName());
        System.out.println(result.getData().get(0).getId());
        System.out.println(result.getData().get(0).getEmail());
        System.out.println(result.getData().get(0).getFirst_name());
        System.out.println(result.getData().get(0).getLast_name());
        System.out.println(Thread.currentThread().getName());
    }

    @Test
    void getDelayedDataWithContext() throws InterruptedException {

        System.out.println(Thread.currentThread().getName());

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Scheduler threadPoolScheduler = Schedulers.fromExecutor(executorService);

        currentTenant.set("jozo1");

        Mono.just("hello")
                .flatMap(s -> {
                    printTenant();
                    return Mono.just(s);
                })
                .doOnTerminate(() -> {
                    // Clear tenant context as would be done in a request lifecycle
                    currentTenant.remove();
                })
                .subscribeOn(threadPoolScheduler)
                .subscribe();
        Thread.sleep(5000);
        executorService.shutdown();
    }

    private void printTenant() {
        System.out.println(Thread.currentThread().getName());
        System.out.print("Tenant: ");
        System.out.println(currentTenant.get());
    }
}

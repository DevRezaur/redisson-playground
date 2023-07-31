package com.devrezaur.main;

import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class NumberTest extends BaseTestClass {

    @Test
    public void numberIncreaseTest() {
        RAtomicLongReactive atomicLong = this.client.getAtomicLong("user:1:marks");

        // Setting the initial value to 0
        atomicLong.set(0).block();

        // Incrementing the value 5 times
        Flux<Long> flux = Flux.range(1, 5)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(i -> atomicLong.incrementAndGet());

        // Testing if the value is being updated accordingly
        StepVerifier
                .create(flux)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .expectNext(5L)
                .verifyComplete();
    }
}

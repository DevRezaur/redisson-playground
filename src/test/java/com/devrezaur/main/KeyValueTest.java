package com.devrezaur.main;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class KeyValueTest extends BaseTestClass {

    @Test
    public void keyValueAccessTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Fahim Faysal");
        Mono<String> get = bucket.get();

        // Testing if the data is saved to redis properly
        StepVerifier
                .create(set.then(get))
                .expectNext("Fahim Faysal")
                .verifyComplete();
    }

    @Test
    public void keyValueExpiryTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Fahim Faysal", 3, TimeUnit.SECONDS);
        Mono<String> get = bucket.get();

        // Testing if the key is saved to redis
        StepVerifier
                .create(set.then(get))
                .expectNextCount(1)
                .verifyComplete();

        // Testing if the key exist in redis after 3 seconds
        sleep(3000);

        StepVerifier
                .create(get)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void keyValueExtendExpiryTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Fahim Faysal", 1, TimeUnit.SECONDS);
        Mono<String> get = bucket.get();

        // Testing if the key is saved to redis
        StepVerifier
                .create(set.then(get))
                .expectNextCount(1)
                .verifyComplete();

        // Printing the expiry time in console
        bucket.remainTimeToLive().doOnNext(System.out::println).block();

        // Extend the expiry time
        Mono<Boolean> extend = bucket.expire(Duration.ofSeconds(3));

        // Testing if the expiry time has successfully extended
        StepVerifier
                .create(extend)
                .expectNext(true)
                .verifyComplete();

        // Printing the expiry time in console after extending the time
        bucket.remainTimeToLive().doOnNext(System.out::println).block();

        // Testing if the key exist in redis after 2 seconds
        sleep(2000);

        StepVerifier
                .create(get)
                .expectNextCount(1)
                .verifyComplete();
    }

}

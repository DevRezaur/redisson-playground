package com.devrezaur.main;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class KeyValueTest extends BaseTestClass {

    @Test
    public void keyValueAccessTest() {
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Fahim Faysal");
        Mono<String> get = bucket.get().doOnNext(System.out::println);

        StepVerifier
                .create(set.then(get))
                .expectNext("Fahim Faysal")
                .verifyComplete();
    }

}

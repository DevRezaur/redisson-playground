package com.devrezaur.main;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

public class BucketAsMapTest extends BaseTestClass {

    @Test
    public void bucketAsMapTest() {
        // Set some data in redis
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        bucket.set("Fahim Faysal").block();

        bucket = this.client.getBucket("user:2:name", StringCodec.INSTANCE);
        bucket.set("Rezaur Rahman").block();

        bucket = this.client.getBucket("user:3:name", StringCodec.INSTANCE);
        bucket.set("Piyal Ahmed").block();

        // Get multiple key-value data as map from redis
        Mono<Map<String, Object>> get = this.client.getBuckets(StringCodec.INSTANCE)
                .get("user:1:name", "user:2:name", "user:3:name");

        // Test the retrieved values from redis
        StepVerifier
                .create(get)
                .expectNext(getUserMap())
                .verifyComplete();

    }

    private HashMap<String, Object> getUserMap() {
        HashMap<String, Object> user = new HashMap<>();
        user.put("user:1:name", "Fahim Faysal");
        user.put("user:2:name", "Rezaur Rahman");
        user.put("user:3:name", "Piyal Ahmed");
        return user;
    }
}

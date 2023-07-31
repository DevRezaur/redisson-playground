package com.devrezaur.main;

import com.devrezaur.main.model.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

public class KeyValueObjectTest extends BaseTestClass {

    @Test
    public void keyValueObjectTest() {
        Student student = new Student("Fahim", 20, "Dhaka", Arrays.asList("Math", "Geography"));
        RBucketReactive<Student> bucket = this.client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));
        Mono<Void> set = bucket.set(student);
        Mono<Student> get = bucket.get();

        // Testing if the data is saved to redis properly
        StepVerifier
                .create(set.then(get))
                .expectNext(student)
                .verifyComplete();
    }
}

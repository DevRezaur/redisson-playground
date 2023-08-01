package com.devrezaur.main;

import com.devrezaur.main.config.RedissonConfig;
import com.devrezaur.main.model.Student;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

/**
 * First run method "appServer1" and check the console log.
 * While "appServer1" is running, run "appServer2" and check the console log for "appServer1".
 * --
 * To verify that the data is coming from the local cache, kill the redis server.
 * Method "appServer1" will still fetch data & print them to the console.
 * This means data is coming from the local cache. Not redis server.
 * --
 * You can change the "SyncStrategy" & "ReconnectionStrategy" value.
 * And observe the behaviour change of "appServer1" method.
 */
public class MapLocalCacheTest extends BaseTestClass {

    private RLocalCachedMap<Integer, Student> map;

    @BeforeAll
    public void setClient() {
        RedissonConfig redissonConfig = new RedissonConfig();
        RedissonClient redissonClient = redissonConfig.getRedissonClient();

        LocalCachedMapOptions<Integer, Student> options =
                LocalCachedMapOptions
                        .<Integer, Student>defaults()
                        .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                        .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE);

        map = redissonClient.getLocalCachedMap(
                "students",
                new TypedJsonJacksonCodec(Integer.class, Student.class),
                options)
        ;
    }

    @AfterAll
    public void shutDown() {}

    @Test
    public void appServer1() {
        Student student1 = new Student("Rezaur Rahman", 25, "Dhaka", List.of("Physics", "Biology"));

        this.map.put(1, student1);

        Flux.interval(Duration.ofSeconds(1)).doOnNext(i -> System.out.println(i + " -> " + map.get(1))).subscribe();

        sleep(60000);
    }

    @Test
    public void appServer2() {
        Student student1 = new Student("Rezaur Rahman - Updated", 25, "Dhaka", List.of("Physics", "Biology"));
        this.map.put(1, student1);
    }

}

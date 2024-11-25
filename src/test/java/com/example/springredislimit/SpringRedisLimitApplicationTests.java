package com.example.springredislimit;

import com.example.springredislimit.limitService.RateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

//@RunWith(SpringRunner.class)
@SpringBootTest
class SpringRedisLimitApplicationTests {

    @Autowired
    RateLimiterService rateLimiterService;
    //使用Jmeter进行压测
    @Test
    void contextLoads() {

    }

    @Test
    public void testRedisLimiter() throws InterruptedException{
        for(int i = 1; i <= 10000; i++){
            boolean b = rateLimiterService.allowRequest("2",10000, Duration.ofSeconds(60));
            System.out.println(String.format("第 %s 次请求，是否允许执行： %s", i, b));

        }
    }

}

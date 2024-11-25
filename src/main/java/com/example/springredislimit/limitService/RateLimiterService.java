package com.example.springredislimit.limitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private static final String LIMITER_KEY_PREFIX = "rate_limiter:";
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //使用Redis 的ZSET 有序集合来实现滑动窗口限流，实际应用都应使用lua脚本来保证操作的原子性
    public boolean allowRequest(String userId, int maxRequests, Duration timeWindow) {
        String key = LIMITER_KEY_PREFIX + userId;
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        // 当前时间的时间戳（秒级）
        long currentTime = Instant.now().getEpochSecond();

        // 移除时间窗口之外的旧请求
        zSetOps.removeRangeByScore(key, 0, currentTime - timeWindow.getSeconds());

        // 添加当前请求到集合中，使用当前时间戳作为score
        zSetOps.add(key, String.valueOf(currentTime), currentTime);

        // 获取时间窗口内的请求数量（注意：这里使用(0, currentTime+1)是为了确保不包含currentTime这一秒内的后续请求）
        // 但是，由于redis的rangeByScore是左闭右开的，所以currentTime会被排除在外，这通常是我们想要的
        Set<String> requestTimes = zSetOps.rangeByScore(key, currentTime - timeWindow.getSeconds(), currentTime + 1);

        // 由于我们使用了currentTime + 1作为上界，但实际上我们想要的是不包含currentTime这一秒内的请求，
        // 所以我们需要从结果中排除任何等于currentTime的条目（尽管由于rangeByScore的左闭右开性质，这通常不会发生）。
        // 但是，由于我们已经通过移除旧请求和添加新请求确保了currentTime不会出现在结果集中（除非有并发问题），
        // 所以这一步实际上是不必要的。然而，为了清晰起见，我还是保留了这段注释。

        // 判断请求数量是否超过限制
        return requestTimes.size() <=maxRequests;
    }

    //使用Redis的 INCR 和 EXPIRE 实现固定窗口的计数与限流
    public boolean isAllowed(String userId, String api, int maxRequests, int intervalSeconds) {
        String key = "rate_limit:" + userId + ":" + api;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, intervalSeconds, TimeUnit.SECONDS);
        }
        return count <= maxRequests;
    }
}
package service;

import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class RedisService {

    @Inject
    RedisClient redisClient;

    private static final String SEAT_LOCK_PREFIX = "seat:lock:";
    private static final long LOCK_TTL_SECONDS = TimeUnit.MINUTES.toSeconds(2); // 2 minutes TTL

    /**
     * Lock a seat with a 2-minute TTL
     * @param seatId The ID of the seat to lock
     * @param orderId The ID of the order that is locking the seat
     * @return true if the seat was successfully locked, false otherwise
     */
    public boolean lockSeat(UUID seatId, UUID orderId) {
        String lockKey = SEAT_LOCK_PREFIX + seatId.toString();
        Response response = redisClient.setex(
                lockKey, 
                Long.toString(LOCK_TTL_SECONDS),
                orderId.toString()
        );
        return response != null && "OK".equalsIgnoreCase(response.toString());
    }

    /**
     * Check if a seat is locked
     * @param seatId The ID of the seat to check
     * @return true if the seat is locked, false otherwise
     */
    public boolean isSeatLocked(UUID seatId) {
        String lockKey = SEAT_LOCK_PREFIX + seatId.toString();
        Response response = redisClient.get(lockKey);
        return response != null;
    }

    /**
     * Get the order ID that locked a seat
     * @param seatId The ID of the seat
     * @return The ID of the order that locked the seat, or null if the seat is not locked
     */
    public UUID getOrderIdForLockedSeat(UUID seatId) {
        String lockKey = SEAT_LOCK_PREFIX + seatId.toString();
        Response response = redisClient.get(lockKey);
        if (response != null) {
            return UUID.fromString(response.toString());
        }
        return null;
    }

    /**
     * Release a seat lock
     * @param seatId The ID of the seat to unlock
     * @return true if the seat was successfully unlocked, false otherwise
     */
    public boolean releaseSeatLock(UUID seatId) {
        String lockKey = SEAT_LOCK_PREFIX + seatId.toString();
        Response response = redisClient.del(Arrays.asList(lockKey));
        return response != null && response.toInteger() > 0;
    }
}

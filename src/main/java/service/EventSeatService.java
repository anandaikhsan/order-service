package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.EventEntity;
import entity.SeatEntity;
import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for retrieving event seat availability information.
 */
@ApplicationScoped
@Slf4j
public class EventSeatService {

    private static final String EVENT_KEY_PREFIX = "event:";

    @Inject
    RedisClient redisClient;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Get event details with seat availability for a specific event
     *
     * @param eventId The ID of the event
     * @return Map containing event details and seat availability, or null if event not found
     * @throws Exception if there's an error processing the request
     */
    @Transactional
    public Map<String, Object> getEventSeatAvailability(UUID eventId) throws Exception {
        // Get event info from cache
        String eventKey = EVENT_KEY_PREFIX + eventId.toString();
        Response vertxResponse = redisClient.get(eventKey);

        if (vertxResponse == null) {
            return null;
        }

        // Deserialize event from cache
        EventEntity event = objectMapper.readValue(vertxResponse.toString(), EventEntity.class);

        // Get seat availability from database
        List<SeatEntity> seats = SeatEntity.find("event.id", eventId).list();

        // Count seats by status
        Map<SeatEntity.SeatStatus, Integer> seatStatusCounts = new HashMap<>();
        for (SeatEntity.SeatStatus status : SeatEntity.SeatStatus.values()) {
            seatStatusCounts.put(status, 0);
        }

        for (SeatEntity seat : seats) {
            SeatEntity.SeatStatus status = seat.getStatus();
            seatStatusCounts.put(status, seatStatusCounts.get(status) + 1);
        }

        // Create response with event details and seat availability
        Map<String, Object> response = new HashMap<>();
        response.put("event", event);
        response.put("seatAvailability", seatStatusCounts);
        response.put("totalSeats", seats.size());

        return response;
    }
}
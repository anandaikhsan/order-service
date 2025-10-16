package service;

import entity.EventEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.runtime.StartupEvent;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service responsible for loading event data from the database to Redis.
 * This service is initialized at application startup.
 */
@ApplicationScoped
@Slf4j
public class EventDataLoader {

    private static final String EVENT_KEY_PREFIX = "event:";
    private static final String ALL_EVENTS_KEY = "events:all";
    private static final long CACHE_EXPIRATION_HOURS = 24;

    @Inject
    RedisClient redisClient;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Loads all event data from the database to Redis when the application starts.
     * 
     * @param event The startup event
     */
    public void onStart(@Observes StartupEvent event) {
        log.info("Loading event data from database to Redis...");
        loadEventsToRedis();
    }

    /**
     * Loads all events from the database to Redis.
     * This method can also be called manually to refresh the cache.
     */
    @Transactional
    public void loadEventsToRedis() {
        try {
            // Fetch all events from the database
            PanacheQuery<EventEntity> query = EventEntity.findAll();
            List<EventEntity> events = query.list();

            if (events.isEmpty()) {
                log.info("No events found in the database");
                return;
            }

            log.info("Found {} events in the database", events.size());

            // Store each event in Redis
            List<String> eventIds = new ArrayList<>();
            for (EventEntity event : events) {
                event.setSeats(null);
                String eventKey = EVENT_KEY_PREFIX + event.getId().toString();
                String eventJson = objectMapper.writeValueAsString(event);

                // Store the event in Redis with expiration
                redisClient.setex(eventKey, String.valueOf(TimeUnit.HOURS.toSeconds(CACHE_EXPIRATION_HOURS)), eventJson);
                eventIds.add(event.getId().toString());

                log.debug("Stored event {} in Redis", event.getId());
            }

            // Store the list of all event IDs
            String eventIdsJson = objectMapper.writeValueAsString(eventIds);
            redisClient.setex(ALL_EVENTS_KEY, String.valueOf(TimeUnit.HOURS.toSeconds(CACHE_EXPIRATION_HOURS)), eventIdsJson);

            log.info("Successfully loaded {} events to Redis", events.size());
        } catch (Exception e) {
            log.error("Error loading events to Redis", e);
        }
    }

    public void refreshEventCache() {
        log.info("Manually refreshing event cache in Redis...");
        loadEventsToRedis();
    }

}

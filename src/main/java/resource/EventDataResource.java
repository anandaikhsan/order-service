package resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import service.EventDataLoader;
import service.EventSeatService;

import java.util.Map;
import java.util.UUID;

/**
 * REST resource for managing event data in Redis cache.
 */
@Path("/api/events/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class EventDataResource {

    @Inject
    EventDataLoader eventDataLoader;

    @Inject
    EventSeatService eventSeatService;

    @POST
    @Path("/cache/refresh")
    public Response refreshEventCache() {
        try {
            eventDataLoader.refreshEventCache();
            return Response.ok().entity("{\"message\": \"Event cache refreshed successfully\"}").build();
        } catch (Exception e) {
            return Response.serverError().entity("{\"error\": \"Failed to refresh event cache: " + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Get event details with seat availability for a specific event
     * 
     * @param eventId The ID of the event
     * @return Response containing event details and seat availability
     */
    @GET
    @Path("/{eventId}/seats")
    @Transactional
    public Response getEventSeatAvailability(@PathParam("eventId") UUID eventId) {
        try {
            Map<String, Object> result = eventSeatService.getEventSeatAvailability(eventId);

            if (result == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Event not found in cache\"}").build();
            }

            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error getting event seat availability", e);
            return Response.serverError()
                    .entity("{\"error\": \"Failed to get event seat availability: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}

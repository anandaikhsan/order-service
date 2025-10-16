package resource;

import dto.OrderRequest;
import entity.SeatEntity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Path("/api/test/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderTestResource {

    private final ExecutorService executor = Executors.newFixedThreadPool(20); // configurable thread pool

    @Inject
    @RestClient
    OrderClient orderClient; // REST client for /api/events/order

    @POST
    @Path("/simulate")
    public CompletionStage<Response> simulateOrders() {
        List<CompletableFuture<Object>> futures = new ArrayList<>();

        List<SeatEntity> seats = SeatEntity.listAll();

        for (int i = 0; i < seats.size(); i++) {
            OrderRequest req = new OrderRequest();
            req.setSeatId(seats.get(i).getId()); // random seatId for simulation
            req.setCustomerName("User " + i);
            req.setCustomerEmail("user" + i + "@example.com");
            req.setCustomerPhone("+62812" + String.format("%06d", i));
            req.setPaymentMethod("credit_card");

            CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return orderClient.orderSeat(req);
                } catch (Exception e) {
                    return Map.of("error", e.getMessage(), "seatId", req.getSeatId());
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<Object> results = futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    return Response.ok(results).build();
                });
    }
}

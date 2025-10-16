package resource;

import dto.OrderRequest;
import dto.OrderResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import service.OrderService;

@Path("/api/events/order")
public class OrderResource {

    private static final Logger LOG = Logger.getLogger(OrderResource.class);

    @Inject
    OrderService orderService;

    @POST()
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response orderSeat(OrderRequest request) {
        LOG.info("Received order request for seat: " + request.getSeatId());

        try {
            OrderResponse response = orderService.createOrder(request);
            LOG.info("Order created successfully with order number: " + response.getOrderNumber());
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid order request: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Error creating order", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating order").build();
        }
    }
}

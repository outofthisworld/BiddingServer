package auction.processing;

/**
 * Created by daleappleby on 1/10/15.
 */
public class AuctionProcessorResponse {
    private Response response;

    /**
     * Instantiates a new Auction processor response.
     *
     * @param response the response
     */
    public AuctionProcessorResponse(Response response) {
        this.response = response;
    }

    /**
     * Get response.
     *
     * @return the response
     */
    public Response getResponse(){
        return response;
    }

    /**
     * The enum Response.
     */
    public enum Response{
        //Specifies the auction was already processed.
        AUCTION_PRE_PROCESSED,

        //The auction is already open, therefor cannot be processed.
        AUCTION_IS_OPEN,

        //An exception of some kind occurred whilst trying to compute the result of the callable
        COULD_NOT_COMPUTE,

        //The auction was successfully processed.
        COMPLETED
    }
}

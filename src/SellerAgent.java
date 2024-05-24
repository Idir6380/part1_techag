import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import java.util.ArrayList;
import java.util.List;
public class SellerAgent extends Agent {

    /*//produit à vendre
    public String product;
    // prix de resereve
    double reservePrice;
    // prix de depart
    double startingPrice;
    //liste des ag acheteurs
    List<AID> buyers = new ArrayList<>();
    //meilleure offre
    double bestOffer = -1;
    AID bestBuyer = null;*/

    @Override
    protected void setup() {
        super.setup();
        SharedState sharedState = new SharedState();
        sharedState.product = "A";
        sharedState.reservePrice = 100.0;
        sharedState.startingPrice = 50.0;
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            List<AID> buyerAIDs = (List<AID>) args[0];
            sharedState.buyers.addAll(buyerAIDs);
        }
        addBehaviour(new SubscriptionManager(sharedState));
        addBehaviour(new AuctionInitiator(this, sharedState));

    }
}

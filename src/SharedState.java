import jade.core.AID;
import java.util.ArrayList;
import java.util.List;

public class SharedState {
    //produit à vendre
    public String product;
    // prix de resereve
    public double reservePrice;
    // prix de depart
    public double startingPrice;
    //liste des ag acheteurs
    public List<AID> buyers = new ArrayList<>();
    //best offer
    public double bestOffer = -1;
    //meilleur acheteur
    public AID bestBuyer = null;
}
import jade.core.AID;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static List<AID> buyersAIDs = new ArrayList<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Créer une instance de la plateforme JADE
            Runtime runtime = Runtime.instance();
            ProfileImpl profileImpl = new ProfileImpl();
            profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
            profileImpl.setParameter(ProfileImpl.MAIN_PORT, "8888");
            profileImpl.setParameter(ProfileImpl.GUI, "true");
            AgentContainer mainContainer = runtime.createMainContainer(profileImpl);



            AgentController buyer1 = mainContainer.createNewAgent("Buyer1", BuyerAgent.class.getName(), new Object[]{"SellerAgent"});
            buyer1.start();
            buyersAIDs.add(new AID("Buyer1", AID.ISLOCALNAME));

            AgentController buyer2 = mainContainer.createNewAgent("Buyer2", BuyerAgent.class.getName(), new Object[]{"SellerAgent"});
            buyer2.start();
            buyersAIDs.add(new AID("Buyer2", AID.ISLOCALNAME));

            // Créer et lancer l'agent vendeur
            // passer la liste buyersAIDs comme argument
            AgentController sellerAgent = mainContainer.createNewAgent("SellerAgent", SellerAgent.class.getName(), new Object[] { buyersAIDs });
            sellerAgent.start();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

}

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class OfferCollector extends CyclicBehaviour {
    SharedState sharedState;
    MessageTemplate mt;
    int round = 0;
    long timeout = 1000000; //10sec

    public OfferCollector(Agent a, SharedState sharedState,MessageTemplate mt){
        super(a);
        this.sharedState = sharedState;
        this.mt = mt;
    }

    @Override
    public void action() {
        round++;
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null){
            double offer = Double.parseDouble(msg.getContent());
            AID sender = msg.getSender();

            if (offer > sharedState.bestOffer){
                sharedState.bestOffer = offer;
                sharedState.bestBuyer = sender;
                System.out.println("Nouvelle meilleure offre : " + sharedState.bestOffer + " par " + sender.getName());
            }else {
                System.out.println("Offre reçue : " + offer + " par " + sender.getName());
            }
            //envoyer le meilleur prix aux autre acheteurs
            ACLMessage updateMsg = msg.createReply();
            updateMsg.setPerformative(ACLMessage.INFORM);
            updateMsg.setContent(Double.toString(sharedState.bestOffer));
            for(AID buyer : sharedState.buyers){
                if (!buyer.equals(sender)){
                    updateMsg.addReceiver(buyer);
                }
            }
            myAgent.send(updateMsg);
        }else {
            //si auccune offre nest recu pendant 10sec
            block(timeout);
            //verification si lenchere doit finir
            if (sharedState.bestOffer < sharedState.reservePrice){
                //pas doffre > prixdereserve ench anulé
                System.out.println("Enchère annulée, aucune offre supérieure au prix de réserve");
                myAgent.removeBehaviour(this);
            }else {
                //vendre au meillzur acheteur
                ACLMessage confirm = new ACLMessage(ACLMessage.CONFIRM);
                confirm.addReceiver(sharedState.bestBuyer);
                confirm.setContent("Votre offre de " + sharedState.bestOffer + " a été acceptée pour le produit " + sharedState.product);
                myAgent.send(confirm);
                //notification des autres agents de la fermeture de lenchere
                ACLMessage end = new ACLMessage(ACLMessage.INFORM);
                for(AID buyer: sharedState.buyers){
                    if(!buyer.equals(sharedState.bestBuyer)){
                        end.addReceiver(buyer);
                    }
                }
                end.setContent("L'enchère est terminée, le produit a été vendu à " + sharedState.bestBuyer.getName() + " pour " + sharedState.bestOffer);
                System.out.println("L'enchère est terminée, le produit a été vendu à " + sharedState.bestBuyer.getName() + " pour " + sharedState.bestOffer);
                myAgent.send(end);

                // fin du comportement
                myAgent.removeBehaviour(this);
            }
        }
    }
}

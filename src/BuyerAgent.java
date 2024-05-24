import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;

import java.util.Random;

public class BuyerAgent extends Agent {
    private double maxPrice; // Le prix maximum que l'acheteur est prêt à payer
    private double currentOffer; // L'offre actuelle de l'acheteur
    private AID sellerAgent; // L'agent vendeur
    private Random rand = new Random();

    protected void setup() {
        // Initialiser le prix maximum et l'offre de départ
        maxPrice = 200.0; // Définir le prix maximum
        currentOffer = 180.0; // Définir l'offre initiale

        // Obtenir l'AID de l'agent vendeur (supposons qu'il est unique)
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            sellerAgent = new AID((String) args[0], AID.ISLOCALNAME);
        } else {
            System.out.println("Aucun agent vendeur spécifié");
            doDelete();
        }

        // Ajouter un comportement pour s'inscrire à l'enchère
        addBehaviour(new SubscribeToAuction());
    }

    // Comportement pour s'inscrire à l'enchère
    private class SubscribeToAuction extends OneShotBehaviour {
        public void action() {
            ACLMessage subscribe = new ACLMessage(ACLMessage.SUBSCRIBE);
            //subscribe.addReceiver(sellerAgent);
            subscribe.addReceiver(new AID("SellerAgent", AID.ISLOCALNAME));
            myAgent.send(subscribe);

            // Ajouter un comportement pour négocier avec le vendeur
            myAgent.addBehaviour(new NegotiationBehaviour());
        }
    }

    // Comportement pour négocier avec le vendeur
    private class NegotiationBehaviour extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchConversationId("auction");
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // Répondre au message reçu
                ACLMessage reply = msg.createReply();

                if (msg.getPerformative() == ACLMessage.CFP) {
                    // Invitation à faire une offre
                    double currentPrice = Double.parseDouble(msg.getContent());
                    if (currentPrice < maxPrice) {
                        // Faire une offre supérieure
                        currentOffer = currentPrice + rand.nextDouble() * 100.0; // Augmenter l'offre de façon aléatoire
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(Double.toString(currentOffer));
                    } else {
                        // Ne pas enchérir
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("Prix trop élevé");
                    }
                } else if (msg.getPerformative() == ACLMessage.INFORM) {
                    // Mise à jour du prix courant
                    double newPrice = Double.parseDouble(msg.getContent());
                    System.out.println("Nouveau prix : " + newPrice);
                } else {
                    // Répondre avec un message not-understood
                    reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                    reply.setContent("Message non compris");
                }

                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
}
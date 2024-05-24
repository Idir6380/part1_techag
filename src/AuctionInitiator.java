import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AuctionInitiator extends OneShotBehaviour {
    SharedState sharedState;
    Agent myAgent;
    public AuctionInitiator(Agent myAgent, SharedState sharedState){
        this.myAgent = myAgent;
        this.sharedState = sharedState;
    }
    @Override
    public void action() {
        //envoi du message dappel doffre aux acheteurs inscrits
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        for (AID buyer : sharedState.buyers){
            cfp.addReceiver(buyer);
        }
        cfp.setContent(sharedState.startingPrice+"");
        cfp.setConversationId("auction");
        cfp.setReplyWith("cfp"+System.currentTimeMillis());
        myAgent.send(cfp);

        // preparer un template pour recevoir les propositions
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId("auction"),
                MessageTemplate.MatchInReplyTo(cfp.getReplyWith())
        );
        myAgent.addBehaviour(new OfferCollector(myAgent, sharedState, mt));
    }
}

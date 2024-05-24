import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SubscriptionManager extends CyclicBehaviour {
    SharedState sharedState;
    public SubscriptionManager(SharedState sharedState){
        this.sharedState = sharedState;
    }
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null){
            sharedState.buyers.add(msg.getSender());
            System.out.println("acheteur inscrit:"+msg.getSender().getName());
        }else {
            block();
        }
    }
}


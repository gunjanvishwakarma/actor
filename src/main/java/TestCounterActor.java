import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class TestCounterActor {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("sample1");
        final ActorRef counter = actorSystem.actorOf(CounterActor.props(), "counter");

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 5; j++) {
                        counter.tell(new Message(), ActorRef.noSender());
                    }
                }
            }).start();
        }

    }
}

class CounterActor extends AbstractLoggingActor {

    private int counter;

    private void onMessage(Message message) {
        counter++;
        log().info("Increased AlarmActor " + counter);
    }

    public Receive createReceive() {
        return ReceiveBuilder.create().match(Message.class, this::onMessage).build();
    }

    public static Props props() {
        return Props.create(CounterActor.class);
    }
}



class Message {

}

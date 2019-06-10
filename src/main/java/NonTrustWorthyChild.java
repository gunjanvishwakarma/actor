import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;
import static akka.actor.SupervisorStrategy.*;


class NonTrustWorthyChildTest{
    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create();

        final ActorRef supervisor = actorSystem.actorOf(SupervisorActor.props(),"supervisor");
        for(int i =0 ; i < 10 ; i++){
            supervisor.tell(new Command(),ActorRef.noSender());
        }
    }
}

class SupervisorActor extends AbstractLoggingActor {

    public static final OneForOneStrategy ONE_FOR_ONE_STRATEGY_RESTART = new OneForOneStrategy(10, Duration.create("10 seconds"), DeciderBuilder.match(RuntimeException.class, ex -> restart()).build());
    public static final OneForOneStrategy ONE_FOR_ONE_STRATEGY_RESUME = new OneForOneStrategy(10, Duration.create("10 seconds"), DeciderBuilder.match(RuntimeException.class, ex -> resume()).build());
    public static final OneForOneStrategy ONE_FOR_ONE_STRATEGY_STOP = new OneForOneStrategy(10, Duration.create("10 seconds"), DeciderBuilder.match(RuntimeException.class, ex -> stop()).build());
    public static final OneForOneStrategy ONE_FOR_ONE_STRATEGY_ESCALATE = new OneForOneStrategy(10, Duration.create("10 seconds"), DeciderBuilder.match(RuntimeException.class, ex -> escalate()).build());

    @Override
    public Receive createReceive() {
        final ActorRef child = getContext().actorOf(NonTrustWorthyChild.props(), "child");
        return ReceiveBuilder.create().matchAny(any -> child.forward(any, getContext())).build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy(){
        return ONE_FOR_ONE_STRATEGY_ESCALATE;
    }

    public static Props props() {
        return Props.create(SupervisorActor.class);
    }
}

public class NonTrustWorthyChild extends AbstractLoggingActor {
    private long messages = 0L;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(Command.class, this::onCommand).build();
    }

    private void onCommand(Command command) {
        messages++;
        if (messages % 4 == 0) {
            throw new RuntimeException("Oh no, I got four command, I can't handle any more");
        } else {
            log().info("Got a command " + messages);
        }
    }

    public static Props props() {
        return Props.create(NonTrustWorthyChild.class);
    }
}

class Command {

}

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class TestActivityActor {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("sample2");
        final ActorRef alarm = actorSystem.actorOf(AlarmActor.props("right password"), "alarm");
        alarm.tell(new ActivityMsg(), ActorRef.noSender());
        alarm.tell(new EnableMsg("wrong password"), ActorRef.noSender());
        alarm.tell(new EnableMsg("right password"), ActorRef.noSender());
        alarm.tell(new ActivityMsg(), ActorRef.noSender());
        alarm.tell(new DisableMsg("wrong password"), ActorRef.noSender());
        alarm.tell(new DisableMsg("right password"), ActorRef.noSender());
    }
}

class AlarmActor extends AbstractLoggingActor {
    private final String password;

    private final Receive disable;
    private final Receive enable;

    public AlarmActor(String password) {
        this.password = password;
        this.disable = ReceiveBuilder.create()
                .match(EnableMsg.class, this::onEnable)
                .build();
        this.enable = ReceiveBuilder.create()
                .match(ActivityMsg.class, this::onActivity)
                .match(DisableMsg.class, this::onDisable).build();
    }

    @Override
    public Receive createReceive() {
        return enable;
    }

    private void onActivity(ActivityMsg ignored) {
        log().warning("alarm alarm!!!");
    }

    private void onEnable(EnableMsg enable) {
        if (password.equals(enable.password)) {
            log().info("AlarmActor enable");
            getContext().become(this.enable);
        } else {
            log().info("Someone failed to enable the alarm");
        }
        log().info("Increased AlarmActor ");
    }

    private void onDisable(DisableMsg disable) {
        if (password.equals(disable.password)) {
            log().info("AlarmActor disabled");
            getContext().become(this.disable);
        } else {
            log().warning("Someone who didn't know the password tried to disable it");
        }
    }


    public static Props props(String password) {
        return Props.create(AlarmActor.class, password);
    }
}

class ActivityMsg {

}

class DisableMsg {
    public final String password;

    public DisableMsg(String password) {
        this.password = password;
    }
}


class EnableMsg {
    public final String password;

    public EnableMsg(String password) {
        this.password = password;
    }
}


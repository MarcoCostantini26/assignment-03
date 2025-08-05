// filepath: src/main/java/pcd/ass03/actors/BoidActor.java
package pcd.ass03.actors;

import akka.actor.AbstractActor;
import pcd.ass03.model.Boid;
import pcd.ass03.model.BoidModel;
import pcd.ass03.messages.BoidMessages;

public class BoidActor extends AbstractActor {
    private Boid boid;
    private final BoidModel model;

    public BoidActor(Boid boid, BoidModel model) {
        this.boid = boid;
        this.model = model;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(BoidMessages.UpdateBoidState.class, msg -> {
                // Aggiorna lo stato del boid usando i neighbors
                boid.updateState(model);
                // Puoi inviare una risposta al manager se serve
            })
            .match(BoidMessages.GetBoidState.class, msg -> {
                getSender().tell(boid, getSelf());
            })
            .match(BoidMessages.SetWeights.class, msg -> {
                // Aggiorna i pesi se necessario
            })
            .build();
    }
}
// filepath: src/main/java/pcd/ass03/actors/SimulationManagerActor.java
package pcd.ass03.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import java.util.List;
import pcd.ass03.messages.SimulationMessages;

public class SimulationManagerActor extends AbstractActor {
    private List<ActorRef> boidActors;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SimulationMessages.StartSimulation.class, msg -> {
                // Avvia la simulazione: invia messaggi di update ai boid
            })
            .match(SimulationMessages.PauseSimulation.class, msg -> {
                // Gestisci la pausa
            })
            .match(SimulationMessages.ResumeSimulation.class, msg -> {
                // Gestisci la ripresa
            })
            .match(SimulationMessages.ResetSimulation.class, msg -> {
                // Reset della simulazione
            })
            .match(SimulationMessages.BoidStateUpdated.class, msg -> {
                // Ricevi aggiornamenti dai boid
            })
            .build();
    }
}
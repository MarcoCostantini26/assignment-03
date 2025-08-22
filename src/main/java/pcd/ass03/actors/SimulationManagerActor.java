// filepath: src/main/java/pcd/ass03/actors/SimulationManagerActor.java
package pcd.ass03.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import java.util.ArrayList;
import java.util.List;
import pcd.ass03.messages.SimulationMessages;
import pcd.ass03.messages.BoidMessages;
import pcd.ass03.model.Boid;
import pcd.ass03.model.BoidModel;

public class SimulationManagerActor extends AbstractActor {
    private List<ActorRef> boidActors;
    private int responses = 0;
    private BoidModel model;
    private List<Boid> updatedStates = new ArrayList<>();
    private List<Boid> currentStates = new ArrayList<>();
    private boolean running = false;

    public SimulationManagerActor(List<ActorRef> boidActors, List<Boid> initialStates, BoidModel model) {
        this.boidActors = boidActors;
        this.currentStates = initialStates;
        this.model = model;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SimulationMessages.StartSimulation.class, msg -> {
                running = true;
                startStep();
            })
            .match(SimulationMessages.PauseSimulation.class, msg -> {
                running = false;
            })
            .match(SimulationMessages.ResumeSimulation.class, msg -> {
                if (!running) {
                    running = true;
                    startStep();
                }
            })
            .match(SimulationMessages.ResetSimulation.class, msg -> {
                running = false;
                responses = 0;
                updatedStates.clear();
                // Qui puoi aggiungere logica per ricreare i boid se necessario
            })
            .match(SimulationMessages.BoidStateUpdated.class, msg -> {
                responses++;
                updatedStates.add(msg.state);
                if (responses == boidActors.size()) {
                    // Aggiorna la lista degli stati correnti
                    currentStates.clear();
                    currentStates.addAll(updatedStates);

                    // Aggiorna il modello globale
                    for (int i = 0; i < currentStates.size() && i < updatedStates.size(); i++) {
                        // Debug: verifica che gli indici siano validi
                        if (i < model.getBoids().size()) {
                            model.updateBoid(i, currentStates.get(i));
                        }
                    }

                    // Qui puoi aggiornare la GUI se serve

                    responses = 0;
                    updatedStates.clear();
                    if (running) {
                        startStep();
                    }
                }
            })
            .build();
    }

    private static final double PERCEPTION_RADIUS = 50.0; // o il valore che preferisci

    private void startStep() {
        for (int i = 0; i < boidActors.size(); i++) {
            Boid self = currentStates.get(i);
            List<Boid> neighbors = new ArrayList<>();
            for (int j = 0; j < currentStates.size(); j++) {
                if (i != j) {
                    Boid other = currentStates.get(j);
                    double dist = self.getPos().distance(other.getPos());
                    if (dist < PERCEPTION_RADIUS) {
                        neighbors.add(other);
                    }
                }
            }
            boidActors.get(i).tell(new BoidMessages.UpdateBoidState(neighbors), getSelf());
        }
    }
}
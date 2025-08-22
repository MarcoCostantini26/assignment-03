package pcd.ass03.actors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.ActorRef;
import pcd.ass03.model.Boid;
import pcd.ass03.model.BoidModel;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SimulationController {
    private ActorSystem system;
    private ActorRef manager;
    private ActorSimulatorAdapter simulator;
    private final BoidModel model;

    public SimulationController(BoidModel model) {
        this.model = model;
        createActorSystem();
    }

    private void createActorSystem() {
        // Crea nuovo ActorSystem
        system = ActorSystem.create("BoidsSystem");
        
        // Ottieni la lista corrente dei boid dal modello
        List<Boid> boids = model.getBoids();
        
        // Crea un attore per ogni boid
        List<ActorRef> boidActors = new ArrayList<>();
        for (Boid boid : boids) {
            ActorRef boidActor = system.actorOf(Props.create(BoidActor.class, boid, model));
            boidActors.add(boidActor);
        }

        // Crea il SimulationManagerActor
        manager = system.actorOf(Props.create(SimulationManagerActor.class, boidActors, boids, model));
        
        // Crea l'adapter
        simulator = new ActorSimulatorAdapter(manager);
    }

    public ActorSimulatorAdapter getSimulator() {
        return simulator;
    }

    public void recreateWithNewBoids(int nBoids) {
        // 1. Ferma la simulazione corrente
        if (simulator != null) {
            simulator.stop();
        }
        
        // 2. Shutdown dell'ActorSystem corrente
        if (system != null) {
            try {
                system.terminate();
                // Aspetta che l'ActorSystem si chiuda completamente
                system.getWhenTerminated().toCompletableFuture().get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.err.println("Errore durante shutdown ActorSystem: " + e.getMessage());
            }
        }
        
        // 3. Reset del modello con i nuovi boids
        model.resetWithNewBoids(nBoids);
        
        // 4. Ricrea tutto l'ActorSystem
        createActorSystem();
        
        // 5. Avvia la nuova simulazione
        simulator.start();
    }

    public void shutdown() {
        if (simulator != null) {
            simulator.stop();
        }
        if (system != null) {
            system.terminate();
        }
    }
}
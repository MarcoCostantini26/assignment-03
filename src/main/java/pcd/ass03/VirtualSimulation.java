/*
    COMPONENTI DEL GRUPPO:
    Arcese Gabriele
    Colì Diego
    Costantini Marco
    Meco Daniel
 */

package pcd.ass03;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.ActorRef;
import pcd.ass03.actors.ActorSimulatorAdapter;
import pcd.ass03.actors.BoidActor;
import pcd.ass03.actors.SimulationManagerActor;
import pcd.ass03.model.Boid;
import pcd.ass03.model.BoidModel;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.ArrayList;
import pcd.ass03.messages.*;
import pcd.ass03.view.*;

public class VirtualSimulation {
    
    final static double SEPARATION_WEIGHT = 1.0;
    final static double ALIGNMENT_WEIGHT = 1.0;
    final static double COHESION_WEIGHT = 1.0;

    final static int ENVIRONMENT_WIDTH = 1000; 
    final static int ENVIRONMENT_HEIGHT = 1000;
    static final double MAX_SPEED = 4.0;
    static final double PERCEPTION_RADIUS = 50.0;
    static final double AVOID_RADIUS = 20.0;

    final static int SCREEN_WIDTH = 1000; 
    final static int SCREEN_HEIGHT = 800; 

    public static void main(String[] args) {      
        // Chiede all'utente di inserire il numero di boids
        String input = JOptionPane.showInputDialog("Inserisci il numero di boids:");
        int nBoids = 1500; // valore default
        try {
            nBoids = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            System.out.println("Numero non valido, utilizzo valore di default 1500.");
        }
        
        var model = new BoidModel(
                        nBoids, 
                        SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT, 
                        ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
                        MAX_SPEED,
                        PERCEPTION_RADIUS,
                        AVOID_RADIUS); 
        
        // 1. Ottieni la lista iniziale dei boid dal modello
        List<Boid> initialBoids = model.getBoids();

        // 2. Crea l'ActorSystem
        ActorSystem system = ActorSystem.create("BoidsSystem");

        // 3. Crea un attore per ogni boid
       List<ActorRef> boidActors = new ArrayList<>();
        for (Boid boid : initialBoids) {
            ActorRef boidActor = system.actorOf(Props.create(BoidActor.class, boid, model));
            boidActors.add(boidActor);
        }

        // 4. Crea il SimulationManagerActor, passando la lista di ActorRef e la lista degli stati iniziali
        ActorRef manager = system.actorOf(Props.create(SimulationManagerActor.class, boidActors, initialBoids, model));

        // 5. (Opzionale) Avvia la simulazione inviando StartSimulation
        manager.tell(new SimulationMessages.StartSimulation(), ActorRef.noSender());

        ActorSimulatorAdapter simulator = new ActorSimulatorAdapter(manager);
        // 6. (Opzionale) Collega la GUI al manager/actor system
        BoidsView view = new BoidsView(model, simulator, SCREEN_WIDTH, SCREEN_HEIGHT);
    }
}

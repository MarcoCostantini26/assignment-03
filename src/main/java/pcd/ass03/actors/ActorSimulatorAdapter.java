package pcd.ass03.actors;

import akka.actor.ActorRef;
import pcd.ass03.messages.SimulationMessages;

public class ActorSimulatorAdapter implements Simulator {
    private final ActorRef manager;
    private boolean paused = false;

    public ActorSimulatorAdapter(ActorRef manager) {
        this.manager = manager;
    }

    @Override
    public void runSimulation() {
        manager.tell(new SimulationMessages.StartSimulation(), ActorRef.noSender());
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void togglePause() {
        if (paused) {
            manager.tell(new SimulationMessages.ResumeSimulation(), ActorRef.noSender());
        } else {
            manager.tell(new SimulationMessages.PauseSimulation(), ActorRef.noSender());
        }
        paused = !paused;
    }

    @Override
    public void attachView(Object view) {
        // Se vuoi, puoi gestire la view qui
    }

    @Override
    public void stop() {
        // Puoi implementare uno stop vero se hai un messaggio dedicato
        manager.tell(new SimulationMessages.PauseSimulation(), ActorRef.noSender());
        paused = true;
    }

    @Override
    public void start() {
        manager.tell(new SimulationMessages.StartSimulation(), ActorRef.noSender());
        paused = false;
    }
}
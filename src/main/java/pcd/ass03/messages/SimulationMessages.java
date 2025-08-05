package pcd.ass03.messages;

import pcd.ass03.model.Boid;

public class SimulationMessages {
    public static class StartSimulation { }
    public static class PauseSimulation { }
    public static class ResumeSimulation { }
    public static class ResetSimulation {
        public final int nBoids;
        public ResetSimulation(int nBoids) {
            this.nBoids = nBoids;
        }
    }
    public static class BoidStateUpdated {
        public final int boidId;
        public final Boid state;
        public BoidStateUpdated(int boidId, Boid state) {
            this.boidId = boidId;
            this.state = state;
        }
    }
}
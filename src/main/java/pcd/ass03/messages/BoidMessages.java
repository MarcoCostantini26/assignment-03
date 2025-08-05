package pcd.ass03.messages;

import java.util.List;
import pcd.ass03.model.Boid;

public class BoidMessages {
    public static class UpdateBoidState {
        public final List<Boid> neighbors;
        public UpdateBoidState(List<Boid> neighbors) {
            this.neighbors = neighbors;
        }
    }

    public static class GetBoidState { }

    public static class SetWeights {
        public final double separation, alignment, cohesion;
        public SetWeights(double separation, double alignment, double cohesion) {
            this.separation = separation;
            this.alignment = alignment;
            this.cohesion = cohesion;
        }
    }
}
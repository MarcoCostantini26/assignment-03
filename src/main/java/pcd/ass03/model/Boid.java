package pcd.ass03.model;

import pcd.ass03.common.*;
import java.util.List;
import java.util.ArrayList;

public class Boid {
    // Stato semplice - nel modello ad attori non servono AtomicReference o lock
    private P2d pos;
    private V2d vel;

    public Boid(P2d pos, V2d vel) {
        this.pos = pos;
        this.vel = vel;
    }

    public P2d getPos() {
        // Copia difensiva per evitare modifiche esterne
        return new P2d(pos.x(), pos.y());
    }

    public V2d getVel() {
        // Copia difensiva per evitare modifiche esterne
        return new V2d(vel.x(), vel.y());
    }

    public void setPos(P2d pos) {
        this.pos = pos;
    }

    public void setVel(V2d vel) {
        this.vel = vel;
    }

    public void updateStateWithNeighbors(List<Boid> neighbors, BoidModel model) {
        double separationWeight = model.getSeparationWeight();
        double alignmentWeight = model.getAlignmentWeight();
        double cohesionWeight = model.getCohesionWeight();
        double maxSpeed = model.getMaxSpeed();

        P2d currentPos = getPos();
        V2d currentVel = getVel();

        V2d separation = calculateSeparation(neighbors, model, currentPos);
        V2d alignment = calculateAlignment(neighbors, currentVel);
        V2d cohesion = calculateCohesion(neighbors, currentPos);

        // Aggiornamento diretto - nessun lock necessario nel modello ad attori
        V2d newVel = currentVel
            .sum(alignment.mul(alignmentWeight))
            .sum(separation.mul(separationWeight))
            .sum(cohesion.mul(cohesionWeight));
            
        double speed = newVel.abs();
        if (speed > maxSpeed) {
            newVel = newVel.getNormalized().mul(maxSpeed);
        }
        
        this.vel = newVel;
        P2d newPos = currentPos.sum(newVel);
        this.pos = newPos;
    }

    public void updateState(BoidModel model) {
        P2d currentPos = getPos();
        V2d currentVel = getVel();
        
        List<Boid> nearbyBoids = getNearbyBoids(model, currentPos);
        V2d separation = calculateSeparation(nearbyBoids, model, currentPos);
        V2d alignment = calculateAlignment(nearbyBoids, currentVel);
        V2d cohesion = calculateCohesion(nearbyBoids, currentPos);
        
        // Aggiornamento diretto - nessun lock necessario nel modello ad attori
        V2d newVel = currentVel.sum(alignment.mul(model.getAlignmentWeight()))
                .sum(separation.mul(model.getSeparationWeight()))
                .sum(cohesion.mul(model.getCohesionWeight()));
                
        double speed = newVel.abs();
        if (speed > model.getMaxSpeed()) {
            newVel = newVel.getNormalized().mul(model.getMaxSpeed());
        }
        
        this.vel = newVel;
        
        // Aggiornamento della posizione
        P2d newPos = currentPos.sum(newVel);
        
        // Controllo dei bordi
        if (newPos.x() < model.getMinX()) newPos = newPos.sum(new V2d(model.getWidth(), 0));
        if (newPos.x() >= model.getMaxX()) newPos = newPos.sum(new V2d(-model.getWidth(), 0));
        if (newPos.y() < model.getMinY()) newPos = newPos.sum(new V2d(0, model.getHeight()));
        if (newPos.y() >= model.getMaxY()) newPos = newPos.sum(new V2d(0, -model.getHeight()));
        
        this.pos = newPos;
    }

    private List<Boid> getNearbyBoids(BoidModel model, P2d myPos) {
        List<Boid> list = new ArrayList<>();
        for (Boid other : model.getBoids()) {
            if (other != this) {
                P2d otherPos = other.getPos();
                double distance = myPos.distance(otherPos);
                if (distance < model.getPerceptionRadius()) {
                    list.add(other);
                }
            }
        }
        return list;
    }

    private V2d calculateAlignment(List<Boid> nearbyBoids, V2d currentVel) {
        double avgVx = 0, avgVy = 0;
        if (!nearbyBoids.isEmpty()) {
            for (Boid other : nearbyBoids) {
                V2d otherVel = other.getVel();
                avgVx += otherVel.x();
                avgVy += otherVel.y();
            }
            avgVx /= nearbyBoids.size();
            avgVy /= nearbyBoids.size();
            return new V2d(avgVx - currentVel.x(), avgVy - currentVel.y()).getNormalized();
        }
        return new V2d(0, 0);
    }

    private V2d calculateCohesion(List<Boid> nearbyBoids, P2d currentPos) {
        double centerX = 0, centerY = 0;
        if (!nearbyBoids.isEmpty()) {
            for (Boid other : nearbyBoids) {
                P2d otherPos = other.getPos();
                centerX += otherPos.x();
                centerY += otherPos.y();
            }
            centerX /= nearbyBoids.size();
            centerY /= nearbyBoids.size();
            return new V2d(centerX - currentPos.x(), centerY - currentPos.y()).getNormalized();
        }
        return new V2d(0, 0);
    }

    private V2d calculateSeparation(List<Boid> nearbyBoids, BoidModel model, P2d currentPos) {
        double dx = 0, dy = 0;
        int count = 0;
        for (Boid other : nearbyBoids) {
            P2d otherPos = other.getPos();
            double distance = currentPos.distance(otherPos);
            if (distance < model.getAvoidRadius()) {
                dx += currentPos.x() - otherPos.x();
                dy += currentPos.y() - otherPos.y();
                count++;
            }
        }
        if (count > 0) {
            dx /= count;
            dy /= count;
            return new V2d(dx, dy).getNormalized();
        }
        return new V2d(0, 0);
    }
}

package pcd.ass03.concurrent;

public interface Simulator {
    void runSimulation();
    boolean isPaused();
    void togglePause();
    void attachView(Object view);
    void stop();
    void start();
}

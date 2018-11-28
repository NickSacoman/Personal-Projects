package problem;

import simulator.State;

public class StateWrapper {
    private State state;

    public StateWrapper(State s){
        this.state = s;
    }

    public String generateStateString(){
        StringBuilder sb = new StringBuilder();
        sb.append("State: [ ");
        sb.append("Pos=").append(state.getPos()).append(" | ");
        sb.append("Car=").append(state.getCarType()).append(" | ");
        sb.append("Driver=").append(state.getDriver()).append(" | ");
        sb.append("Tire=").append(state.getTireModel().toString()).append(" | ");
        sb.append("Pressure=").append(state.getTirePressure().asString()).append(" | ");
        return sb.toString();
    }

    public State getState(){
        return this.state;
    }
}

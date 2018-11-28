package problem;

import simulator.Simulator;
import simulator.State;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        ProblemSpec ps;
        try {
            ps = new ProblemSpec(args[0]); 
            System.out.println(ps.toString());
            Simulator sim = new Simulator(ps, args[1]); 
            MDP markov = new MDP(ps);
            MDPSolver markovSolver = new MDPSolver(markov, ps);
            markovSolver.ValueIteration();
            State s = State.getStartState(ps.getFirstCarType(),ps.getFirstDriver(),ps.getFirstTireModel());
            while(sim.getSteps() < ps.getMaxT()){
                s = sim.step(markovSolver.getAction(s));
                if(sim.isGoalState(s)){
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception occurred");
            System.exit(1);
        }
        System.out.println("Finished loading!");

    }
}
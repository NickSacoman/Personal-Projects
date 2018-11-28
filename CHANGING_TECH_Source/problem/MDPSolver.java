package problem;

import java.security.Policy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import simulator.Simulator;
import simulator.State;

/**
 * Solver for an MDP problem as defined by the generic MDP class. The aim of this class is to
 * solve the MDP problem as it is defined.
 * 
 * @author Nick Sacoman
 * @version 31/10/2018
 */
public class MDPSolver {

	// Define useful fields for the MDP Problem
	MDP mdpProblem;
	ProblemSpec specs;
	private Map<String, Action> policy;
	private Map<String, Double> V;
	private Map<State, Boolean> solvedSet;
	State s;
	Action a;
	private Long time;
	private int solvedCount =0;

	/**
	 * Construct a new MDP solver from the given specs and MDP defined problem
	 * 
	 * @param mdpDef, MDP problem
	 * @param ps, problem specs
	 */
	public MDPSolver(MDP mdpDef, ProblemSpec ps) {
		// Define mdp and get specs
		mdpProblem = mdpDef;
		specs = ps;

		// Initialize S, A, and policy map
		s = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());
		a = null;
		policy = new HashMap<String, Action>();
		V = new HashMap<String, Double>();
		solvedSet = new HashMap<State, Boolean>();
		time = System.currentTimeMillis();
		solvedCount = 0;
	}

	/**
	 * Use value iteration through all states to determine the optimal action for each of the 
	 * states. Label values as solved as needed
	 */
	public void ValueIteration() {
		// Get states from the mdp def, initialize their reward and solved value
		List<State> states = mdpProblem.getStates();
		for (int i = 0; i < states.size(); i++) {
			solvedSet.put(states.get(i), false);
			V.put(new StateWrapper(states.get(i)).generateStateString(), mdpProblem.getReward(states.get(i)));
			policy.put(new StateWrapper(states.get(i)).generateStateString(), mdpProblem.getActions().get(0));
		}

		// Loop through all states, updating each evenly often, checking if they are solved
		int i = 0;
		boolean loopNotDone = true;
		Long startTime = System.currentTimeMillis();
		Long loopTime = 0l;
		while (System.currentTimeMillis() - time - loopTime < 95000 || solvedCount == states.size()) //TODO Change to loop for 2 mins
		{
			i=0;
			while (i < states.size()) {
				if (!solvedSet.get(states.get(i))){
					double prevOptionV = V.get(new StateWrapper(states.get(i)).generateStateString());
					ActionVPair bestOption = maxAction(states.get(i));
					double bestOptionV = bestOption.getVS();
					Action bestOptionAction = bestOption.getBestAction();
					V.replace(new StateWrapper(states.get(i)).generateStateString(), bestOptionV);
					policy.replace(new StateWrapper(states.get(i)).generateStateString(), bestOptionAction);
					if (bestOptionV - prevOptionV == 1e-5) {
						solvedSet.replace(states.get(i), true);
						solvedCount++;
					}
				}
				i++;
			}
			if(loopNotDone){
				loopTime = startTime - System.currentTimeMillis();
				loopNotDone = false;
			}
		}
	}

	/**
	 * Attempts to calculate and choose the action in a given state that will offer the best
	 * reward
	 * 
	 * @param state
	 * @return
	 */
	private ActionVPair maxAction(State state) {
		double VS = 0;
		Action bestAction = null;
		List<Action> possibleActions = mdpProblem.getActions();
		for (int i = 0; i < possibleActions.size(); i++) {
			// Action 1
			if (possibleActions.get(i).getActionType().getActionNo()==1) {
				double curUtil = state.getPos() + (sumTransition(state));
				if (curUtil > VS) {
					VS = curUtil;
					bestAction = possibleActions.get(i);
				}
			}
			// Action 2 - change car
			else if (possibleActions.get(i).getActionType().getActionNo()==2) {
				State possibleState = state.changeCarType(possibleActions.get(i).getCarType());
				double curUtil = possibleState.getPos() + (sumTransition(possibleState));
				if (curUtil > VS) {
					VS = curUtil;
					bestAction = possibleActions.get(i);
				}
			}
			// Action 3 - change driver
			else if (possibleActions.get(i).getActionType().getActionNo()==3) {
				State possibleState = state.changeDriver(possibleActions.get(i).getDriverType());
				double curUtil = possibleState.getPos() + (sumTransition(possibleState));
				if (curUtil > VS) {
					VS = curUtil;
					bestAction = possibleActions.get(i);
				}
			}
			// Action 4 - change tires
			else if (possibleActions.get(i).getActionType().getActionNo()==4) {
				State possibleState = state.changeTires(possibleActions.get(i).getTireModel());
				double curUtil = possibleState.getPos() + (sumTransition(possibleState));
				if (curUtil > VS) {
					VS = curUtil;
					bestAction = possibleActions.get(i);
				}
			}
			// Action 5 - add fuel
			else if (possibleActions.get(i).getActionType().getActionNo()==5) {
				State possibleState = state.addFuel(possibleActions.get(i).getFuel());
				double curUtil = possibleState.getPos() + (sumTransition(possibleState));
				if (curUtil > VS) {
					VS = curUtil;
					bestAction = possibleActions.get(i);
				}
			}
			// Action 6 - change tire pressure
			else if (possibleActions.get(i).getActionType().getActionNo()==6) {
				State possibleState = state.changeTirePressure(possibleActions.get(i).getTirePressure());
				double curUtil = possibleState.getPos() + (sumTransition(possibleState));
				if (curUtil > VS) {
					VS = curUtil;
					bestAction = possibleActions.get(i);
				}
			}
			// Action 7 - change driver and car
			else if (possibleActions.get(i).getActionType().getActionNo()==7) {
				State possibleState = state.changeCarAndDriver(possibleActions.get(i).getCarType(), possibleActions.get(i).getDriverType());
				double curUtil = possibleState.getPos() + (sumTransition(possibleState));
				if (curUtil > VS) {
					VS = curUtil;
					bestAction = possibleActions.get(i);
				}
			}
			// Action 8 - change tire pressure, tires, and fuel (or any combination)
			else if (possibleActions.get(i).getActionType().getActionNo()==8) {
				State possibleState = state.changeTireFuelAndTirePressure(possibleActions.get(i).getTireModel(), 
						possibleActions.get(i).getFuel(), possibleActions.get(i).getTirePressure());
				double curUtil = possibleState.getPos() + (sumTransition(possibleState));
				if (curUtil > VS) {
					VS = curUtil;
					bestAction = possibleActions.get(i);
				}
			}
		}

		// Return the Action Utility pair
		return new ActionVPair(VS, bestAction);
	}

	public Action test(State s){
		Action a = maxAction(s).a;
		return a;
	}
	/**
	 *  Used to calculate the sum of moving transition in a given state
	 *  
	 * @param state, current state
	 * @return transition utility
	 */
	private double sumTransition(State state) {
		double[] probList = getMoveProbs(state);
		double totalUtil = 0;

		// Iterate through the probabilities and calculate util
		int distance = -4;
		for (int i = 0; i < probList.length; i++) {
			if (i == probList.length-2) {
				totalUtil = totalUtil + (probList[i] * (-1*specs.getSlipRecoveryTime()));
			}
			else if (i == probList.length-1) {
				totalUtil = totalUtil + (probList[i] * (-1*specs.getRepairTime()));
			}
			else {
				totalUtil = totalUtil + (probList[i] * Math.min(Math.max(1d, s.getPos() + distance), specs.getN()));
				//totalUtil = totalUtil + (probList[i] * V.get(new StateWrapper(state.changePosition(distance, specs.getN())).generateStateString()));
			}
			distance++;
		}
		return totalUtil * specs.getDiscountFactor();
	}

	/**
	 * Calculate the conditional move probabilities for the current state.
	 *
	 *          P(K | C, D, Ti, Te, Pressure)
	 *
	 * @return list of move probabilities
	 */
	private double[] getMoveProbs(State s) {
		// get parameters of current state
		Terrain terrain = specs.getEnvironmentMap()[s.getPos() - 1];
		int terrainIndex = specs.getTerrainIndex(terrain);
		String car = s.getCarType();
		String driver = s.getDriver();
		Tire tire = s.getTireModel();
		// calculate priors
		double priorK = 1.0 / ProblemSpec.CAR_MOVE_RANGE;
		double priorCar = 1.0 / specs.getCT();
		double priorDriver = 1.0 / specs.getDT();
		double priorTire = 1.0 / ProblemSpec.NUM_TYRE_MODELS;
		double priorTerrain = 1.0 / specs.getNT();
		double priorPressure = 1.0 / ProblemSpec.TIRE_PRESSURE_LEVELS;
		// get probabilities of k given parameter
		double[] pKGivenCar = specs.getCarMoveProbability().get(car);
		double[] pKGivenDriver = specs.getDriverMoveProbability().get(driver);
		double[] pKGivenTire = specs.getTireModelMoveProbability().get(tire);
		double pSlipGivenTerrain = specs.getSlipProbability()[terrainIndex];
		double[] pKGivenPressureTerrain = convertSlipProbs(s, pSlipGivenTerrain);
		// use bayes rule to get probability of parameter given k
		double[] pCarGivenK = bayesRule(pKGivenCar, priorCar, priorK);
		double[] pDriverGivenK = bayesRule(pKGivenDriver, priorDriver, priorK);
		double[] pTireGivenK = bayesRule(pKGivenTire, priorTire, priorK);
		double[] pPressureTerrainGivenK = bayesRule(pKGivenPressureTerrain,
				(priorTerrain * priorPressure), priorK);
		// use conditional probability formula on assignment sheet to get what
		// we want (but what is it that we want....)
		double[] kProbs = new double[ProblemSpec.CAR_MOVE_RANGE];
		double kProbsSum = 0;
		double kProb;
		for (int k = 0; k < ProblemSpec.CAR_MOVE_RANGE; k++) {
			kProb = magicFormula(pCarGivenK[k], pDriverGivenK[k],
					pTireGivenK[k], pPressureTerrainGivenK[k], priorK);
			kProbsSum += kProb;
			kProbs[k] = kProb;
		}
		// Normalize
		for (int k = 0; k < ProblemSpec.CAR_MOVE_RANGE; k++) {
			kProbs[k] /= kProbsSum;
		}
		return kProbs;
	}
	/**
	 * Convert the probability of slipping on a given terrain with 50% tire
	 * pressure into a probability list, of move distance versus current
	 * terrain and tire pressure.
	 *
	 * @param slipProb probability of slipping on current terrain and 50%
	 *                 tire pressure
	 * @return list of move probabilities given current terrain and pressure
	 */
	private double[] convertSlipProbs(State s, double slipProb) {
		// Adjust slip probability based on tire pressure
		TirePressure pressure = s.getTirePressure();
		if (pressure == TirePressure.SEVENTY_FIVE_PERCENT) {
			slipProb *= 2;
		} else if (pressure == TirePressure.ONE_HUNDRED_PERCENT) {
			slipProb *= 3;
		}
		// Make sure new probability is not above max
		if (slipProb > ProblemSpec.MAX_SLIP_PROBABILITY) {
			slipProb = ProblemSpec.MAX_SLIP_PROBABILITY;
		}
		// for each terrain, all other action probabilities are uniform over
		// remaining probability
		double[] kProbs = new double[ProblemSpec.CAR_MOVE_RANGE];
		double leftOver = 1 - slipProb;
		double otherProb = leftOver / (ProblemSpec.CAR_MOVE_RANGE - 1);
		for (int i = 0; i < ProblemSpec.CAR_MOVE_RANGE; i++) {
			if (i == specs.getIndexOfMove(ProblemSpec.SLIP)) {
				kProbs[i] = slipProb;
			} else {
				kProbs[i] = otherProb;
			}
		}
		return kProbs;
	}

	/**
	 * Apply bayes rule to all values in cond probs list.
	 *
	 * @param condProb list of P(B|A)
	 * @param priorA prior probability of parameter A
	 * @param priorB prior probability of parameter B
	 * @return list of P(A|B)
	 */
	private double[] bayesRule(double[] condProb, double priorA, double priorB) {
		double[] swappedProb = new double[condProb.length];
		for (int i = 0; i < condProb.length; i++) {
			swappedProb[i] = (condProb[i] * priorA) / priorB;
		}
		return swappedProb;
	}

	public Action getAction(State s){
		Action a = policy.get(new StateWrapper(s).generateStateString());
		if(a.getActionType() != ActionType.CHANGE_CAR) {
			if (s.getFuel() < getFuelConsumption(s)) {
				for (int i = 0; i < specs.getCarOrder().size(); i++) {
					if (specs.getCarOrder().get(i) != s.getCarType()) {
						return new Action(ActionType.CHANGE_CAR, specs.getCarOrder().get(i));
					}
				}

			}
		}
		return a;
	}

	/**
	 * Get the fuel consumption of moving given the current state
	 *
	 * @return move fuel consumption for current state
	 */
	private int getFuelConsumption(State s) {

		// get parameters of current state
		Terrain terrain = specs.getEnvironmentMap()[s.getPos() - 1];
		String car = s.getCarType();
		TirePressure pressure = s.getTirePressure();

		// get fuel consumption
		int terrainIndex = specs.getTerrainIndex(terrain);
		int carIndex = specs.getCarIndex(car);
		int fuelConsumption = specs.getFuelUsage()[terrainIndex][carIndex];

		if (pressure == TirePressure.FIFTY_PERCENT) {
			fuelConsumption *= 3;
		} else if (pressure == TirePressure.SEVENTY_FIVE_PERCENT) {
			fuelConsumption *= 2;
		}
		return fuelConsumption;
	}

	/**
	 * Conditional probability formula from assignment 2 sheet
	 *
	 * @param pA P(A | E)
	 * @param pB P(B | E)
	 * @param pC P(C | E)
	 * @param pD P(D | E)
	 * @param priorE P(E)
	 * @return numerator of the P(E | A, B, C, D) formula (still need to divide
	 *      by sum over E)
	 */
	private double magicFormula(double pA, double pB, double pC, double pD,
			double priorE) {
		return pA * pB * pC * pD * priorE;
	}

	/**
	 * Class used to pair an action with its V(s) value during iteration
	 * @author Nick Sacoman
	 * @version 1/11/2018
	 */
	private class ActionVPair {
		double vs;
		Action a;

		/**
		 * Construct new reward and action pair
		 * @param vs, reward
		 * @param a, action maximizing reward
		 */
		public ActionVPair(double vs, Action a) {
			this.vs = vs;
			this.a = a;
		}

		/**
		 * Get the reward
		 * @return reward
		 */
		public double getVS() {
			return this.vs;
		}

		/**
		 * Get the best action
		 * @return action
		 */
		public Action getBestAction() {
			return this.a;
		}
	}
}

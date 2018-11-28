package problem;

import simulator.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * General MDP agent, adaptable to become online or offline (hopefully).
 * 
 * @author Nick Sacoman
 * @version 31/10/2018
 */
public class MDP {

	// Lists for states/actions and mapping state to reward
	private List<State> stateList;
	private List<Action> actionList;
	private List<ActionType> actionTypes;

	/**
	 * Construct an MDP agent out of the given problem spec
	 * 
	 * @param ps, spec for given problem
	 */
	public MDP(ProblemSpec ps) {
		// Initialize states and actions
		stateList = new ArrayList<State>();
		actionList = new ArrayList<Action>();
		actionTypes = new ArrayList<ActionType>();

		// Get the level from problem spec
		Level lvl = ps.getLevel();

		// Formulate all states and actions given spec
		formListsFromSpec(ps, lvl);
	}

	/**
	 * Given the problem spec, formulate all possible states and actions that will solve the problem
	 * 
	 * @param ps, problem spec
	 * @param lvl, level given in problem spec
	 */
	public void formListsFromSpec(ProblemSpec ps, Level lvl) {
		// Get all possible state combinations
		for (int gridPos = 1; gridPos <= ps.getN(); gridPos++) {
			// Iterate cars
			for (int i = 0; i < ps.getCarOrder().size(); i++) {
				// Iterate drivers
				for (int j = 0; j < ps.getDriverOrder().size(); j++) {
					// Iterate tires
					for (int z = 0; z < ps.getTireOrder().size(); z++) {
						stateList.add(new State(gridPos, false, false, ps.getCarOrder().get(i),
							ProblemSpec.FUEL_MAX, TirePressure.FIFTY_PERCENT,
							ps.getDriverOrder().get(j), ps.getTireOrder().get(z)));
						stateList.add(new State(gridPos, false, false, ps.getCarOrder().get(i),
								ProblemSpec.FUEL_MAX, TirePressure.SEVENTY_FIVE_PERCENT,
								ps.getDriverOrder().get(j), ps.getTireOrder().get(z)));
						stateList.add(new State(gridPos, false, false, ps.getCarOrder().get(i),
								ProblemSpec.FUEL_MAX, TirePressure.ONE_HUNDRED_PERCENT,
								ps.getDriverOrder().get(j), ps.getTireOrder().get(z)));
					}
				}
			}
		}

		// Get all possible action combinations given via input
		actionTypes = lvl.getAvailableActions();
		for (int i = 0; i < actionTypes.size(); i++) {
			if (actionTypes.get(i).getActionNo() == 1) {
				// Movement
				actionList.add(new Action(actionTypes.get(i)));
			}
			else if (actionTypes.get(i).getActionNo() == 2) {
				// All possible car changes
				for (int j = 0; j < ps.getCarOrder().size(); j++) {
					actionList.add(new Action(actionTypes.get(i), ps.getCarOrder().get(j)));
				}
			}
			else if (actionTypes.get(i).getActionNo() == 3) {
				// All possible driver changes
				for (int z = 0; z < ps.getDriverOrder().size(); z++) {
					actionList.add(new Action(actionTypes.get(i), ps.getDriverOrder().get(z)));
				}
			}
			else if (actionTypes.get(i).getActionNo() == 4) {
				// All possible tire changes
				for (int j = 0; j < ps.getTireOrder().size(); j++) {
					actionList.add(new Action(actionTypes.get(i), ps.getTireOrder().get(j)));
				}
			}
			/**
			else if (actionTypes.get(i).getActionNo() == 5) {
				// Add fuel *DISCRETIZED BY INCREMENTS OF 5*
				int fuelAdd = 5; 
				while (fuelAdd <= 50) {
					actionList.add(new Action(actionTypes.get(i), fuelAdd));
					fuelAdd = fuelAdd + 5;
				}
			}
			 **/
			else if (actionTypes.get(i).getActionNo() == 6) {
				// Three choices of pressure
				actionList.add(new Action(actionTypes.get(i), TirePressure.FIFTY_PERCENT));
				actionList.add(new Action(actionTypes.get(i), TirePressure.SEVENTY_FIVE_PERCENT));
				actionList.add(new Action(actionTypes.get(i), TirePressure.ONE_HUNDRED_PERCENT));
			}
			else if (actionTypes.get(i).getActionNo() == 7) {
				// All options of driver and car changes
				for (int z = 0; z < ps.getCarOrder().size(); z++) {
					for (int j = 0; j < ps.getDriverOrder().size(); j++) {
						actionList.add(new Action(actionTypes.get(i), ps.getCarOrder().get(z), ps.getDriverOrder().get(j)));
					}
				}
			}
			else if (actionTypes.get(i).getActionNo() == 8) {
				// All options of tire, pressure, and fuel
				for (int z = 0; z < ps.getTireOrder().size(); z++) {
					for (int j = 0; j <= 50; j = j + 5) {
						actionList.add(new Action((actionTypes.get(i)), ps.getTireOrder().get(z), j, TirePressure.FIFTY_PERCENT));
						actionList.add(new Action((actionTypes.get(i)), ps.getTireOrder().get(z), j, TirePressure.SEVENTY_FIVE_PERCENT));
						actionList.add(new Action((actionTypes.get(i)), ps.getTireOrder().get(z), j, TirePressure.ONE_HUNDRED_PERCENT));
					}
				}
			}
		}
	}

	/**
	 * Calculate the immediate reward given a certain state (position on the grid)
	 * 
	 * @param s, given 
	 */
	public void calculateImmediateReward(State s) {
		s.getPos();
	}
	/**
	 * Get the states of the MDP
	 * 
	 * @return list of states
	 */
	public List<State> getStates() {
		return stateList;
	}

	/**
	 * Get the actions of the MDP
	 * 
	 * @return list of actions
	 */
	public List<Action> getActions() {
		return actionList;
	}

	/**
	 * Get the reward for a given state
	 * 
	 * @param s, given state
	 * @return the reward
	 */
	public double getReward(State s) {
		return s.getPos();
	}

}

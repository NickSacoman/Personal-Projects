package problem;

import simulator.Simulator;
import simulator.State;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class EndlessInputTester {
    private static int num_file = 50;
    private static int levelForTest = 1;
    private static long timeLimit = 15000;
    private static List<String> broken = new ArrayList<>();
    private static String outputFile = "auto_generated_examples/level_" + levelForTest + "/output_lvl" + levelForTest + ".txt";
    public static void main(String[] args) throws IOException {
        int[] level =  new int[] {levelForTest};
        for (int lv:level) {
            InputStructure(lv);}
        int num=0;
        int passed=0;
        long startTime = System.currentTimeMillis();
        while (num<num_file)
        {
            String newInputFile = "auto_generated_examples/level_" + levelForTest + "/input_lvl" + levelForTest + "_" + num + ".txt";
            num++;
            //Your test method here!!!
            System.out.println("Endless input files testing start: ");
            ProblemSpec ps = new ProblemSpec(newInputFile);
            Simulator sim = new Simulator(ps, outputFile);
            MDP markov = new MDP(ps);
            markov.formListsFromSpec(ps, ps.getLevel());
            MDPSolver markovSolver = new MDPSolver(markov, ps);
            markovSolver.ValueIteration();
            boolean worked = false;
            State s = State.getStartState(ps.getFirstCarType(),ps.getFirstDriver(),ps.getFirstTireModel());
            for(int i =0; i<3; i++){
                while(sim.getSteps() < ps.getMaxT()){
                    s = sim.step(markovSolver.getAction(s));
                    if(sim.isGoalState(s)){
                        passed++;
                        worked = true;
                        break;
                    }
                }
                if(worked){
                    break;
                }
                if(i==2){
                    broken.add(num + "");
                }
                sim.reset();
            }

            System.out.println("Next Test File :   "   +newInputFile);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Some statistic...
        long aveTime = (System.currentTimeMillis()-startTime)/1000/num;
        double passRate = (passed/(num*1.0))*100;
        System.out.println("===============testing END==============");
        System.out.print("Total times tested:  "+num);
        System.out.println("Total times passed:  "+passed);
        System.out.println("Average solving time for each test:  "+aveTime);
        System.out.println("Passing Rate =====>:  "+passRate);
        for(int i=0; i<broken.size(); i++){
            System.out.print(broken.get(i )+ " ,");
        }
    }

    private static void InputStructure(int lv) throws IOException {
        String inputFile= "examples/level_" + lv + "/input_lvl" + lv + ".txt";
        ProblemSpec ps = new ProblemSpec(inputFile);
        getInputFormat(ps);
        //number of input files generate

        for (int num = 0; num< num_file; num++) {
            String newInputFile =  "auto_generated_examples/level_" + lv + "/input_lvl" + lv + "_"+num+".txt";
            BufferedWriter input = new BufferedWriter(new FileWriter(newInputFile));
            input.write(getInputFormat(ps));
            input.close();
        }

    }

    private static String getInputFormat(ProblemSpec ps) {
        StringBuilder sb = new StringBuilder();
        sb.append(ps.getLevel().getLevelNumber()+"\n");
        sb.append(ps.getDiscountFactor()).append(" ");
        sb.append(ps.getSlipRecoveryTime()).append(" ");
        sb.append(ps.getRepairTime());
        sb.append("\n");
        sb.append(ps.getN()+" "+ps.getMaxT());
        sb.append("\n");
        Map<Terrain,List<Integer>> terrainListMap =buildTerrainList(ps);
        for (Terrain ter :terrainListMap.keySet()){
            sb.append(ter.asString()+":");
            for (Integer value : terrainListMap.get(ter)){
                sb.append(value+",");
            }
            sb.setLength(sb.length()-1);
            sb.append("\n");
        }
        sb.append(ps.getCarOrder().size());
        sb.append("\n");
        for (String car:ps.getCarOrder()){
            sb.append(car+":"+random12String());
            sb.append("\n"); }
        sb.append(ps.getDriverOrder().size());
        sb.append("\n");
        for (String driver:ps.getDriverOrder()){
            sb.append(driver+":"+random12String());
            sb.append("\n");}
        for (Tire tire:ps.getTireOrder()){
            sb.append(tire.asString().toLowerCase()+":"+random12String());
            sb.append("\n");}
        for (int i=0;i<ps.getTerrainMap().keySet().size();i++)
            for (int j=0;j<ps.getCarOrder().size();j++)
                sb.append(ps.getFuelUsage()[i][j]+" ");
        sb.append("\n");
        for (double slipP:ps.getSlipProbability())
            sb.append(slipP+" ");

        return sb.toString();
    }

    private static String random12String(){
        StringBuilder sb = new StringBuilder();
        double[] random12 = random12Generator();
        for (double next :random12){
            sb.append(next+" ");
        }
        return sb.toString();
    }

    private static  double[] random12Generator(){
        double[] random12 = new double[12];
        List<Integer> temRandom12 = n_random(100,12);
        for (int i =0; i<12;i++) {
            random12[i] = temRandom12.get(i)*1.0/100;
        }
        return random12;
    }

    public static List<Integer> n_random(int targetSum, int numberOfDraws) {
        Random r = new Random();
        List<Integer> load = new ArrayList<>();

        //random numbers
        int sum = 0;
        for (int i = 0; i < numberOfDraws; i++) {
            int next = r.nextInt(targetSum) + 1;
            load.add(next);
            sum += next;
        }

        //scale to the desired target sum
        double scale = 1d * targetSum / sum;
        sum = 0;
        for (int i = 0; i < numberOfDraws; i++) {
            load.set(i, (int) (load.get(i) * scale));
            sum += load.get(i);
        }

        //take rounding issues into account
        while(sum++ < targetSum) {
            int i = r.nextInt(numberOfDraws);
            load.set(i, load.get(i) + 1);
        }
        return load;
    }


    private static Map<Terrain,List<Integer>> buildTerrainList(ProblemSpec ps){
        //make terrainOrder in ProblemSpec public
        Map<Terrain,List<Integer>> terrainListList=new HashMap<>();
        List<Integer> fullTerrainInt=new ArrayList<>();
        for (int i=1;i<ps.getN()+1;i++)
            fullTerrainInt.add(i);
        Collections.shuffle(fullTerrainInt);
        int divide = (ps.getN()/ps.terrainOrder.size());
        for (int i=0;i<ps.terrainOrder.size()-1;i++) {
            terrainListList.put(ps.terrainOrder.get(i),fullTerrainInt.subList(divide * i, divide * (i+1)));
        }
        terrainListList.put(ps.terrainOrder.get(ps.terrainOrder.size()-1),fullTerrainInt.subList(divide*(ps.terrainOrder.size()-1),fullTerrainInt.size()));
        return terrainListList;
    }
}

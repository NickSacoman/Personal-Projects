package problem;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Class to write output to the file
 *
 * Created by Harry on 9/10/2018.
 */
public class FileOutput {

    private String outputFile;

    public FileOutput(String outputFile) {
        this.outputFile = outputFile;
    }

    public void writeRobot(List<RobotConfig> robotPath) throws IOException{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
        try {
            writer.flush();
            // line 1
            writer.newLine();


        } finally
        {
            writer.flush();
            writer.close();
        }
    }

}

package engine;

import java.io.*;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String path) throws Exception {
        StringBuilder result = new StringBuilder();
        String line = null;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                result.append(line + '\n');
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" +path + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + path + "'");
        }
        return result.toString();
    }
}

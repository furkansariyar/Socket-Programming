import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

// Includes utility methods related Database
public class DatabaseController {

    // Read file line by line, and each line putted in hashmap
    public static HashMap<Integer, String> readFile(File file) {
        HashMap<Integer, String> data = new HashMap<Integer, String>();
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] separatedLine = line.split(",", 2);
                data.put(Integer.valueOf(separatedLine[0]), separatedLine[1]);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }

     /* Read file line by line
      * Data in row putted in hashmap and this hashmap putted in another hashmap with row id
      * Return external hashmap
      */
    public static HashMap<Integer, HashMap> readDetailFile(File file) {
        HashMap<Integer, HashMap> data = new HashMap<Integer, HashMap>();
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                HashMap<String, String> detailData = new HashMap<String, String>();
                String[] separatedLine = line.split(",");
                detailData.put("Target", separatedLine[1]); // just for airlines
                detailData.put("Date", separatedLine[2]);
                detailData.put("Engaged", separatedLine[3]);
                detailData.put("Capacity", separatedLine[4]);
                data.put(Integer.parseInt(separatedLine[0]), detailData);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }

    // update file
    // checks date and find the relevant row
    // then update full room/seat in the row
    public static void updateFile(File fileName, String date, int travellerCount) {
        try {
            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(fileName.getName()));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            // read each line
            while ((line = file.readLine()) != null) {
                String[] separatedLine = line.split(",");
                if (separatedLine[2].equals(date)) {    // update line
                    separatedLine[3]=String.valueOf(Integer.parseInt(separatedLine[3])+travellerCount); // full room/seat updated
                    inputBuffer.append(separatedLine[0]+","+separatedLine[1]+","+separatedLine[2]+","+separatedLine[3]+","+separatedLine[4]);
                    inputBuffer.append('\n');
                }
                else {  // other line does not change
                    inputBuffer.append(line);
                    inputBuffer.append('\n');
                }
            }

            file.close();
            String inputStr = inputBuffer.toString();
            // write the new string with the replaced line over the same file
            FileOutputStream fileOut = new FileOutputStream(fileName.getName());
            fileOut.write(inputStr.getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }
}

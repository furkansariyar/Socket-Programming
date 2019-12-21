import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DatabaseController {

    private static File hotelsFile;
    private ArrayList<String> hotels = new ArrayList<String>();

    public DatabaseController(TripDetail tripDetail) {
        //hotels=readFile(hotelsFile);
        System.out.println("*-*-*-*-*--*-* " + tripDetail);
        /*if (tripDetail.preferredHotels.get(0)==hotels.get(0)) {
            System.out.println("---****----***");
        }*/
    }

    public static void UpdateHotels() {
        hotelsFile = new File("Hotels.txt");
        createFile(hotelsFile); // checking file is exist or not
        readFile(hotelsFile);
        System.out.println("-------------------");
        //updateFile(hotelsFile, "suvasotel");

    }

    public static void createFile(File file) {
        try {
            // if the file does not exist, then create
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static HashMap<Integer, String> readFile(File file) {
        HashMap<Integer, String> data = new HashMap<Integer, String>();
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                //System.out.println(line);

                String[] separatedLine = line.split(",", 2);
                data.put(Integer.valueOf(separatedLine[0]), separatedLine[1]);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }

    public static HashMap<Integer, HashMap> readDetailFile(File file) {

        HashMap<Integer, HashMap> data = new HashMap<Integer, HashMap>();
        //int id;

        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                //System.out.println(line);
                //detailData.clear();
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

    public static void updateFile(File fileName, String newData) {
        try {
            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(fileName.getName()));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            inputBuffer.append(newData);

            file.close();
            String inputStr = inputBuffer.toString();

            System.out.println(inputStr); // display the original file for debugging

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(fileName.getName());
            fileOut.write(inputStr.getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    public void getAllHotel(){

    }

    /*public static void main(String[] args) {
        UpdateHotels();
    }*/

}

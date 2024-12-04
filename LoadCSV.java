import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoadCSV {
    //only use multiple paths if you want to create a version that
    //updates your csv file into new files
    private static String csvPath = "Original path";
    private static String newPath = "Second path (if updating csv into new files)";
    private static String finalPath = "Third path (if updating csv into new files)";

    public static List<double[]> loadData(String csvFile) throws IOException {
        File first = Imputer.impute("Age", new File(csvPath), new File(newPath));
        File last = Imputer.impute("Salary", first, new File(finalPath));

        List<double[]> data = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(last));
        String line;

        br.readLine();

        while ((line = br.readLine()) != null){
            String[] values = line.split(",");
            double[] features = new double[5];

            String country = values[0];
            if (country.equals("France")) {features[0] = 1;}
            else if (country.equals("Spain")) {features[1] = 1;}
            else if (country.equals("Germany")) {features[2] = 1;}

            features[3] = Double.parseDouble(values[1]);
            features[4] = Double.parseDouble(values[2]);

            double purchased;
            if (values[3].equals("Yes")) {purchased = 1;}
            else {purchased = 0;}

            double[] dataPoint = new double[features.length + 1];
            System.arraycopy(features, 0, dataPoint, 0, features.length);
            dataPoint[features.length] = purchased;
            data.add(dataPoint);
        }
        br.close();
        return data;
    }
}

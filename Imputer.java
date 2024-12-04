import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class Imputer {
    public static File impute(String param, File input, File output) throws IOException {
        FileReader reader = new FileReader(input);
        CSVFormat format = CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();
        CSVParser csvParser = new CSVParser(reader, format);

        List<Integer> nums = new ArrayList<>();
        List<CSVRecord> records = new ArrayList<>();

        for (CSVRecord record : csvParser){
            String str = record.get(param);
            if (!str.isEmpty()){
                nums.add(Integer.parseInt(str));
            }
            records.add(record);
        }

        int mean = (int) nums.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0);

        csvParser.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        CSVPrinter csvPrinter = new CSVPrinter(writer, format);

        csvPrinter.printRecord(records.get(0).toMap().keySet());

        for (CSVRecord record : records){
            Map<String, String> updatedRecord = new LinkedHashMap<>(record.toMap());
            
            if (updatedRecord.get(param).isEmpty()){
                updatedRecord.put(param, String.valueOf(mean));
            }

            csvPrinter.printRecord(updatedRecord.values());
        }

        csvPrinter.close();

        return output;
    }
}

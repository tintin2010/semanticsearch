package pitt.search.semanticvectors.tables;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.VectorStoreWriter;
import pitt.search.semanticvectors.VerbatimLogger;

/**
 * Class that reads input data from a stream and organizes it into records and columns.
 * 
 * @author dwiddows
 */
public class TableIndexer {

  public static final String usageMessage =
      "Usage: java pitt.search.semanticvectors.tables.TableIndexer [--args] $TABLE_CSV_FILENAME";
  
  public static Table createTable(FlagConfig flagConfig, String headerLine, ArrayList<String> dataLines) {  
    String[] columnNames = headerLine.split(",");

    Table table = new Table(flagConfig, columnNames.length, columnNames);
    table.prepareTypeSchema(dataLines);

    for (String line : dataLines) {
      table.addRowFromString(line);
    }
    
    return table;
  }

  public static void main(String[] args) throws IOException {
    FlagConfig flagConfig = null;
    try {
      flagConfig = FlagConfig.getFlagConfig(args);
      args = flagConfig.remainingArgs;
    } catch (IllegalArgumentException e) {
      System.err.println(usageMessage);
      throw e;
    }
    
    VerbatimLogger.info("Building vector index of table in file: " + args[0] + "\n");
    BufferedReader fileReader = new BufferedReader(new FileReader(args[0]));
    ArrayList<String> dataLines = new ArrayList<>();
    String headerLine = fileReader.readLine();
    String dataLine;
    while((dataLine = fileReader.readLine()) != null) {
      dataLines.add(dataLine);
    }
    fileReader.close();
    
    Table table = TableIndexer.createTable(flagConfig, headerLine, dataLines);
    VectorStoreWriter.writeVectors(flagConfig.termvectorsfile(), flagConfig, table.getRowVectorStore());
  }
}

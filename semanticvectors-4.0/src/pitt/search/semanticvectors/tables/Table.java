package pitt.search.semanticvectors.tables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.VectorStore;
import pitt.search.semanticvectors.VectorStoreOrthographical;
import pitt.search.semanticvectors.VectorStoreRAM;
import pitt.search.semanticvectors.hashing.Bobcat;
import pitt.search.semanticvectors.vectors.Vector;
import pitt.search.semanticvectors.vectors.VectorFactory;

/**
 * Represents a table of data. This includes:
 * <ul>
 * <li> Column labels </li>
 * <li> Column types </li>
 * <li> Homogeneous rows </li>
 * </ul>
 * 
 * @author dwiddows
 */
public class Table {
  private FlagConfig flagConfig;
  private int numColumns;
  private ObjectVector[] columnHeaders;
  private TypeSpec[] columnTypes;
  private ArrayList<TableRow> rows;
  private VectorStoreRAM rowSummaryVectors;
  private VectorStoreOrthographical orthographicVectorStore;

  public Table(FlagConfig flagConfig, int numColumns, String[] columnNames) {
    this.flagConfig = flagConfig;
    this.orthographicVectorStore = new VectorStoreOrthographical(flagConfig);
    this.numColumns = numColumns;
    this.columnHeaders = new ObjectVector[columnNames.length];
    this.columnTypes = new TypeSpec[columnNames.length];
    for (int i = 0; i < columnNames.length; ++i) {
      Vector columnNameVector = VectorFactory.generateRandomVector(
          flagConfig.vectortype(), flagConfig.dimension(), flagConfig.seedlength,
          new Random(Bobcat.asLong(columnNames[i])));
      this.columnHeaders[i] = new ObjectVector(columnNames[i], columnNameVector);
    }
    this.rowSummaryVectors = new VectorStoreRAM(flagConfig);
    this.rows = new ArrayList<>();
  }
  
  public VectorStore getRowVectorStore() {
    return this.rowSummaryVectors;
  }
  
  public void addRowFromString(String rowString) {
    String[] rowValues = rowString.split(",");
    if (rowValues.length != numColumns) {
      throw new IllegalArgumentException(String.format(
          "rowString should have %d entries.\n%s", numColumns, rowString));
    }
    TableRow newRow = new TableRow(
        flagConfig, orthographicVectorStore, rowString.split(","), columnHeaders);
    rows.add(newRow);
    rowSummaryVectors.putVector(newRow.rowVector.getObject(), newRow.rowVector.getVector());
  }
  
  public Iterator<TableRow> getRows() {
    return rows.iterator();
  }

  public void prepareTypeSchema(ArrayList<String> dataLines) {
    
  }
}

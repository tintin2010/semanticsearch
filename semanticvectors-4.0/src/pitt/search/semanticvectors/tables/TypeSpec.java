package pitt.search.semanticvectors.tables;

/**
 * This class represents a "type" for a list of data items.
 * 
 * @author dwiddows
 */
public class TypeSpec {

  public enum SupportedType {
    STRING,
    DOUBLE,
    // Date would be nice, but predicting formats would be hard.
    // Integers will be treated as continuous by vector libraries, so there's no point distinguishing them.
  }
  
  private SupportedType type;
  /** Returns the {@link type}. */
  public Object getType() {
    return type;
  }
  
  private double minDoubleValue;
  /** Returns the {@link minDoubleValue} if {@link type} is {@link DOUBLE}. */
  public double getMinDoubleValue() {
    if (type != SupportedType.DOUBLE)
      throw new IllegalArgumentException("Must have type DOUBLE, not " + type);
    return minDoubleValue;
  }
  
  private double maxDoubleValue;
  /** Returns the {@link maxDoubleValue} if {@link type} is {@link DOUBLE}. */
  public double getMaxDoubleValue() {
    if (type != SupportedType.DOUBLE)
      throw new IllegalArgumentException("Must have type DOUBLE, not " + type);
    return maxDoubleValue;
  }
  
  /** See {@link #getEmptyType()} */
  private TypeSpec() {
    this.type = SupportedType.DOUBLE;
    this.minDoubleValue = Double.MAX_VALUE;
    this.maxDoubleValue = Double.MIN_VALUE;
  }
  
  /**
   * Returns an 'empty' type, which is actually a double with very lax endpoints.
   * Every example will be used to constrain this.
   */
  public static TypeSpec getEmptyType() {
    return new TypeSpec();
  }
  
  /**
   * Adds a new example to this TypeSpec. If the new example won't parse as any other {@link SupportedType},
   * the {@link #type} is set to {@link SupportedType#STRING}. If it parses as a compatible type, the
   * min and max values are modified accordingly.
   */
  public void addExample(String example) {
    switch (this.type) {
    case STRING:
      return;
    case DOUBLE:
      try {
        Double newDouble = Double.parseDouble(example);
        if (newDouble < this.minDoubleValue)
          this.minDoubleValue = newDouble;
        if (newDouble > this.maxDoubleValue)
          this.maxDoubleValue = newDouble;
      } catch (NumberFormatException e) {
        this.type = SupportedType.STRING;
        return;
      }
    }
  }
}

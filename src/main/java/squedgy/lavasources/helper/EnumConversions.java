package squedgy.lavasources.helper;

/**
 *
 * @author David
 */
public enum EnumConversions {
	SECONDS_TO_TICKS(20),
	TICKS_TO_SECONDS(1/20);

	private final double MULTIPLIER;
	
	EnumConversions(int multiplier){ this.MULTIPLIER = multiplier; }
	
	public int convertToInt(double toBeConverted){ return (int)(toBeConverted * MULTIPLIER); }
	
	public double convertToDouble(double toBeConverted){ return toBeConverted * MULTIPLIER; }
}

package squedgy.lavasources.helper;

/**
 *
 * @author David
 */
public enum EnumConversions {
	SECONDS_TO_TICKS(d -> d * 20.),
	TICKS_TO_SECONDS(d -> d / 20.);

	private final Converter converter;
	
	EnumConversions( Converter converter){
		this.converter = converter;
	}
	
	public int convertToInt(double toBeConverted){ return (int)converter.convert(toBeConverted); }
	
	public double convertToDouble(double toBeConverted){ return converter.convert(toBeConverted); }

	//In case there is a ever a more complicated conversion to be done
	@FunctionalInterface private interface Converter{
		public abstract double convert(double toConvert);
	}
}

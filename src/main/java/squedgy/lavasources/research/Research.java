package squedgy.lavasources.research;

import jline.internal.Nullable;
import squedgy.lavasources.LavaSources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Research {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private static List<Research> ALL_RESEARCH = new ArrayList<>();
	private String name;
	private final List<Research> DEPENDENCIES = new ArrayList<>();

	public Research(String name, Research... dependencies){
		this.name = name;
		DEPENDENCIES.addAll(Arrays.asList(dependencies));
	}


//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public static List<Research> getAllResearch(){ return new ArrayList(ALL_RESEARCH); }
	public static void addNewResearch(Research r){
		if(ALL_RESEARCH.indexOf(r) < 0){
			LavaSources.writeMessage(Research.class, "just added research with name: " + r.getName());
			ALL_RESEARCH.add(r);
		}
	}
	public static @Nullable Research getResearch(String r){ return ALL_RESEARCH.stream().filter((re) -> re.getName().equals(r)).findFirst().orElse(null); }

	public List<Research> getDependencies(){ return new ArrayList(DEPENDENCIES); }
	public String getName(){ return name; }
	public void setName(String newName){ name = newName; }

//</editor-fold>

	@Override
	public String toString() {
		return "Research{name="+name+", dependencies="+this.DEPENDENCIES+"}";
	}
}

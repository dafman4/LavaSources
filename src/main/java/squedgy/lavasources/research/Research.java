package squedgy.lavasources.research;

import com.google.common.collect.Lists;
import jline.internal.Nullable;
import squedgy.lavasources.capabilities.PlayerResearchCapability;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Research {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private static List<Research> ALL_RESEARCH = Lists.newArrayList();
	private String name;
	private final List<Research> DEPENDENCIES = Lists.newArrayList();

	public Research(String name, Research... dependencies){
		this.name = name;
		DEPENDENCIES.addAll(Arrays.asList(dependencies));
	}


//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public static List<Research> getAllResearch(){ return ALL_RESEARCH; }
	public static void addNewResearch(Research r){
		if(ALL_RESEARCH.indexOf(r) < 0){
			ALL_RESEARCH.add(r);
		}
	}
	public static @Nullable  Research getResearch(String r){ return ALL_RESEARCH.stream().filter((re) -> re.getName().equals(r)).findFirst().orElse(null); }

	public List<Research> getDependencies(){ return Lists.newArrayList(DEPENDENCIES); }
	public String getName(){ return name; }
	public void setName(String newName){ name = newName; }

//</editor-fold>

}

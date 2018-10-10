package squedgy.lavasources.generic;

import squedgy.lavasources.research.Research;

import java.util.List;

public interface IModCraftable {
	List<Research> getRequirements();
	void addRequirement(Research requirement);
	public default boolean hasRequirements(){ return getRequirements().size() > 0; }
}

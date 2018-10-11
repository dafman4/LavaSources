package squedgy.lavasources.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import squedgy.lavasources.research.Research;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IPlayerResearchCapability extends INBTSerializable<NBTTagCompound> {

	public static Collection<Research> AllResearch(){ return GameRegistry.findRegistry(Research.class).getValuesCollection(); }

	public abstract List<Research> getResearch();

	public abstract void addResearch(Research r);

	public abstract boolean hasResearch(Research r);

	public static class Storage implements Capability.IStorage<IPlayerResearchCapability>{

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<IPlayerResearchCapability> capability, IPlayerResearchCapability instance, EnumFacing side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<IPlayerResearchCapability> capability, IPlayerResearchCapability instance, EnumFacing side, NBTBase nbt) {
			if(nbt instanceof NBTTagCompound) instance.deserializeNBT((NBTTagCompound) nbt);
		}
	}

}

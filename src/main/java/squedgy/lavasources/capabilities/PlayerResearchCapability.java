package squedgy.lavasources.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.research.Research;
import squedgy.lavasources.research.ResearchUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerResearchCapability implements IPlayerResearchCapability {
	private final List<Research> RESEARCH = new ArrayList();

	@Override
	public List<Research> getResearch() { return RESEARCH; }

	@Override
	public void addResearch(Research r) { if(!hasResearch(r)) RESEARCH.add(r); }

	@Override
	public boolean hasResearch(Research r) { return RESEARCH.indexOf(r) >= 0; }

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		for(Research r : RESEARCH)if(r != null) tag.setString(r.getName(),"");
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		RESEARCH.clear();
		for(String s : nbt.getKeySet()) RESEARCH.add(ResearchUtil.getResearch(s));
	}

	public static class Provider implements ICapabilitySerializable<NBTTagCompound>{

		private IPlayerResearchCapability instance = ModCapabilities.PLAYER_RESEARCH_CAPABILITY.getDefaultInstance();

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == ModCapabilities.PLAYER_RESEARCH_CAPABILITY;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			return capability == ModCapabilities.PLAYER_RESEARCH_CAPABILITY ?(T) instance : null;
		}

		@Override
		public NBTTagCompound serializeNBT() { return instance.serializeNBT(); }

		@Override
		public void deserializeNBT(NBTTagCompound nbt) { instance.deserializeNBT(nbt); }
	}
}

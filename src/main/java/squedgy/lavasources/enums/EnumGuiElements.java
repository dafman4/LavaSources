package squedgy.lavasources.enums;

import jline.internal.Nullable;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import squedgy.lavasources.generic.IContainerCreator;
import squedgy.lavasources.generic.IGuiCreator;
import squedgy.lavasources.gui.*;
import squedgy.lavasources.inventory.ContainerCoreModifier;
import squedgy.lavasources.inventory.ContainerLiquefier;

/**
 *
 * @author David
 */
public enum EnumGuiElements {
	CORE_MODIFIER(ContainerCoreModifier::new, GuiCoreModifier::new),
	LIQUEFIER(ContainerLiquefier::new, GuiLiquefier::new),
	GUIDE_BOOK(null, GuiGuideBook::new, true)

	;
	private final IContainerCreator CONTAINER_CREATOR;
	private final IGuiCreator GUI_CREATOR;
	public final boolean ITEM;


	private EnumGuiElements(){ this(null, GuiDefault::new); }

	private EnumGuiElements(IContainerCreator conCreator, IGuiCreator guiCreator){
		this(conCreator, guiCreator, false);
	}

	private EnumGuiElements(IContainerCreator conCreator, IGuiCreator guiCreator, boolean item){
		this.CONTAINER_CREATOR = conCreator;
		this.GUI_CREATOR = guiCreator != null ? guiCreator : GuiDefault::new;
		this.ITEM = item;
	}

	public @Nullable Container getContainer(InventoryPlayer player, IInventory inventory){
		if(CONTAINER_CREATOR == null) return new ContainerEmpty();
		return CONTAINER_CREATOR.getContainer(player, inventory);
	}

	public @Nullable Gui getGui(InventoryPlayer player, IInventory inventory){ return GUI_CREATOR.createGui(player, inventory); }

}

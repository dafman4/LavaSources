package squedgy.lavasources.gui;

import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

public class ContainerEmpty extends net.minecraft.inventory.Container{

	public ContainerEmpty(){
		super();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) { return false; }
}

package squedgy.lavasources.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import squedgy.lavasources.LavaSources;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class CraftingUtils {

	private static final class FieldPredicate implements Predicate<Field>{
		public Class<?> clazz;
		public boolean flag = true;
		@Override
		public boolean test(Field field) {
			flag = field.isAccessible();
			field.setAccessible(true);
			if(field.getType().equals(clazz))
				return true;
			field.setAccessible(flag);
			return false;
		}
	}

	public static EntityPlayer getPlayerIfPossible(InventoryCrafting inv) throws IllegalAccessException {
		FieldPredicate pred = new FieldPredicate();
		pred.clazz = Container.class;
		Optional<Field> test = Arrays.stream(inv.getClass().getDeclaredFields()).filter(pred).findFirst();
		if(test.isPresent() && test.get().getType() == Container.class) {
			Container c = (Container) test.get().get(inv);
			test.get().setAccessible(pred.flag);
			test = Optional.empty();
			Object passIn = c;
			if(c instanceof ContainerPlayer || c instanceof ContainerWorkbench) {
				pred.clazz = net.minecraft.entity.player.EntityPlayer.class;
				passIn = (c.inventorySlots.get(0));
				test = Arrays.stream(passIn.getClass().getDeclaredFields()).filter(pred).findFirst();
			}
			if(test.isPresent()){
				Object o = test.get().get(passIn);
				if(o instanceof  EntityPlayer){
					EntityPlayer ret = (EntityPlayer) test.get().get(passIn);
					test.get().setAccessible(pred.flag);
					return ret;
				}
			}
		}
		return null;
	}
}

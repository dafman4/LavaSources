package squedgy.lavasources.crafting;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import scala.Char;
import squedgy.lavasources.LavaSources;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

	public static String getJsonObjectPrettyPrinted(JsonObject object){
		String json = object.toString();

		for(int i = 0, tabs = 0; i < json.length(); i++){
			int index = i;
			StringBuilder a = new StringBuilder(json.substring(0, index+1));
			StringBuilder b = new StringBuilder(json.substring(index+1));
			if(json.charAt(i) == ('{') || json.charAt(i) == '['){
				a.append("\n");
				append(a, "\t", ++tabs);
				i += tabs + 1;
			}else if( i < json.length()-1 && (json.charAt(i+1) == '}' || json.charAt(i+1) == ']')){
				a.append('\n');
				append(a, "\t", --tabs);
				i += tabs+1;
			}else if(json.charAt(i) == ','){
				a.append("\n");
				append(a, "\t", tabs);
				i += tabs+1;
			}
			json = a.toString() + b.toString();
		}
		return json;
	}

	private static void append(StringBuilder b, String append, int amount){
		for (int i = 0; i < amount; i++) {
			b.append(append);
		}
	}
}

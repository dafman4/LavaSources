
package squedgy.lavasources.factory;

import java.lang.reflect.Constructor;

// Author David
public abstract class GenericFactory {
	
	/**
	 * This method returns an object with the fully qualified name className then attempts to cast that to
	 * Type &lt;T&gt; returning the generated class,casted to T, there needs to be a constructor with 0
	 * arguments for the generated class as well.
	 * @param <T> the type that you want the generated object to be
	 * @param className the fully qualified name of the object being generated
	 * @return class &lt;className&gt; of type &lt;T&gt;
	 * @throws Exception if there is an issue with the fully qualified name, or casting
	 */
	public static <T> T getObject(String className) throws Exception {
		try{
			Class clazz = Class.forName(className);
			Constructor cons = clazz.getDeclaredConstructor(new Class[0]);
			if(!cons.isAccessible())
				cons.setAccessible(true);
			T ret = (T)cons.newInstance(new Object[0]);
			return ret;
		}catch(Exception e){
			throw new Exception("There was an issue instantiating class: " + className + " with error : " + e.getMessage());
		}
	}
}

package squedgy.lavasources.generic;

@FunctionalInterface
public interface IReturnable<T> {
	T getObject(Object... params);
}

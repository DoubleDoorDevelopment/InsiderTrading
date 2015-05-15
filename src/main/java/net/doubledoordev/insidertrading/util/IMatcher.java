package net.doubledoordev.insidertrading.util;

/**
 * @author Dries007
 */
public interface IMatcher<T>
{
    boolean matches(T obj);
}

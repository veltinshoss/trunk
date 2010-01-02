/**
 * 
 */
package monkeypuzzle.io.parser.plist.dynamicproxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import monkeypuzzle.io.dynamicproxy.DynamicProxy;
import monkeypuzzle.io.dynamicproxy.DynamicProxyException;
import monkeypuzzle.io.parser.plist.PListContainer;
import monkeypuzzle.io.parser.plist.PListDict;

public class PListDynamicProxy<T> extends DynamicProxy<T>
{

	@SuppressWarnings("unchecked")
	public static <S> S newInstance(final Class<S> interfaceDef,
			final PListDict dict)
	{
		return (S) Proxy.newProxyInstance(interfaceDef.getClassLoader(),
				new Class[] { interfaceDef }, new PListDynamicProxy<S>(
						interfaceDef, dict));
	}

	private static boolean containsException(final Class<?>[] exceptionTypes,
			final Class<DynamicProxyException> givenException)
	{
		for (Class<?> e : exceptionTypes)
			if (givenException.isAssignableFrom(e))
				return true;
		return false;
	}

	private PListDict dict;

	private PListDynamicProxy(final Class<T> interfaceDef, final PListDict dict)
	{
		super(interfaceDef);
		this.dict = dict;
	}

	@SuppressWarnings("unchecked")
	// can't figure out any other way
	public Object invoke(final Object proxy, final Method m, final Object[] args)
			throws Throwable
	{
		if (m.equals(Object.class.getMethod("toString", (Class<?>[]) null)))
			return dynamicToString(this.interfaceDef, proxy);

		String name;
		PListAnnotationEntry annotation = m
				.getAnnotation(PListAnnotationEntry.class);

		// find key in dictionary to look for.
		if (annotation == null)
		{
			name = m.getName().substring(3);
		} else
		{
			name = annotation.value();
			if ((name == null) || (name.length() == 0))
				return this.dict;
		}

		PListContainer valuetype = this.dict.get(name);

		if (valuetype != null)
		{
			T returnValue;
			// check if we return the node directly or if we need to create a
			// dynamic proxy
			if (PListContainer.class
					.isAssignableFrom(getUnderlyingReturnType(m)))
			{
				returnValue = (T) valuetype;
			} else
			{
				returnValue = valuetype.getAsInterface((Class<T>) m
						.getReturnType());
			}
			return DynamicProxy.coerceValueToType(m.getReturnType(),
					returnValue);
		}

		// error case
		if (containsException(m.getExceptionTypes(),
				DynamicProxyException.class))
			throw new DynamicProxyException("\"" + name
					+ "\" could not be found within "
					+ this.dict.keySet().toString());
		else
			// return a default value
			return m.getReturnType().isPrimitive() ? (m.getReturnType()
					.isAssignableFrom(Boolean.TYPE) ? false : 0) : null;

	}

	@Override
	protected boolean isMethodVisible(final Method m)
	{
		// always - there are no key fields in a PList
		return true;
	}
}
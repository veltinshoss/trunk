package com.crypticbit.ipa.io.dynamicproxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.util.FormattedArrayList;
import com.crypticbit.ipa.util.TypeFormatter;


public abstract class DynamicProxy<T> implements
		java.lang.reflect.InvocationHandler
{

	public static Object coerceValueToType(final Class<?> returnType,
			final Object value) throws DynamicProxyException
	{
		if ((value == null) || returnType.isPrimitive()
				|| returnType.isAssignableFrom(value.getClass()))
			return value;
		else
		{
			try
			{
				Constructor<?> constructor = null;
				for (Constructor<?> c : returnType.getConstructors())
					if ((c.getParameterTypes().length == 1)
							&& (c.getParameterTypes()[0] == value.getClass()))
					{
						constructor = c;
					}

				if (constructor != null)
					return constructor.newInstance(value);
				else
					return returnType.getDeclaredMethod("convert",
							value.getClass()).invoke(null, value);

			} catch (Exception e)
			{
				throw new DynamicProxyException("Problem mapping \""
						+ value.getClass() + "\" to " + returnType, e);
			}
		}
	}

	/**
	 * Get the type of interface to return. Specifically for a method with a
	 * return type:
	 * <ul>
	 * <li>List&lt;A&gt; this returns A
	 * <li>A[] this returns A - not implemented yet
	 * <li>A this returns A
	 * </ul>
	 * 
	 * @param method
	 * @return
	 */
	protected static Class<?> getUnderlyingReturnType(final Method method)
	{
		// FIXME use method below
		if (method.getGenericReturnType() instanceof ParameterizedType)
			return ((Class<?>) ((ParameterizedType) method
					.getGenericReturnType()).getActualTypeArguments()[0]);
		else
			return method.getReturnType();
	}

	protected static Type getUnderlyingType(final Type type)
	{
		if (type instanceof ParameterizedType)
			return (((ParameterizedType) type).getActualTypeArguments()[0]);
		else
			return type;
	}

	protected Class<?> interfaceDef;

	public DynamicProxy(final Class<T> interfaceDef)
	{
		this.interfaceDef = interfaceDef;
	}

	protected String dynamicToString(final Class<?> interface0,
			final Object proxy)
	{
		List<String> result = new FormattedArrayList<String>();
		Set<String> duplicates = new HashSet<String>();
		for (Method m : interface0.getMethods())
		{

			if (m.getName().startsWith("get")
					&& (m.getParameterTypes().length == 0)
					&& isMethodVisible(m) && duplicates.add(m.getName()))
			{
				try
				{
					Object invokeResponse = m.invoke(proxy);
					result.add(m.getName().substring(3) + " = "
							+ TypeFormatter.formatTypeAsString(invokeResponse));
				} catch (Exception e)
				{
					LogFactory.getLogger().log(Level.SEVERE,"Exception",e);
					result.add(m.getName().substring(3) + "=<error>");
				}
			}
		}
		return result.toString();
	}

	protected abstract boolean isMethodVisible(Method m);

}

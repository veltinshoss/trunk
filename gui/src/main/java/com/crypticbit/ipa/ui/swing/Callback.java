/**
 * 
 */
package com.crypticbit.ipa.ui.swing;


public abstract class Callback
{
	public static Callback doNothing()
	{
		return new Callback() {

			@Override
			void callback(final ViewingPane viewingPane)
			{
				// do nothing
			}
		};
	}

	abstract void callback(ViewingPane viewingPane);
}
package monkeypuzzle.ui.swing;

import java.awt.Frame;

import javax.swing.JOptionPane;

import monkeypuzzle.licence.ValidatorUi;

public class LicenceRequestor implements ValidatorUi
{

	private Frame frame;

	public LicenceRequestor(final Frame owner)
	{
		this.frame = owner;
	}

	@Override
	public String getCustomerNumber()
	{
		return (String) JOptionPane.showInputDialog(this.frame,
				"Licence Key...", "Licence validation",
				JOptionPane.PLAIN_MESSAGE, null, null, null);
	}

}

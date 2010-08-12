package monkeypuzzle.ui.swing;

import java.awt.Desktop;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.jdesktop.swingx.JXTitledPanel;

public class ProductMessage extends JXTitledPanel implements HyperlinkListener {

	ProductMessage() {
		super("Product Message");
		getContentContainer().setLayout(new GridLayout(1, 1));
		JEditorPane tc;

		tc = new JEditorPane("text/html","iPhone Analyzer has been brought to you free by "
						+ "<a href=\"http://www.crypticbit.com/screenshots.php\">"
						+ "crypticbit.com</a>. If you are a developer and "
						+ "would like to join the project - get in touch!. If you are a user and have found this "
						+ "tool useful you can show your appreciation and help ensure the continued "
						+ "development of iPhoneAnalyzer by "
						+ "<a href=\"https://sourceforge.net/donate/index.php?group_id=296588\">contributing here</a>."
						+ "If you are an organisation then you can use our consultancy services to get any changes you wish to this product, as well as a wide range of development and training services.<br><i>Don't worry - this message disappears as soon as you do a search. We don't want it to get in your way.");
		tc.addHyperlinkListener(this);
		tc.setEditable(false);
		this.add(tc);

	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if(e.getEventType() == EventType.ACTIVATED)
		try {
			Desktop.getDesktop().browse(e.getURL().toURI());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}


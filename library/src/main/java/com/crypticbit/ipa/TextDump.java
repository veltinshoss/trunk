package com.crypticbit.ipa;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.IPhoneFactory;
import com.crypticbit.ipa.central.IPhoneParseException;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.NavigateException;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.entity.settings.MailAccounts;
import com.crypticbit.ipa.entity.settings.SafariBookmarks;
import com.crypticbit.ipa.entity.status.Manifest;
import com.crypticbit.ipa.io.parser.plist.PListDict;
import com.crypticbit.ipa.licence.NotLicencedException;
import com.crypticbit.ipa.licence.UnlimitedLicenceValidator;
import com.crypticbit.ipa.results.TextSearchAlgorithm;


/**
 * takes a single backup directory as an argument and dumps supported data as
 * plain text to stdout
 * 
 * @author mat
 * 
 */
public class TextDump
{
	static final String USAGE = "java -jar iphoneDump.jar [-e <export directory>] <backup directory>";

	/**
	 * @param args
	 * @throws NavigateException
	 * @throws NotLicencedException
	 */
	public static void main(final String[] args) throws NavigateException,
			NotLicencedException
	{
		if ((args.length != 1) && (args.length != 3))
		{
			die(USAGE);
		}

		File f = new File(args[args.length - 1]);

		if (!f.isDirectory())
		{
			die("Supplied argument is not a directory: '"
					+ args[args.length - 1] + "'");
		}
		if (!f.canRead())
		{
			die("Unable to read directory: " + args[args.length - 1]);
		}

		try
		{
			System.out.println(About.getAll());

			IPhone backupDir = new IPhoneFactory(
					UnlimitedLicenceValidator.DEFAULT_INSTANCE)
					.createIPhoneState(f,false);

			if (args.length == 3)
			{
				if (args[0].equals("-e"))
				{
					File exportTo = new File(args[1]);
					if (!exportTo.isDirectory())
					{
						die("Supplied export path is not a directory: '"
								+ args[1] + "'");
					}
					if (!exportTo.canRead())
					{
						die("Unable to read directory: " + args[1]);
					}
					backupDir.restoreDirectory(exportTo);
				} else
				{
					die(USAGE);
				}
			}

			System.out.println("The complete list of files parsed is : ");
			for (BackupFile bfd : backupDir.getParsedFiles())
			{
				System.out.println(bfd + " (" + bfd.getParserType() + ") ==> "
						+ bfd.getParsedData().getSummary());
			}

			System.out.println("In summary this equates to : ");
			System.out.println(backupDir.getCountOfBackupFileTypes());

			System.out.println("Device name is: "
					+ backupDir.getConfigElements().getInfo().getDeviceName());

			// System.out.println(backupDir.getAddressBook().getContents());
			// System.out.println(backupDir.getCalendar().getContents());
			// System.out.println(backupDir.getCallHistory().getContents());
			// System.out.println(backupDir.getMessages().getContents());

			System.out.println("An example of the exposed Preferences");
			MailAccounts m = backupDir.getByInterface(MailAccounts.class);
			if ((m != null) && (m.getDeliveryAccounts() != null))
			{
				for (MailAccounts.DeliveryAccounts d : m.getDeliveryAccounts())
				{
					System.out.println(d);
				}
				for (MailAccounts.MailAccount ma : m.getMailAccounts())
				{
					System.out.println(ma.getAccountPath() + ","
							+ ma.getAccountType() + ","
							+ ma.getDraftsMailboxName());
				}
			}
			SafariBookmarks sb = backupDir.getByInterface(SafariBookmarks.class);
			printBookMarks(sb, 0);
			for (BackupFile bfd : backupDir.getParsedFiles())
			{
				System.out.println("=== " + bfd + " In full ===\n"
						+ bfd.getParsedData().getContents() + "\n");

			}

			System.out.println("Search results for \"Ontario\"");
			System.out.println(backupDir.search(
					TextSearchAlgorithm.CASE_INSENSITIVE, "Ontario"));

			System.out.println("Search results for \"Google\"");
			System.out.println(backupDir.search(
					TextSearchAlgorithm.CASE_INSENSITIVE, "Google"));

			System.out.println("Search results for \"jake\"");
			System.out.println(backupDir.search(
					TextSearchAlgorithm.CASE_SENSITIVE, "jake"));

			System.out.println("Digging into the manifest");
			System.out.println(backupDir.getConfigElements().getManifest());
			System.out.println(backupDir.getConfigElements().getManifest()
					.getDataAsManifest());
			System.out.println(backupDir.getConfigElements().getManifest()
					.getDataAsManifest());
			System.out.println(backupDir.getConfigElements().getManifest()
					.getDataAsManifest().getDeviceId());
			System.out.println(((PListDict) backupDir.getConfigElements()
					.getManifest().getDataAsManifest().getFiles()).get(
					"109a2a5fd7979f659b91440bef1bcff2d468a4b1").getAsInterface(
					Manifest.ManifestEntry.FileEntry.class));

		} catch (IOException e)
		{
			LogFactory.getLogger().log(Level.SEVERE,"Exception",e);
			die("Error setting up backup directory: "
					+ e.getCause().getMessage());
		} catch (FileParseException e)
		{
			LogFactory.getLogger().log(Level.SEVERE,"Exception",e);
			die("Error parsing files: " + e.getCause().getMessage());
		} catch (IPhoneParseException e)
		{
			LogFactory.getLogger().log(Level.SEVERE,"Exception",e);
			die("Error reading BackupDirectory: " + e.getMessage());
		}

	}

	private static void die(final String lastWords)
	{
		System.err.println(lastWords);
		System.exit(1);
	}

	private static void printBookMarks(final SafariBookmarks sb,
			final int indent)
	{
		if (sb != null)
		{
			StringBuffer b = new StringBuffer();
			for (int loop = 0; loop < indent; loop++)
			{
				b.append(" ");
			}
			System.out.println(b
					+ sb.getTitle()
					+ ","
					+ sb.getURLString()
					+ ","
					+ (sb.getURIDictionary() == null ? "" : sb
							.getURIDictionary().getUrl()));
			if (sb.getChildren() != null)
			{
				for (SafariBookmarks sbb : sb.getChildren())
				{
					printBookMarks(sbb, indent + 1);
				}
			}
		}
	}
}

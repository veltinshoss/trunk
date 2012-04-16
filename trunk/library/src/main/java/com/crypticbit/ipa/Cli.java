package com.crypticbit.ipa;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.crypticbit.ipa.central.FileParseException;
import com.crypticbit.ipa.central.IPhone;
import com.crypticbit.ipa.central.IPhoneFactory;
import com.crypticbit.ipa.central.IPhoneParseException;
import com.crypticbit.ipa.central.LogFactory;
import com.crypticbit.ipa.central.backupfile.BackupFile;
import com.crypticbit.ipa.licence.NotLicencedException;
import com.crypticbit.ipa.licence.UnlimitedLicenceValidator;


/**
 * A command line interface for accessing some basic system capability. The enum
 * lists all supported functions.
 * 
 * @author Leo
 * 
 */
public class Cli
{

	private enum Commands
	{
		DUMP()
		{
			@Override
			protected void printHelp()
			{
				printCommandOption("<backup directory>");
			}

			@Override
			protected void run(final String[] args) throws Exception
			{
				INFO.run(args);
				LIST.run(args);
			}
		},
		EXTRACT()
		{

			@Override
			protected void printHelp()
			{
				printCommandOption("<backup directory> <output directory>");
			}

			@Override
			protected void run(final String[] args) throws IOException, FileParseException, IPhoneParseException, NotLicencedException
			{
				IPhone bd = getBackupDirectory(args[args.length - 2]);
				bd.restoreDirectory(new File(args[args.length -1]));
			}
		},
		FIND()
		{

			@Override
			protected void printHelp()
			{
				printCommandOption("<directory to start search>");
			}

			@Override
			protected void run(final String[] args) throws Exception
			{
				LogFactory.getLogger().log(Level.INFO,"This feature is not implemented yet");

			}

		},
		HELP()
		{

			@Override
			protected void printHelp()
			{
				printCommandOption("");
			}

			@Override
			protected void run(final String[] args)
			{
				System.out
						.println("usage: java Cli <command> <arguments> (pick from list below)");
				for (Commands c : Commands.values())
				{
					c.printHelp();
				}
			}

		},
		INFO()
		{

			@Override
			protected void printHelp()
			{
				printCommandOption("<backup directory>");
			}

			@Override
			protected void run(final String[] args) throws IOException,
					FileParseException, IPhoneParseException,
					NotLicencedException
			{
				IPhone bd = getBackupDirectory(args[args.length - 1]);
				outputPairs(new String[][] {
						{
								"Device name",
								bd.getConfigElements().getInfo()
										.getDeviceName() },
						{
								"Display name",
								bd.getConfigElements().getInfo()
										.getDisplayName() },
						{
								"Last backup date",
								bd.getConfigElements().getInfo()
										.getLastBackupDate().toString() },
						{
								"Phone number",
								bd.getConfigElements().getInfo()
										.getPhoneNumber() },
						{
								"Ptoduct type",
								bd.getConfigElements().getInfo()
										.getProductType() },
						{
								"Product version",
								bd.getConfigElements().getInfo()
										.getProductVersion() },
						{
								"Serial number",
								bd.getConfigElements().getInfo()
										.getSerialNumber() },
						{
								"Target Identifier",
								bd.getConfigElements().getInfo()
										.getTargetIdentifier() },
						{
								"Target type",
								bd.getConfigElements().getInfo()
										.getTargetType() },
						{
								"Unique identifier",
								bd.getConfigElements().getInfo()
										.getUniqueIdentifier() } });
			}

			private void outputPairs(final String[][] pairs)
			{
				for (String[] pair : pairs)
				{
					LogFactory.getLogger().log(Level.INFO,pair[0] + ": " + pair[1]);
				}

			}
		},
		LIST()
		{

			@Override
			protected void printHelp()
			{
				printCommandOption("<backup directory>");
			}

			@Override
			protected void run(final String[] args) throws IOException,
					FileParseException, IPhoneParseException,
					NotLicencedException
			{
				IPhone bd = getBackupDirectory(args[args.length - 1]);
				for (BackupFile bfd : bd.getParsedFiles())
				{
					LogFactory.getLogger().log(Level.INFO,bfd.getCompleteOriginalFileName() + " ("
							+ bfd.getParserType() + ")");
				}

			}
		};
		private static IPhone getBackupDirectory(final String filename)
				throws IOException, FileParseException, IPhoneParseException,
				NotLicencedException
		{
			return new IPhoneFactory(UnlimitedLicenceValidator.DEFAULT_INSTANCE)
					.createIPhoneState(new File(filename),false);
		}

		protected void printCommandOption(final String options)
		{
			LogFactory.getLogger().log(Level.INFO,"\t" + name().toLowerCase() + " " + options);

		}

		protected abstract void printHelp();

		protected abstract void run(String[] args) throws Exception;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		
		Commands command;
		try {command = args.length > 0 ? Commands.valueOf(args[0]
				.toUpperCase()) : null;
		} catch (IllegalArgumentException e) {
			command = null;
			LogFactory.getLogger().log(Level.WARNING,"\""+args[0]+"\" is not a valid command.");
		}
		if (command == null)
		{
			command = Commands.HELP;
		}
		try
		{
			command.run(args);
		} catch (Exception e)
		{
			LogFactory.getLogger().log(Level.WARNING,"Error executing command " + command);
			LogFactory.getLogger().log(Level.SEVERE,"Exception",e);
		}

	}

}

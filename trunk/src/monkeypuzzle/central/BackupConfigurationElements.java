package monkeypuzzle.central;

import monkeypuzzle.entity.status.Info;
import monkeypuzzle.entity.status.Manifest;
import monkeypuzzle.entity.status.Status;

public class BackupConfigurationElements
{
	private Info info;
	private Manifest manifest;
	private Status status;

	public BackupConfigurationElements(final Manifest manifest,
			final Status status, final Info info)
	{
		this.info = info;
		this.manifest = manifest;
		this.status = status;
	}

	public Info getInfo()
	{
		return this.info;
	}

	public Manifest getManifest()
	{
		return this.manifest;
	}

	public Status getStatus()
	{
		return this.status;
	}

}
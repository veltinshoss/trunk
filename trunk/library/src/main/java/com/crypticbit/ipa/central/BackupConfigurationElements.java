package com.crypticbit.ipa.central;

import com.crypticbit.ipa.entity.status.Info;
import com.crypticbit.ipa.entity.status.Manifest;
import com.crypticbit.ipa.entity.status.Status;

/**
 * A backup file typically contains these files which are metatdata about the
 * phone and backup
 */

public class BackupConfigurationElements {
	private Info info;
	private Manifest manifest;
	private Status status;

	public BackupConfigurationElements(final Manifest manifest,
			final Status status, final Info info) {
		this.info = info;
		this.manifest = manifest;
		this.status = status;
	}

	public Info getInfo() {
		return this.info;
	}

	public Manifest getManifest() {
		return this.manifest;
	}

	public Status getStatus() {
		return this.status;
	}

}
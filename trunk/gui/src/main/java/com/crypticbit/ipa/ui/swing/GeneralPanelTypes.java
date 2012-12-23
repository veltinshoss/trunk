package com.crypticbit.ipa.ui.swing;

import com.crypticbit.ipa.central.ProgressIndicator;
import com.crypticbit.ipa.ui.swing.concept.ConceptView;

enum GeneralPanelTypes {
    MEDIA("All media", true) {

	@Override
	public ViewingPane create(Mediator mediator) {
	    return new MediaViewingPane(mediator);

	}
    },
    CONCEPT("Concepts", true) {

	@Override
	public ViewingPane create(Mediator mediator) {
	    return new ConceptView(mediator);
	}

	@Override
	public boolean isReady(Mediator mediator) {
	    return mediator.getBackupDirectory().isEventsLoaded();
	}

	public void load(Mediator mediator, ProgressIndicator progressIndicator) {
	    mediator.getBackupDirectory().getAllEvents(progressIndicator);
	}

    };
    public abstract ViewingPane create(Mediator mediator);

    private String description;
    private boolean showOntask;

    GeneralPanelTypes(String description, boolean showOnTask) {
	this.description = description;
	this.showOntask = showOnTask;
    }

    public String getDescription() {
	return description;
    }

    public boolean isShowOntask() {
	return showOntask;
    }

    public boolean isReady(Mediator mediator) {
	return true;
    }

    public void load(Mediator mediator, ProgressIndicator progressIndicator) {
    }
}
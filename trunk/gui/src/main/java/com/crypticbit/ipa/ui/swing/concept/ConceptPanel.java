package com.crypticbit.ipa.ui.swing.concept;

public interface ConceptPanel {

	void fireFilterChange(Filter filter);

	void fireHighlightChange();

	void fireSelectChange();

	void registerToUpdateOnSelectionChange();

	void registerToUpddateOnMouseOverChange();

}

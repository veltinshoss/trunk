package com.crypticbit.ipa.ui.swing.concept;

import com.crypticbit.ipa.entity.concept.Event;

public interface Filter {

	boolean accept(Event e);

}

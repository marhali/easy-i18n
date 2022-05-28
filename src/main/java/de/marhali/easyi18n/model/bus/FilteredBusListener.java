package de.marhali.easyi18n.model.bus;

import de.marhali.easyi18n.FilteredDataBus;

/**
 * Interface to replicate the state of {@link FilteredDataBus} to underlying components.
 * @author marhali
 */
public interface FilteredBusListener extends UpdateDataListener, FocusKeyListener, ExpandAllListener {}

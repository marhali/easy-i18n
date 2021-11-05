package de.marhali.easyi18n.model.bus;

/**
 * Interface for communication of changes for participants of the data bus.
 * Every listener needs to be registered manually via {@link de.marhali.easyi18n.DataBus}.
 * @author marhali
 */
public interface BusListener extends UpdateDataListener, FocusKeyListener, SearchQueryListener {}
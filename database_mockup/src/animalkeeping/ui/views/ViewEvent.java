package animalkeeping.ui.views;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by grewe on 4/24/17.
 */
public class ViewEvent extends Event {
    private static final long serialVersionUID = 201721107L;
    public static final EventType<ViewEvent> VIEW_ALL = new EventType<>("VIEW all");
    public static final EventType<ViewEvent> REFRESHING = new EventType<>(VIEW_ALL, "refreshing data");
    public static final EventType<ViewEvent> REFRESHED = new EventType<>(VIEW_ALL, "refreshed");
    public static final EventType<ViewEvent> REFRESH_FAIL = new EventType<>(VIEW_ALL, "failed");

    public ViewEvent(EventType<ViewEvent> type) {
        super(type);
    }

    @Override
    public ViewEvent copyFor(Object newSource, EventTarget newTarget) {
        return (ViewEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends ViewEvent> getEventType() {
        return (EventType<? extends ViewEvent>) super.getEventType();
    }
}

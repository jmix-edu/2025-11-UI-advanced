package com.company.timesheets.view.mytimeentrylist;

import com.company.timesheets.app.TimeEntrySupport;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.view.main.MainView;
import com.company.timesheets.view.timeentry.TimeEntryDetailView;
import com.vaadin.flow.component.grid.editor.EditorCloseEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.MetadataTools;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.propertyfilter.PropertyFilter;
import io.jmix.flowui.facet.Timer;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "my-time-entries", layout = MainView.class)
@ViewController(id = "ts_TimeEntry.my")
@ViewDescriptor(path = "my-time-entry-list-view.xml")
public class MyTimeEntryListView extends StandardView {

    @ViewComponent
    private DataGrid<TimeEntry> timeEntriesDataGrid;

    @ViewComponent
    private CollectionContainer<TimeEntry> timeEntriesDc;

    @ViewComponent
    private CollectionLoader<TimeEntry> timeEntriesDl;

    @ViewComponent
    private PropertyFilter<?> taskFilter;

    @ViewComponent
    private PropertyFilter<?> statusFilter;

    @Autowired
    private TimeEntrySupport timeEntrySupport;

    @Autowired
    private DialogWindows dialogWindows;

    @Autowired
    private Notifications notifications;

    @ViewComponent
    private Timer timer;

    @Autowired
    private MetadataTools metadataTools;

    @Autowired
    private DataManager dataManager;

    @Install(to = "timeEntriesDataGrid.create", subject = "queryParametersProvider")
    private QueryParameters timeEntriesDataGridCreateQueryParametersProvider() {
        return QueryParameters.of(TimeEntryDetailView.PARAM_OWN_TIME_ENTRY, "");
    }

    @Install(to = "timeEntriesDataGrid.edit", subject = "queryParametersProvider")
    private QueryParameters timeEntriesDataGridEditQueryParametersProvider() {
        return QueryParameters.of(TimeEntryDetailView.PARAM_OWN_TIME_ENTRY, "");
    }

    @Supply(to = "timeEntriesDataGrid.status", subject = "renderer")
    private Renderer<TimeEntry> timeEntriesDataGridStatusRenderer() {
        return new ComponentRenderer<>(Span::new, (span, timeEntry) -> {
            String theme = switch (timeEntry.getStatus()) {
                case NEW -> "";
                case APPROVED -> "success";
                case REJECTED -> "error";
                case CLOSED -> "contrast";
            };

            span.getElement().setAttribute("theme", "badge " + theme);
            span.setText(metadataTools.format(timeEntry.getStatus()));
        });
    }

    @Install(to = "timeEntriesDataGrid.@editor", subject = "closeListener")
    private void timeEntriesDataGridEditorCloseListener(final EditorCloseEvent<TimeEntry> event) {
        TimeEntry timeEntry = event.getItem();
        TimeEntry savedTimeEntry = dataManager.save(timeEntry);
        timeEntriesDc.replaceItem(savedTimeEntry);
    }

    @Subscribe("clearFiltersBtn")
    public void onClearFiltersBtnClick(ClickEvent<Button> event) {
        taskFilter.setValue(null);
        statusFilter.setValue(null);
        timeEntriesDl.load();
    }



//    int seconds = 0;
//
//    @Subscribe("timer")
//    public void onTimerTimerAction(final Timer.TimerActionEvent event) {
//        seconds += event.getSource().getDelay() / 1000;
//        notifications.show("Timer tick", seconds + " seconds passed");
//    }
//
//    @Subscribe("timer")
//    public void onTimerTimerStop(final Timer.TimerStopEvent event) {
//        notifications.show("Timer stopped");
//    }
}
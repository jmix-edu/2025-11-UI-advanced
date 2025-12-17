package com.company.timesheets.view.timeentry;

import com.company.timesheets.app.TaskSupport;
import com.company.timesheets.entity.Task;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.entity.TimeEntryStatus;
import com.company.timesheets.entity.User;
import com.company.timesheets.view.main.MainView;
import com.company.timesheets.view.task.TaskLookupView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.datetimepicker.TypedDateTimePicker;
import io.jmix.flowui.component.textarea.JmixTextArea;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

@Route(value = "time-entries/:id", layout = MainView.class)
@ViewController("ts_TimeEntry.detail")
@ViewDescriptor("time-entry-detail-view.xml")
@EditedEntityContainer("timeEntryDc")
@DialogMode(width = "30em")
public class TimeEntryDetailView extends StandardDetailView<TimeEntry> {
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private JmixTextArea rejectionReasonField;

    @ViewComponent
    private EntityComboBox<Task> tasksComboBox;
    @Autowired
    private DialogWindows dialogWindows;
    @ViewComponent
    private EntityComboBox<User> userField;

    public static final String PARAM_OWN_TIME_ENTRY = "ownTimeEntry";

    private boolean ownTimeEntry = false;
    @ViewComponent
    private TypedDateTimePicker<OffsetDateTime> lastModifiedDateField;
    @Autowired
    private TaskSupport taskSupport;

    public void setOwnTimeEntry(boolean ownTimeEntry) {
        this.ownTimeEntry = ownTimeEntry;
    }

    @Subscribe
    public void onQueryParametersChange(final QueryParametersChangeEvent event) {
        ownTimeEntry = event.getQueryParameters()
                .getSingleParameter(PARAM_OWN_TIME_ENTRY)
                .isPresent();
    }


    @Subscribe("lastModifiedDateField")
    public void onLastModifiedDateFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<TypedDateTimePicker<OffsetDateTime>, OffsetDateTime> event) {
    }

    @Subscribe(id = "timeEntryDc", target = Target.DATA_CONTAINER)
    public void onTimeEntryDcItemChange(final InstanceContainer.ItemChangeEvent<TimeEntry> event) {
        updateRejectionReasonField();
    }

    @Subscribe(id = "timeEntryDc", target = Target.DATA_CONTAINER)
    public void onTimeEntryDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<TimeEntry> event) {
        if ("status".equals(event.getProperty())) {
            updateRejectionReasonField();
        }

        if ("user".equals(event.getProperty())) {
            tasksComboBox.setReadOnly(getEditedEntity().getUser() == null);
            tasksComboBox.getDataProvider().refreshAll();
        }

        if ("task".equals(event.getProperty())) {
            userField.setReadOnly(getEditedEntity().getTask() != null);
        }

    }

    private void updateRejectionReasonField() {
        rejectionReasonField.setVisible(TimeEntryStatus.REJECTED == getEditedEntity().getStatus());
    }

    @Subscribe("userField.assignSelf")
    public void onUserFieldAssignSelf(final ActionPerformedEvent event) {
        final User user = (User) currentAuthentication.getUser();
        getEditedEntity().setUser(user);
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<TimeEntry> event) {
        TimeEntry timeEntry = event.getEntity();

        if (timeEntry.getDate() == null) {
            timeEntry.setDate(LocalDate.now());
        }

        if (timeEntry.getUser() == null) {
            if (ownTimeEntry) {
                final User user = (User) currentAuthentication.getUser();
                timeEntry.setUser(user);
            } else {
                userField.setReadOnly(false);
                tasksComboBox.setReadOnly(true);
            }
        } else {
            tasksComboBox.setReadOnly(timeEntry.getTask() != null);
        }
    }


    @Subscribe("tasksComboBox.entityLookup")
    public void onTasksComboBoxEntityLookup(final ActionPerformedEvent event) {
        DialogWindow<TaskLookupView> window = dialogWindows.lookup(tasksComboBox)
                .withViewClass(TaskLookupView.class)
                .build();

        window.getView().setUser(getEditedEntity().getUser());
        window.open();
    }

    @Install(to = "tasksComboBox", subject = "itemsFetchCallback")
    private Stream<Task> tasksComboBoxItemsFetchCallback(final Query<Task, String> query) {
        User user = getEditedEntity().getUser();
        String filter = query.getFilter().orElse(null);

        return user != null
                ? taskSupport.getUserActiveTasks(user, query.getOffset(), query.getLimit(), filter)
                : taskSupport.getActiveTasks(query.getOffset(), query.getLimit(), filter);

    }

}
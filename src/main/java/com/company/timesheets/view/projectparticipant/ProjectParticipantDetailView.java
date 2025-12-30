package com.company.timesheets.view.projectparticipant;

import com.company.timesheets.entity.Project;
import com.company.timesheets.entity.ProjectParticipant;
import com.company.timesheets.entity.ProjectRole;
import com.company.timesheets.view.tabbedmain.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import io.jmix.tabbedmode.ViewBuilders;
import io.jmix.tabbedmode.view.ViewOpenMode;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "project-participants/:id", layout = MainView.class)
@ViewController("ts_ProjectParticipant.detail")
@ViewDescriptor("project-participant-detail-view.xml")
@EditedEntityContainer("projectParticipantDc")
@DialogMode(width = "35em")
public class ProjectParticipantDetailView extends StandardDetailView<ProjectParticipant> {

    @ViewComponent
    private EntityPicker<Project> projectField;
    @ViewComponent
    private EntityComboBox<ProjectRole> roleField;
    @Autowired
    private ViewBuilders viewBuilders;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        // in case we open a view for an entity creation by direct navigation
        projectField.setVisible(getEditedEntity().getProject() == null);
    }

    @Subscribe("roleField.entityCreate")
    public void onRoleFieldEntityCreate(final ActionPerformedEvent event) {
        viewBuilders.detail(roleField)
                .newEntity()
                .withOpenMode(ViewOpenMode.DIALOG)
                .open();
    }
}
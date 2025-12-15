package com.company.timesheets.view.resourcerolemodellist;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.DefaultMainViewParent;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import io.jmix.securityflowui.view.resourcerole.ResourceRoleModelListView;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "sec/resourcerolemodels", layout = DefaultMainViewParent.class)
@ViewController(id = "sec_ResourceRoleModel.list")
@ViewDescriptor(path = "ts-resource-role-model-list-view.xml")
public class TsResourceRoleModelListView extends ResourceRoleModelListView {
    @Autowired
    private Notifications notifications;

    @Subscribe(id = "customButton", subject = "clickListener")
    public void onCustomButtonClick(final ClickEvent<JmixButton> event) {
        notifications.show("I am clicked!!!");
    }
}
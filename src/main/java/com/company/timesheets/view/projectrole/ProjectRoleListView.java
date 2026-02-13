package com.company.timesheets.view.projectrole;

import com.company.timesheets.entity.ProjectRole;
import com.company.timesheets.entity.ProjectRoleType;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.Messages;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.Supply;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "project-roles", layout = MainView.class)
@ViewController("ts_ProjectRole.list")
@ViewDescriptor("project-role-list-view.xml")
@LookupComponent("projectRolesDataGrid")
@DialogMode(width = "64em")
public class ProjectRoleListView extends StandardListView<ProjectRole> {

    @Autowired
    private Messages messages;

    @Supply(to = "projectRolesDataGrid.type", subject = "renderer")
    private Renderer<ProjectRole> projectRolesDataGridTypeRenderer() {
        return new ComponentRenderer<>(
                this::createTypeSpan,
                this::updateTypeSpan
        );
    }

    private Span createTypeSpan() {
        Span span = new Span();
        // базовый стиль бейджа
        span.getElement().getThemeList().add("badge");
        return span;
    }

    private void updateTypeSpan(Span span, ProjectRole role) {
        ProjectRoleType type = role.getType();

        // очистим прошлые стили, кроме базового "badge"
        var themeList = span.getElement().getThemeList();
        themeList.clear();
        themeList.add("badge");

        if (type != null) {
            // локализованный текст enum (ключи уже есть в messages_en.properties)
            span.setText(messages.getMessage(ProjectRoleType.class, type.name()));

            // цвет по типу роли
            switch (type) {
                case MANAGER -> themeList.add("primary");
                case APPROVER -> themeList.add("success");
                case MEMBER -> {
                    // обычный бейдж без доп. варианта
                }
                case OBSERVER -> themeList.add("contrast");
            }
        } else {
            span.setText("—");
            themeList.add("contrast");
        }
    }
}
package com.company.timesheets.view.tasktype;

import com.company.timesheets.entity.TaskType;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "taskTypes-simple", layout = MainView.class)
@ViewController(id = "ts_TaskType_simple.list")
@ViewDescriptor(path = "task-type-simple-list-view.xml")
@LookupComponent("taskTypesDataGrid")
@DialogMode(width = "64em")
public class TaskTypeSimpleListView extends StandardListView<TaskType> {
}
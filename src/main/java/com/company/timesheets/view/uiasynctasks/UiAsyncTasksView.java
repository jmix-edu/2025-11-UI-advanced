package com.company.timesheets.view.uiasynctasks;


import com.company.timesheets.component.ColorPicker;
import com.company.timesheets.component.composite.ColorComponent;
import com.company.timesheets.component.slider.Slider;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.app.propertyfilter.dateinterval.model.DateInterval;
import io.jmix.flowui.asynctask.UiAsyncTasks;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;

@Route(value = "ui-async-tasks-view", layout = MainView.class)
@ViewController(id = "ts_UiAsyncTasksView")
@ViewDescriptor(path = "ui-async-tasks-view.xml")
public class UiAsyncTasksView extends StandardView {

    @Autowired
    private UiAsyncTasks uiAsyncTasks;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private TypedTextField<Object> inputField;

    @Subscribe(id = "performWithoutResultBtn", subject = "clickListener")
    public void onPerformWithoutResultBtnClick(final ClickEvent<JmixButton> event) {
        uiAsyncTasks.runnableConfigurer(this::voidMethod)
                .withResultHandler(() -> {
                    notifications.show("Action performed w/o result");
                })
                .runAsync();
    }

    private void voidMethod() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }
    }

    @Subscribe(id = "performChangesBtn", subject = "clickListener")
    public void onPerformChangesBtnClick(final ClickEvent<JmixButton> event) {
        String typed =  inputField.getValue();

        uiAsyncTasks.supplierConfigurer(() -> changeString(typed))
                .withResultHandler(resultString -> {
                    notifications.show(resultString);
                })
                .withTimeout(3, TimeUnit.SECONDS)
                .withExceptionHandler(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        notifications.create("Timeout exception!")
                                .withType(Notifications.Type.WARNING)
                                .show();
                    } else {
                        notifications.create("Unknown error: " + throwable.getMessage() +"!")
                                .withType(Notifications.Type.WARNING)
                                .show();
                    }
                })
                .supplyAsync();
    }

    private String changeString(String typed) {
        try {
            sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }

        return (typed + "_changed").toUpperCase();
    }

    @Subscribe
    public void onInit(final InitEvent event) {
        ColorPicker colorPicker = new ColorPicker();
        getContent().add(colorPicker);
        colorPicker.addValueChangeListener(e ->
                notifications.show("Color: " + e.getValue()));

        ColorComponent colorComponent = new ColorComponent();
        getContent().add(colorComponent);
    }

    @Subscribe("slider")
    public void onSliderChange(Slider.SlideChangedEvent event) {
        notifications.show("New value is: " + event.getValue());
    }
    
}
package com.company.timesheets.view.uiasynctasks;

import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.asynctask.UiAsyncTasks;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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

    // ВАЖНО: обычный Vaadin VerticalLayout
    @ViewComponent
    private VerticalLayout card1;
    @ViewComponent
    private VerticalLayout card2;
    @ViewComponent
    private VerticalLayout card3;
    @ViewComponent
    private VerticalLayout card4;

    // текущая перетаскиваемая карточка
    private Component draggedCard;

    @Subscribe(id = "performWithoutResultBtn", subject = "clickListener")
    public void onPerformWithoutResultBtnClick(ClickEvent<JmixButton> event) {
        uiAsyncTasks.runnableConfigurer(this::voidMethod)
                .withResultHandler(() ->
                        notifications.show("Action performed w/o result"))
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
    public void onPerformChangesBtnClick(ClickEvent<JmixButton> event) {
        String typed = inputField.getValue();

        uiAsyncTasks.supplierConfigurer(() -> changeString(typed))
                .withResultHandler(resultString ->
                        notifications.show(resultString))
                .withTimeout(3, TimeUnit.SECONDS)
                .withExceptionHandler(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        notifications.create("Timeout exception!")
                                .withType(Notifications.Type.WARNING)
                                .show();
                    } else {
                        notifications.create("Unknown error: " + throwable.getMessage() + "!")
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
    public void onReady(ReadyEvent event) {
        initDragAndDrop(card1);
        initDragAndDrop(card2);
        initDragAndDrop(card3);
        initDragAndDrop(card4);
    }

    private void initDragAndDrop(Component card) {
        // источник
        DragSource<Component> dragSource = DragSource.create(card);
        dragSource.setDraggable(true);

        dragSource.addDragStartListener(e -> {
            draggedCard = card;
            card.getElement().getStyle().set("opacity", "0.7");
            card.getElement().getStyle().set("cursor", "grabbing");
        });
        dragSource.addDragEndListener(e -> {
            draggedCard = null;
            card.getElement().getStyle().remove("opacity");
            card.getElement().getStyle().remove("cursor");
        });

        // цель
        DropTarget<Component> dropTarget = DropTarget.create(card);
        dropTarget.setDropEffect(DropEffect.MOVE);

        dropTarget.addDropListener(e -> {
            if (draggedCard == null || draggedCard == card) {
                return;
            }
            swapCards(draggedCard, card);
        });
    }

    private void swapCards(Component c1, Component c2) {
        if (c1.getParent().isEmpty() || c2.getParent().isEmpty()) {
            return;
        }
        if (c1.getParent().get() != c2.getParent().get()) {
            // у нас карточки все лежат в одном gridLayout, так что сюда не попадём
            return;
        }

        HasComponents parent = (HasComponents) c1.getParent().get();

        List<Component> children = ((Component) parent)
                .getChildren()
                .collect(Collectors.toList());

        int i1 = children.indexOf(c1);
        int i2 = children.indexOf(c2);
        if (i1 < 0 || i2 < 0 || i1 == i2) {
            return;
        }

        // меняем местами
        children.set(i1, c2);
        children.set(i2, c1);

        // пересобираем
        parent.removeAll();
        children.forEach(parent::add);
    }
}
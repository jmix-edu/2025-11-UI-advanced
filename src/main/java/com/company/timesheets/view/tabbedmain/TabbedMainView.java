package com.company.timesheets.view.tabbedmain;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import io.jmix.tabbedmode.app.main.StandardTabbedModeMainView;

/*
 * To use the view as a main view don't forget to set
 * new value (see @ViewController) to 'jmix.ui.main-view-id' property.
 */
@Route(value = "tabbed-main-view")
@ViewController(id = "ts_TabbedMainView")
@ViewDescriptor(path = "tabbed-main-view.xml")
public class TabbedMainView extends StandardTabbedModeMainView {
}
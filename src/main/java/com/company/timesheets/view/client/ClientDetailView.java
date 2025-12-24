package com.company.timesheets.view.client;

import com.company.timesheets.entity.Client;
import com.company.timesheets.entity.ContactInformation;
import com.company.timesheets.view.contactinformationfragment.ContactInformationFragment;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Fragments;
import io.jmix.flowui.component.details.JmixDetails;
import io.jmix.flowui.component.image.JmixImage;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "clients/:id", layout = MainView.class)
@ViewController("ts_Client.detail")
@ViewDescriptor("client-detail-view.xml")
@EditedEntityContainer("clientDc")
public class ClientDetailView extends StandardDetailView<Client> {

    @ViewComponent
    private JmixImage<Object> imageDisplayField;
//    @ViewComponent
//    private InstanceContainer<ContactInformation> contactInformationDc;
//    @Autowired
//    private Fragments fragments;
//    @ViewComponent
//    private JmixDetails ciDetails;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        updateImage(getEditedEntity().getImage());
        applySecurityPermissions();
    }

    @Subscribe(id = "uploadClearButton", subject = "clickListener")
    public void onUploadClearButtonClick(final ClickEvent<JmixButton> event) {
        getEditedEntity().setImage(null);
    }

    @Subscribe(id = "clientDc", target = Target.DATA_CONTAINER)
    public void onClientDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<Client> event) {
        if ("image".equals(event.getProperty())) {
            updateImage(getEditedEntity().getImage());
        }
    }

    private void updateImage(byte[] imageBytes) {
        if (imageBytes == null) {
            imageDisplayField.setSrc("images/add-image-placeholder.png");
        }
    }

    private void applySecurityPermissions() {
    }

//    @Subscribe
//    public void onInit(final InitEvent event) {
//        ContactInformationFragment fragment = fragments.create(this, ContactInformationFragment.class);
//        fragment.setContactInformationValues(contactInformationDc);
//        ciDetails.add(fragment);
//    }
    
    




}
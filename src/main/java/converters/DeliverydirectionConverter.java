/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package converters;

import db.Deliverydirection;
import db.helpers.DeliveriesHelper;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Georgi
 */
@FacesConverter("deliverydirectionConverter")
//@ManagedBean(name = "deliverydirectionConverter")
public class DeliverydirectionConverter implements Converter {
 
    private DeliveriesHelper helper;
    
    public DeliverydirectionConverter() {
        super();
        this.helper = new DeliveriesHelper();
    }
 
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return this.helper.getDeliveryDirectionById(Integer.valueOf(value));
    }
 
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Deliverydirection) value).getId().toString();
    }
}
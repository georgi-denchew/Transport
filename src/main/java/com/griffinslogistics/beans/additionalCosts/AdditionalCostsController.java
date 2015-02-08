/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.beans.additionalCosts;

import com.griffinslogistics.db.entities.AdditionalCost;
import com.griffinslogistics.db.entities.Transportation;
import com.griffinslogistics.db.helpers.TransportDbHelper;
import com.griffinslogistics.models.TruckGroupTotalsModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class AdditionalCostsController implements Serializable {

    private final TransportDbHelper dbHelper;

    private Transportation transportation;
    private AdditionalCost newAdditionalCost;

    private List<AdditionalCost> additionalCosts;
    private Map<String, TruckGroupTotalsModel> truckGroupTotalsMap;
    private List<String> truckGroupTotalsKeys;
    private List<TruckGroupTotalsModel> truckGroupTotals;

    private int truckGroupsPrice;
    private int additionalCostsPrice;

    public AdditionalCostsController() {
        this.dbHelper = new TransportDbHelper();
        this.newAdditionalCost = new AdditionalCost();
        this.additionalCosts = new ArrayList<AdditionalCost>();
    }

    @PostConstruct
    public void init() {
        initTransportation();
        initAdditionalCosts();
        initTruckGroupTotals();
    }

    public void addAdditionalCost() {
        newAdditionalCost.setTransportation(transportation);

        boolean added = this.dbHelper.additionalCosts.updateAdditionalCost(newAdditionalCost);

        FacesMessage message;

        if (added) {
            this.additionalCosts.add(0, newAdditionalCost);
            newAdditionalCost = new AdditionalCost();

            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Разходът е добавен успешно!", "");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Разходът не е добавен.", "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage message = null;

        try {
            AdditionalCost edited = (AdditionalCost) event.getObject();
            boolean updated = this.dbHelper.additionalCosts.updateAdditionalCost(edited);
            if (updated) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Разходът е променен успешно!", "");
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Разходът не е променен.", "");
            }
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void onCancelEdit(RowEditEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Обновлението е отказано!", "");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private void initTransportation() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        if (this.transportation == null) {
            this.transportation = (Transportation) session.get("transportation");
        }
    }

    private void initAdditionalCosts() {
        this.additionalCosts = this.dbHelper.additionalCosts.getAdditionalCostsByTransportation(this.transportation.getId());

        for (AdditionalCost additionalCost : additionalCosts) {
            BigDecimal price = additionalCost.getPrice();
            this.additionalCostsPrice += additionalCost.getPrice().intValue();
        }
    }

    private void initTruckGroupTotals() {
        this.truckGroupTotalsMap = this.dbHelper.transportations.getTruckGroupTotalsForTransportation(transportation.getId());

        this.truckGroupTotalsKeys = new ArrayList<String>();
        this.truckGroupTotalsKeys.addAll(truckGroupTotalsMap.keySet());

        Collections.sort(this.truckGroupTotalsKeys);

        this.truckGroupTotals = new ArrayList<TruckGroupTotalsModel>();
        
        for (String string : this.truckGroupTotalsKeys) {
            TruckGroupTotalsModel model = this.truckGroupTotalsMap.get(string);
            this.truckGroupTotals.add(model);
            this.truckGroupsPrice += model.getTotalPrice().intValue();
        }
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public AdditionalCost getNewAdditionalCost() {
        return newAdditionalCost;
    }

    public void setNewAdditionalCost(AdditionalCost newAdditionalCost) {
        this.newAdditionalCost = newAdditionalCost;
    }

    public List<AdditionalCost> getAdditionalCosts() {
        return additionalCosts;
    }

    public void setAdditionalCosts(List<AdditionalCost> additionalCosts) {
        this.additionalCosts = additionalCosts;
    }

    public List<TruckGroupTotalsModel> getTruckGroupTotals() {
        return truckGroupTotals;
    }

    public void setTruckGroupTotals(List<TruckGroupTotalsModel> truckGroupTotals) {
        this.truckGroupTotals = truckGroupTotals;
    }

    public int getTruckGroupsPrice() {
        return truckGroupsPrice;
    }

    public void setTruckGroupsPrice(int truckGroupsPrice) {
        this.truckGroupsPrice = truckGroupsPrice;
    }

    public int getAdditionalCostsPrice() {
        return additionalCostsPrice;
    }

    public void setAdditionalCostsPrice(int additionalCostsPrice) {
        this.additionalCostsPrice = additionalCostsPrice;
    }
}

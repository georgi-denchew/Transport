/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


//$(document).ready(function() {;
//    var expanderIndex = sessionStorage.getItem("expanderIndex");
//
//    if (expanderIndex) {
//        var allExpanders = $('.ui-row-toggler');
//        var expander = allExpanders.get(expanderIndex);
//        expander.click();
//
//        sessionStorage.removeItem("expanderIndex");
//    }
//});

var expander;

$(document).on('click', '.addFile', function(e) {
    var clickedButton = $(e.target);
    expander = clickedButton.parent().parent().parent()
            .prev().find('.ui-row-toggler').first();
});

//$(document).on('click', '.ui-icon-circle-triangle-e', function (e){
//    e.preventDefault();
//    console.log('first test');
//    var clickedToggler = $(e.target);
//    var allClickedTogglers = $('.ui-icon-circle-triangle-s');
//    
//    
//});

initialTrigger = true;

var rowIndex;
var togglerIndex;
var ignoreNext;

function start() {
    var clickedTogglers = $('.ui-icon-circle-triangle-s');
    
    
    if (ignoreNext) {
        ignoreNext = false;
        return;
    }
    console.log('start');

    var length = clickedTogglers.length;
    var i;

    for (i = 0; i < length; i++) {
        var testIndex = Number($(clickedTogglers[i]).parent().parent().attr("data-ri"));
        if (testIndex !== rowIndex) {
            togglerIndex = i;
            break;
        }
    }

    if (typeof togglerIndex === "undefined") {
        togglerIndex = 0;
    }

    rowIndex = Number($(clickedTogglers[togglerIndex]).parent().parent().attr("data-ri"));
}

function success() {
    console.log('success');
    var allClickedTogglers = $('.ui-row-toggler.ui-icon-circle-triangle-s');

    var i;
    var length = allClickedTogglers.length;

    for (i = 0; i < length; i++) {
        var currentToggler = $(allClickedTogglers[i]);

        var parentRowIndex = Number(currentToggler.parent().parent().attr("data-ri"));

        if (parentRowIndex !== rowIndex) {
            currentToggler.parent().parent().next().hide();
            ignoreNext = true;
            currentToggler.click();
        }
    }
}

$(document).on('click', [".ui-icon-pencil", ".ui-icon-check", ".ui-icon-close"], function(e) {

    var editButton = $(e.target);
    var currentRow = editButton.closest(".ui-widget-content");
    var nextRow = currentRow.next();
    var isAttachmentsRow = nextRow.hasClass("ui-expanded-row-content");

    if (isAttachmentsRow) {
        var currentRowToggler = currentRow.find('.ui-row-toggler');
        currentRowToggler.click();
    }
});

function setExpander() {
    PF('deliveryDialog').hide(50);
    console.log('reloaded');
    var allExpanders = $('.ui-row-toggler');
    var expanderIndex = allExpanders.index(expander);
    sessionStorage.setItem("expanderIndex", expanderIndex);

    location.reload();
}

function reloadPage() {
    location.reload();
}

function removeInputFile() {
    $(".file").each(function() {
        this.remove();
    });
}

function disableInputFileElements() {
    $(".file").each(function() {

        this.disabled = true;
    });
}

function tryRemoveFirstRow() {
    var firstRow = $(".ui-datatable-data tr").first();
    var children = $(firstRow.children().slice(2));

    var length = children.length;
    var i = 0;
    var toDelete = true;


    for (i = 0; i < length; i++) {
        var child = $(children[i]);

        if (child.text()) {
            toDelete = false;
        }
    }

    if (toDelete) {
        firstRow.remove();
    }
}


function startEdit() {

    $(".ui-datatable-data tr").first().find(".ui-icon-pencil").each(function() {
        $(this).click();
    });
}
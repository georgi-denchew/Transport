/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



var expander;

$(document).on('click', '.addFile', function(e) {
    var clickedButton = $(e.target);
    expander = clickedButton.parent().parent().parent()
            .prev().find('.ui-row-toggler').first();
});


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

$(document).on('click', ".ui-icon-pencil", function(e) {
    setTimeout(function() {
        var editButton = $(e.target);
        var currentRow = editButton.closest(".ui-widget-content");

        var calendarSpans = currentRow.find("span.inputLabelExclamationMark");

        if (calendarSpans) {
            var calendarInputs = calendarSpans.find("input");
            calendarInputs.addClass("inputLabelExclamationMark");
        }
    }, 10);


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

$(document).on('keyup', function(event) {

    var editingRow = $(".ui-row-editing");

    if (editingRow.size() > 0) {
        // if key is 'ESCAPE'
        if (event.keyCode === 27) {
            event.preventDefault();
            event.stopPropagation();
            var closeButton = editingRow.find(".ui-icon-close");
            closeButton.click();
        }
        // ' when 'ENTER' is a pressed in a widget (e.g. Calendar or Dropdown Menu) the 'keypress' event doesn't work - below stands the workaround
        else if (event.keyCode === 13) {
            debugger;
            event.preventDefault();
            event.stopPropagation();

            var keyPressEvent = jQuery.Event('keypress');
            keyPressEvent.keyCode = 13;
            editingRow.trigger(keyPressEvent);
        }
    }
});

toUpdate = true;

$(document).on('keypress', '.ui-row-editing', function(event) {
    debugger;
    // if key is 'ENTER'

    // workaround for invoking the keypress event twice
    if (!toUpdate) {
        toUpdate = true;
        return;
    }
    if (event.keyCode === 13) {
        event.preventDefault();
        event.stopPropagation();
        var checkButton = $(this).find(".ui-icon-check");
        checkButton.click();
        toUpdate = false;
    }
});

function addCalendarsBackgroundImage() {
}

(function() {
    var pencils = $(".ui-icon-pencil");
    pencils.attr("title", "Промени");

    var saveChangesButtons = $(".ui-icon-check");
    saveChangesButtons.attr("title", "Запази промените");

    var saveChangesButtons = $(".ui-icon-close");
    saveChangesButtons.attr("title", "Откажи промените");

    $("th[role='columnheader']").removeClass("errorRow");

})();


//$('body').on('DOMNodeInserted', '.ui-datatable', function(e) {
//    debugger;
//    console.log(e);
//    $(e.target).hide();
//    $(e.target).fadeIn(500);
//});

//function removeLast(e){
//    debugger;
//    
//    $(".ui-datatable tr:last-of-type").fadeOut(500);
//}


function cancelChanges() {
    var confirmed;
    debugger;
    alertify.confirm("Message", function(e) {
         if (e) {
            debugger;

        } else {
            debugger;

        }
    });
//    
//    swal({
//        title: "Откажи промените?",
//        type: "warning",
//        showCancelButton: true, 
//        confirmButtonColor: "#DD6B55",
//        confirmButtonText: "OK",
//        closeOnConfirm: true 
//    },
//    
//    function (confirmed) {    
//        if (!confirmed) {
//            return false;
//        }
//    });
//    
//        debugger;

}
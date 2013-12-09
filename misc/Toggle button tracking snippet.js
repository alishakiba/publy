$('button').on('click', function(event) {
    var el = $(this);
    var track = true;
    var elEv = []; elEv.value = 0, elEv.non_i = false;

    if (el.hasClass('abstract-toggle')) {
        elEv.category = "toggle";
        elEv.action = "click-abstract";
        elEv.label = el.parent().attr('id');
    } else if (el.hasClass('bibtex-toggle')) {
        elEv.category = "toggle";
        elEv.action = "click-bibtex";
        elEv.label = el.parent().attr('id');
    } else {
        track = false;
    }
    
    if (track) {
        // Report the event to Google Analytics
        _gaq.push(['_trackEvent', elEv.category.toLowerCase(), elEv.action.toLowerCase(), elEv.label.toLowerCase(), elEv.value, elEv.non_i]);
    }
});
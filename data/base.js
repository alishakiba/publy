// Function that is called when a link to a specific paper is clicked
function showPaper(paperID) {
    // Expand the abstract if it is currently hidden, otherwise leave it expanded
    var bibitem = $(paperID);
    var abs = bibitem.children('.abstract-container');

    if (abs.length && abs.is(":hidden")) {
        abs.slideToggle(500);

        // Update the button text to reflect that the abstract expanded
        var button = bibitem.children('button.abstract-toggle');

        if (button.length && button.text() == "Abstract") {
            var width = button.width();
            button.text("Hide");
            button.width(width);
        }
    }
}

// Functions that run the first time the document loads
$(document).ready(function() {
    // Abstracts start hidden for JS-enabled users
    $('.abstract-container').hide();

    // Detection for links to specific articles
    // Run once, for page reloads
    var hash = window.location.hash;
    if (hash) {
        showPaper(hash);
    }

    // Register an event listener, to run every time the hash changes
    $(window).on('hashchange', function() {
        showPaper(document.location.hash);
    });

    // Add click-behaviour to toggle elements
    $('.abstract-toggle').click(function () {
        $(this).parent().children('.abstract-container').slideToggle(500);
    });
    $('button.abstract-toggle').click(function () {
        // Store the original width, so it doesn't jump
        var width = $(this).width();

        if ($(this).text() == "Abstract") {
            $(this).text("Hide");
        } else {
            $(this).text("Abstract");
        }

        // Reset the width to the original
        $(this).width(width);
    });

    $('.bibtex-toggle').click(function () {
        $(this).parent().children('.bibtex-container').slideToggle(500);
    });
    $('button.bibtex-toggle').click(function () {
        // Store the original width, so it doesn't jump
        var width = $(this).width();

        if ($(this).text() == "BibTeX") {
            $(this).text("Hide");
        } else {
            $(this).text("BibTeX");
        }

        // Reset the width to the original
        $(this).width(width);
    });
});
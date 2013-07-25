// Track external links and file downloads with Google Analytics
// Adapted from: http://www.blastam.com/blog/index.php/2013/03/how-to-track-downloads-in-google-analytics-v2/
$(document).ready(function() {
    // The file types whose downloads we want to track
    var filetypes = /\.(zip|rar|gz|tar|pdf|ps|doc.*|txt|xls.*|ppt.*|bib)$/i;
    
    // Figure out the base URL so we can redirect download links later
    var baseHref = '';
    if ($('base').attr('href') != undefined) {
        baseHref = $('base').attr('href');
    }

    // Add click behaviour to all links
    $('a').on('click', function(event) {
        var el = $(this);
        var href = (typeof(el.attr('href')) != 'undefined') ? el.attr('href') : "";
        
        if (!href.match(/^javascript:/i)) {
            var track = true;
            var elEv = []; elEv.value = 0, elEv.non_i = false;
            var isThisDomain = href.match(document.domain.split('.').reverse()[1] + '.' + document.domain.split('.').reverse()[0]);
            
            if (href.match(filetypes)) {
                // A download link
                var extension = (/[.]/.exec(href)) ? /[^.]+$/.exec(href) : undefined;
                
                elEv.category = "download";
                elEv.action = "click-" + extension[0];
                elEv.label = href.replace(/ /g,"-");
                elEv.loc = baseHref + href;
            } else if (href.match(/^https?\:/i) && !isThisDomain) {
                // An external link
                elEv.category = "external";
                elEv.action = "click";
                elEv.label = href.replace(/^https?\:\/\//i, '');
                elEv.non_i = true;
                elEv.loc = href;
            } else {
                track = false;
            }

            if (track) {
                // Report the event to Google Analytics
                _gaq.push(['_trackEvent', elEv.category.toLowerCase(), elEv.action.toLowerCase(), elEv.label.toLowerCase(), elEv.value, elEv.non_i]);
                
                // If this is a link that opens in the same window, we need to wait a little while for the event to register
                if (el.attr('target') == undefined || el.attr('target').toLowerCase() != '_blank') {
                    setTimeout(function() { location.href = elEv.loc; }, 400);
                    return false;
                }
            }
        }
    });
    
    // Add event tracking behaviour to all toggle buttons
    $('.abstract-toggle').on('click', function(event) {
        _gaq.push(['_trackEvent', 'toggle', 'click-abstract', $(this).parent().attr('id').toLowerCase(), 0, false]);
    });
    $('.bibtex-toggle').on('click', function(event) {
        _gaq.push(['_trackEvent', 'toggle', 'click-bibtex', $(this).parent().attr('id').toLowerCase(), 0, false]);
    });
});

// Google analytics code
var _gaq = _gaq || [];
var pluginUrl = '//www.google-analytics.com/plugins/ga/inpage_linkid.js';
_gaq.push(['_require', 'inpage_linkid', pluginUrl]);
_gaq.push(['_setAccount', '~GAUSERACCOUNT~']);
_gaq.push(['_trackPageview']);

(function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();
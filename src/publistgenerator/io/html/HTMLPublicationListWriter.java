/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package publistgenerator.io.html;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import publistgenerator.data.bibitem.BibItem;
import publistgenerator.data.category.OutputCategory;
import publistgenerator.data.settings.HTMLSettings;
import publistgenerator.io.PublicationListWriter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class HTMLPublicationListWriter extends PublicationListWriter {

    private HTMLBibItemWriter itemWriter;
    private HTMLSettings settings;
    private int globalCount;

    public HTMLPublicationListWriter(HTMLSettings settings) {
        super(settings);
        this.settings = settings;
    }

    @Override
    protected void writePublicationList(BufferedWriter out) throws IOException {
        itemWriter = new HTMLBibItemWriter(out, settings);
        globalCount = 0;

        if (settings.getHeader() == null) {
            writeDefaultHeader(out);
        } else {
            // Copy the header from the header file
            copyFile(settings.getHeader(), out);
        }

        // Write the body
        out.write("    <p>My publications as of " + (new SimpleDateFormat("d MMMM yyyy")).format(new Date()) + ".");

        if (settings.linkToTextVersion()) {
            out.write(" Also available as <a href=\"");
            out.write(settings.getSettings().getPlainSettings().getTarget().getPath());
            out.write("\" rel=\"alternate\">plain text</a>.");
        }

        out.write("</p>");
        out.newLine();

        for (OutputCategory c : categories) {
            writeCategory(c, out);
        }

        if (settings.getFooter() == null) {
            writeDefaultFooter(out);
        } else {
            // Copy the footer from the footer file
            copyFile(settings.getFooter(), out);
        }
    }

    private void copyFile(File inputFile, BufferedWriter out) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                int index = line.indexOf("</head>");

                if (index > -1) {
                    // Splice in required Javascript
                    out.write(line.substring(0, index));
                    out.newLine();

                    writeJavascript(out);

                    out.write(line.substring(index));
                    out.newLine();
                } else {
                    out.write(line);
                    out.newLine();
                }
            }
        }
    }

    private void writeCategory(OutputCategory c, BufferedWriter out) throws IOException {
        int localCount = 0;

        out.write("    <div class=\"section\"><h1 class=\"sectiontitle\"><a id=\"" + c.getShortName().toLowerCase() + "\">" + c.getName() + "</a></h1>");
        out.newLine();
        out.newLine();
        writeNavigation(c, out);

        String note = settings.getCategoryNotes().get(c.getId());

        if (note != null && !note.isEmpty()) {
            out.write("      <p class=\"indent\">");
            out.write(note);
            out.write("</p>");
            out.newLine();
            out.newLine();
        }

        for (BibItem item : c.getItems()) {
            globalCount++;
            localCount++;

            out.write("      <div class=\"bibentry\">");
            out.newLine();

            switch (settings.getNumbering()) {
                case NONE:
                    itemWriter.write(item, -1);
                    break;
                case LOCAL:
                    itemWriter.write(item, localCount);
                    break;
                case GLOBAL:
                    itemWriter.write(item, globalCount);
                    break;
            }

            out.write("      </div>");
            out.newLine();
            out.newLine();
        }

        out.write("    </div>");
        out.newLine();
        out.newLine();
    }

    private void writeNavigation(OutputCategory current, BufferedWriter out) throws IOException {
        out.write("      <div class=\"pubnav\">");
        out.newLine();

        for (int i = 0; i < categories.size(); i++) {
            OutputCategory c = categories.get(i);

            out.write("        <a href=\"#");
            out.write(c.getShortName().toLowerCase());
            out.write("\" class=\"");

            if (c == current) {
                out.write("navcurrent\"");
            } else {
                out.write("nav\"");
            }

            out.write(" id=\"");
            out.write(current.getShortName());
            out.write("To");
            out.write(c.getShortName());
            out.write("\">");

            out.write(c.getShortName());
            out.write("</a>");

            if (i < categories.size() - 1) {
                out.write(" -");
            }

            out.newLine();
        }

        out.write("      </div>");
        out.newLine();
        out.newLine();
    }

    private void writeJavascript(BufferedWriter out) throws IOException {
        // Functional stylesheet
        out.write("    <!-- Functional stylesheet -->");
        out.newLine();
        out.write("    <link rel=\"stylesheet\" href=\"interactive.css\" type=\"text/css\">");
        out.newLine();
        out.newLine();

        // Hide interactive stuff
        out.write("    <!-- Hide interactive elements for users who have JavaScript disabled -->");
        out.newLine();
        out.write("    <noscript>");
        out.newLine();
        out.write("     <style type=\"text/css\">");
        out.newLine();
        out.write("      .interactive { display:none; }");
        out.newLine();
        out.write("     </style>");
        out.newLine();
        out.write("    </noscript>");
        out.newLine();
        out.newLine();

        // jQuery
        out.write("    <!-- jQuery framework -->");
        out.newLine();
        out.write("    <script src=\"jquery-1.9.1.min.js\" type=\"text/javascript\"></script>");
        out.newLine();
        out.newLine();

        // Article linking
        out.write("    <!-- Detection for links to specific articles -->");
        out.newLine();
        out.write("    <script type=\"text/javascript\">");
        out.newLine();
        out.write("     $(document).ready(function() {");
        out.newLine();
        out.write("      // Run once, for page reloads");
        out.newLine();
        out.write("      var hash = window.location.hash;");
        out.newLine();
        out.write("      if (hash) {");
        out.newLine();
        out.write("       showOnly(hash.substring(1) + \"_abstract\");");
        out.newLine();
        out.write("      }");
        out.newLine();
        out.write("     ");
        out.newLine();
        out.write("      // Run every time the hash changes");
        out.newLine();
        out.write("      $(window).on('hashchange', function() {");
        out.newLine();
        out.write("       showOnly(document.location.hash.substring(1) + \"_abstract\");");
        out.newLine();
        out.write("      });");
        out.newLine();
        out.write("     });");
        out.newLine();
        out.write("    </script>");
        out.newLine();
        out.newLine();

        // Toggle code
        out.write("    <!-- Function for toggling visibility of abstracts and BibTeX etc. -->");
        out.newLine();
        out.write("    <script type=\"text/javascript\">");
        out.newLine();
        out.write("     function toggle(divID) {");
        out.newLine();
        out.write("      $(\"#\" + divID + \"_plus\").toggle();");
        out.newLine();
        out.write("      $(\"#\" + divID + \"_minus\").toggle();");
        out.newLine();
        out.write("      $(\"#\" + divID).slideToggle(500);");
        out.newLine();
        out.write("     }");
        out.newLine();
        out.write("     ");
        out.newLine();
        out.write("     function show(divID) {");
        out.newLine();
        out.write("      $(\"#\" + divID + \"_plus\").hide();");
        out.newLine();
        out.write("      $(\"#\" + divID + \"_minus\").show();");
        out.newLine();
        out.write("      $(\"#\" + divID).show();");
        out.newLine();
        out.write("     }");
        out.newLine();
        out.write("     ");
        out.newLine();
        out.write("     function hide(divID) {");
        out.newLine();
        out.write("      $(\"#\" + divID + \"_plus\").show();");
        out.newLine();
        out.write("      $(\"#\" + divID + \"_minus\").hide();");
        out.newLine();
        out.write("      $(\"#\" + divID).hide();");
        out.newLine();
        out.write("     }");
        out.newLine();
        out.write("     ");
        out.newLine();
        out.write("     function showOnly(divID) {");
        out.newLine();
        out.write("      $(\".collapsible\").hide();");
        out.newLine();
        out.write("      $(\".hidden\").hide();");
        out.newLine();
        out.write("      $(\".shown\").show();");
        out.newLine();
        out.write("      ");
        out.newLine();
        out.write("      toggle(divID);");
        out.newLine();
        out.write("     }");
        out.newLine();
        out.write("    </script>");
        out.newLine();
        out.newLine();

        // MathJax
        out.write("    <!-- Include MathJax to convert LaTeX formulas to HTML on the fly -->");
        out.newLine();
        out.write("    <script type=\"text/x-mathjax-config\">");
        out.newLine();
        out.write("     MathJax.Hub.Config({");
        out.newLine();
        out.write("      tex2jax: {");
        out.newLine();
        out.write("       inlineMath: [['$','$'], ['\\(','\\)']],");
        out.newLine();
        out.write("       processEscapes: true");
        out.newLine();
        out.write("      }");
        out.newLine();
        out.write("     });");
        out.newLine();
        out.write("    </script>");
        out.newLine();
        out.write("    <script type=\"text/javascript\" src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script>");
        out.newLine();
        out.newLine();

        // Last: Google Analytics code
        if (settings.getGoogleAnalyticsUser() != null) {
            // Download tracking
            out.write("    <!-- Track external links and file downloads with Google Analytics -->");
            out.newLine();
            out.write("    <script type=\"text/javascript\">");
            out.newLine();
            out.write("    if (typeof jQuery != 'undefined') {");
            out.newLine();
            out.write("      jQuery(document).ready(function($) {");
            out.newLine();
            out.write("        var filetypes = /\\.(zip|jar|pdf|ppt*)$/i;");
            out.newLine();
            out.write("        var baseHref = '';");
            out.newLine();
            out.write("        if (jQuery('base').attr('href') != undefined)");
            out.newLine();
            out.write("          baseHref = jQuery('base').attr('href');");
            out.newLine();
            out.write("        jQuery('a').each(function() {");
            out.newLine();
            out.write("          var href = jQuery(this).attr('href');");
            out.newLine();
            out.write("          if (href && (href.match(/^https?\\:/i)) && (!href.match(document.domain))) {");
            out.newLine();
            out.write("            jQuery(this).click(function() {");
            out.newLine();
            out.write("              var extLink = href.replace(/^https?\\:\\/\\//i, '');");
            out.newLine();
            out.write("              _gaq.push(['_trackEvent', 'External', 'Click', extLink]);");
            out.newLine();
            out.write("              if (jQuery(this).attr('target') != undefined && jQuery(this).attr('target').toLowerCase() != '_blank') {");
            out.newLine();
            out.write("                setTimeout(function() { location.href = href; }, 200);");
            out.newLine();
            out.write("                return false;");
            out.newLine();
            out.write("              }");
            out.newLine();
            out.write("            });");
            out.newLine();
            out.write("          }");
            out.newLine();
            out.write("          else if (href && href.match(filetypes)) {");
            out.newLine();
            out.write("            jQuery(this).click(function() {");
            out.newLine();
            out.write("              var extension = (/[.]/.exec(href)) ? /[^.]+$/.exec(href) : undefined;");
            out.newLine();
            out.write("              var filePath = href;");
            out.newLine();
            out.write("              _gaq.push(['_trackEvent', 'Download', 'Click-' + extension, filePath]);");
            out.newLine();
            out.write("              if (jQuery(this).attr('target') != undefined && jQuery(this).attr('target').toLowerCase() != '_blank') {");
            out.newLine();
            out.write("                setTimeout(function() { location.href = baseHref + href; }, 200);");
            out.newLine();
            out.write("                return false;");
            out.newLine();
            out.write("              }");
            out.newLine();
            out.write("            });");
            out.newLine();
            out.write("          }");
            out.newLine();
            out.write("        });");
            out.newLine();
            out.write("      });");
            out.newLine();
            out.write("    }");
            out.newLine();
            out.write("    </script>");
            out.newLine();
            out.newLine();

            // Google Analytics
            out.write("    <!-- Google analytics code -->");
            out.newLine();
            out.write("    <script type=\"text/javascript\">");
            out.newLine();
            out.write("      var _gaq = _gaq || [];");
            out.newLine();
            out.write("      var pluginUrl = '//www.google-analytics.com/plugins/ga/inpage_linkid.js';");
            out.newLine();
            out.write("      _gaq.push(['_require', 'inpage_linkid', pluginUrl]);");
            out.newLine();
            out.write("      _gaq.push(['_setAccount', '" + settings.getGoogleAnalyticsUser() + "']);");
            out.newLine();
            out.write("      _gaq.push(['_trackPageview']);");
            out.newLine();
            out.write("    ");
            out.newLine();
            out.write("      (function() {");
            out.newLine();
            out.write("        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;");
            out.newLine();
            out.write("        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';");
            out.newLine();
            out.write("        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);");
            out.newLine();
            out.write("      })();");
            out.newLine();
            out.write("    </script>");
            out.newLine();
        }
    }

    private void writeDefaultHeader(BufferedWriter out) throws IOException {
        out.write("<!DOCTYPE html>");
        out.newLine();
        out.write("<html>");
        out.newLine();
        out.write("  <head>");
        out.newLine();
        out.write("    <title>Publications</title>");
        out.newLine();
        out.write("    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
        out.newLine();
        
        writeJavascript(out);
        
        out.write("  </head>");
        out.newLine();
        out.write("  <body>");
        out.newLine();
        out.write("    <noscript><p>Some elements on this site require JavaScript. Enable it for the best experience.</p></noscript>");
        out.newLine();
        out.write("  ");
        out.newLine();
    }

    private void writeDefaultFooter(BufferedWriter out) throws IOException {
        out.write("  </body>");
        out.newLine();
        out.write("</html>");
        out.newLine();
    }
}

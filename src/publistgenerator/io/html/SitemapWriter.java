/*
 */
package publistgenerator.io.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import publistgenerator.Pair;
import publistgenerator.bibitem.BibItem;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class SitemapWriter {

    private static final String HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<urlset\n"
            + "      xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n"
            + "      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "      xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9\n"
            + "            http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">";
    private static final String FOOTER = "</urlset>";

    public static void writeSiteMap(List<BibItem> items, File baseSites, File sitemap, File websiteDir) {
        try {
            // Start with all base sites
            Pair<String, List<Site>> pair = parseSitesFile(baseSites);
            String base = pair.getFirst();
            List<Site> sites = pair.getSecond();

            // Add all papers and slides
            for (BibItem item : items) {
                if (item.anyNonEmpty("pdf")) {
                    String loc = "publications/papers/" + item.get("year") + "/" + URLEncoder.encode(item.get("pdf"), "UTF-8").replaceAll("\\+", "%20");

                    if (item.anyNonEmpty("status")) {
                        if ("submitted".equals(item.get("status"))) {
                            // Assign low priority to papers that haven't been accepted yet
                            sites.add(new Site(base, loc, "monthly", 0.2));
                        } else {
                            sites.add(new Site(base, loc, "monthly", 0.5));
                        }
                    } else {
                        sites.add(new Site(base, loc, "yearly", 0.5));
                    }
                } else if (item.anyNonEmpty("slides")) {
                    String loc = "publications/slides/" + item.get("year") + "/" + URLEncoder.encode(item.get("slides"), "UTF-8").replaceAll("\\+", "%20");

                    sites.add(new Site(base, loc, "yearly", 0.5));
                }
            }

            // Read sitemap data, if it exists and check hashes
            File data = new File(websiteDir, "sitemap-data-gen.txt");
            if (data.exists()) {
                HashMap<String, Pair<String, String>> prevData = parseData(data);

                for (Site site : sites) {
                    Pair<String, String> d = prevData.get(site.loc);

                    if (d != null) {
                        site.checkForUpdate(websiteDir, d.getFirst(), d.getSecond());
                    } else {
                        site.checkForUpdate(websiteDir, null, null);
                    }
                }
            } else {
                for (Site site : sites) {
                    site.checkForUpdate(websiteDir, null, null);
                }
            }

            // Write them all to the sitemap
            try (BufferedWriter out = new BufferedWriter(new FileWriter(sitemap))) {
                out.write(HEADER);
                out.newLine();

                for (Site site : sites) {
                    site.writeSitemap(out);
                }

                out.write(FOOTER);
                out.newLine();
            }

            // Write sitemap data for next time
            writeData(data, sites);
        } catch (IOException e) {
            System.err.println("Exception occurred.");
            e.printStackTrace();
        }
    }

    private static Pair<String, List<Site>> parseSitesFile(File baseSites) throws IOException {
        List<Site> sites = new ArrayList<>();
        String base;

        try (BufferedReader br = new BufferedReader(new FileReader(baseSites))) {
            String line = br.readLine();
            base = line.substring(line.indexOf('=') + 1);

            for (line = br.readLine(); line != null; line = br.readLine()) {
                Site s = parseSite(base, line.trim());

                if (s != null) {
                    sites.add(s);
                }
            }
        }

        return new Pair<>(base, sites);
    }

    private static Site parseSite(String base, String line) {
        String[] parts = line.split(",");

        double priority = 0.5;
        String update = "weekly";
        String loc = parts[0].substring(base.length());

        switch (parts.length) {
            case 0:
                return null;
            case 1: // Only loc
                break;
            case 2: // loc and update
                update = parts[1];
                break;
            case 3: // all 3
                update = parts[1];
                priority = Double.parseDouble(parts[2]);
                break;
        }

        return new Site(base, loc, update, priority);
    }

    private static HashMap<String, Pair<String, String>> parseData(File data) throws IOException {
        HashMap<String, Pair<String, String>> siteData = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(data))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String[] parts = line.split(",");
                // loc,hash,lastMod
                siteData.put(parts[0], new Pair<>(parts[1], parts[2]));
            }
        }

        return siteData;
    }

    private static void writeData(File data, List<Site> sites) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(data))) {
            for (Site site : sites) {
                site.writeData(out);
            }
        }
    }

    private static class Site {

        String base;
        String loc;
        String update;
        double priority;
        String lastMod;
        String hash;

        private Site(String base, String loc, String update, double priority) {
            this.base = base;
            this.loc = loc;
            this.update = update;
            this.priority = priority;
            this.lastMod = getDate();
        }

        public void checkForUpdate(File dir, String prevHash, String prevLastMod) throws IOException {
            if (!loc.isEmpty()) {
                hash = getHash(dir);

                if (hash.equals(prevHash)) {
                    // No change
                    lastMod = prevLastMod;
                }
            }
        }

        private String getHash(File dir) throws IOException {
            MessageDigest md = null;

            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(SitemapWriter.class.getName()).log(Level.SEVERE, null, ex);
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(new DigestInputStream(new FileInputStream(new File(dir, URLDecoder.decode(loc, "UTF-8"))), md)))) {
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    // No action needed, just run the entire file through the digest stream
                }
            }

            byte[] digest = md.digest();

            // Convert to a hex string, as per http://stackoverflow.com/a/9855338
            final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            char[] hexChars = new char[digest.length * 2];
            int v;

            for (int j = 0; j < digest.length; j++) {
                v = digest[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }

            return new String(hexChars);
        }

        public void writeSitemap(BufferedWriter out) throws IOException {
            out.write("<url>");
            out.newLine();

            out.write("  <loc>");
            out.write(base);
            out.write(loc);
            out.write("</loc>");
            out.newLine();

            out.write("  <lastmod>");
            out.write(lastMod);
            out.write("</lastmod>");
            out.newLine();

            out.write("  <changefreq>");
            out.write(update);
            out.write("</changefreq>");
            out.newLine();

            out.write("  <priority>");
            out.write(String.format("%.1f", priority));
            out.write("</priority>");
            out.newLine();

            out.write("</url>");
            out.newLine();
        }

        public void writeData(BufferedWriter out) throws IOException {
            if (hash != null && !hash.isEmpty()) {
                out.write(loc);
                out.write(",");
                out.write(hash);
                out.write(",");
                out.write(lastMod);
                out.newLine();
            }
        }

        /**
         * Returns the current date and time, formatted according to
         * http://www.w3.org/TR/NOTE-datetime
         *
         * Example: 2013-04-14T18:31:32-04:00
         *
         * @return
         */
        private String getDate() {
            Calendar c = Calendar.getInstance();
            String dateTime = String.format("%tFT%tT%tz", c, c, c);
            int split = dateTime.length() - 2;
            return dateTime.substring(0, split) + ":" + dateTime.substring(split);
        }
    }
}

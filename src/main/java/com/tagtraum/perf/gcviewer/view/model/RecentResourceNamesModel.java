package com.tagtraum.perf.gcviewer.view.model;

import java.net.URL;
import java.util.*;

import com.tagtraum.perf.gcviewer.view.RecentResourceNamesEvent;

/**
 * RecentFilesMenu.
 * <p/>
 * Date: Sep 25, 2005
 * Time: 10:54:45 PM
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class RecentResourceNamesModel {

    private final static int MAX_ELEMENTS = 10;
    private List<URLSet> urlSetList;
    private List<RecentResourceNamesListener> listeners;
    private Set<URL> allURLs;

    public RecentResourceNamesModel() {
        this.urlSetList = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.allURLs = new HashSet<>();
    }

    public void addRecentURLsListener(RecentResourceNamesListener recentURLsListener) {
        listeners.add(recentURLsListener);
    }

    protected void fireAddEvent(int position, URLSet urlSet) {
        RecentResourceNamesEvent event = new RecentResourceNamesEvent(this, position, urlSet); 
        for (RecentResourceNamesListener listener : listeners) {
            listener.add(event);
        }
    }

    protected void fireRemoveEvent(int position) {
        RecentResourceNamesEvent event = new RecentResourceNamesEvent(this, position);
        for (RecentResourceNamesListener listener : listeners) {
            listener.remove(event);
        }
    }

    public List<String> getURLsStartingWith(String start) {
        List<String> result = new ArrayList<>();
        for (URL url : allURLs) {
            String urlString = url.toString();
            if (urlString.startsWith(start)) {
                result.add(urlString);
            }
        }
        Collections.sort(result);
        return result;
    }

    public void add(URL[] urls) {
        if (urls.length > 0) {
            allURLs.addAll(Arrays.asList(urls));
            final URLSet urlSet = new URLSet(urls);
            if (!urlSetList.contains(urlSet)) {
                urlSetList.add(0, urlSet);
                fireAddEvent(0, urlSet);
                if (urlSetList.size() > MAX_ELEMENTS) {
                    // if list size is too big remove last element
                    urlSetList.remove(MAX_ELEMENTS - 1);
                    fireRemoveEvent(MAX_ELEMENTS - 1);
                }
            }
            else {
                for (int i = 0; i < urlSetList.size(); i++) {
                    URLSet existingURLSet = urlSetList.get(i);
                    if (urlSet.equals(existingURLSet)) {
                        urlSetList.remove(i);
                        fireRemoveEvent(i);
                        urlSetList.add(0, urlSet);
                        fireAddEvent(0, urlSet);
                        break;
                    }
                }
            }
        }
    }

    public class URLSet {
        private String[] urlStrings;
        private URL[] urls;

        public URLSet(URL[] urls) {
            this.urls = urls;
            this.urlStrings = createSortedFileStrings(urls);
        }

        private String[] createSortedFileStrings(URL[] urls) {
            String[] fileStrings = new String[urls.length];
            for (int i = 0; i < urls.length; i++) {
                if (urls[i] != null) {
                    fileStrings[i] = urls[i].toString();
                }
            }
            Arrays.sort(fileStrings);
            
            return fileStrings;
        }

        public URL[] getUrls() {
            return urls;
        }

        public int hashCode() {
            return urlStrings[0].hashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof URLSet) || obj == null) {
                return false;
            }
            
            URLSet that = (URLSet)obj;
            if (that.urlStrings.length != this.urlStrings.length) {
                return false;
            }
            
            for (int i=0; i<that.urlStrings.length; i++) {
                if (!that.urlStrings[i].equals(this.urlStrings[i])) {
                    return false;
                }
            }
            
            return true;
        }

    }

}

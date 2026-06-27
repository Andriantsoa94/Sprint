package model;

import java.util.Objects;

public class UrlMappingKey {
    private String url;
    private String methode;

    public UrlMappingKey (String url ,String method) {
        this.methode = method;
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public String getMethod() {
        return this.methode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UrlMappingKey)){
            return false;
        }
        UrlMappingKey urlMappingKey = (UrlMappingKey) obj;
        return this.url.equals(urlMappingKey.getUrl()) && this.methode.equals(urlMappingKey.getMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.url ,this.methode);
    }

    @Override
    public String toString() {
        return "[" + this.url +","+ this.methode +"]";
    }
}
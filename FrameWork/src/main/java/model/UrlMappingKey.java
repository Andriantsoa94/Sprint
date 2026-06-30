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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlMappingKey that = (UrlMappingKey) o;
        return Objects.equals(url, that.url) && Objects.equals(methode, that.methode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, methode);
    }

}
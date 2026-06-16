package util;

import java.util.ArrayList;
import java.util.List;

import java.net.URL;

public class PackageScanner {
    public List<Class<?>> scanPackage(String packageName) {
        List<Class<?>> listeClasse = new ArrayList<>();

        String path = packageName.replace(".", "/");

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(path);
            if (resource == null) {
                System.out.println("Y a pas de classe dans :" + path);
                return listeClasse;
            }
            System.out.println("Ressources trouves :" + resource.toString());
            return listeClasse;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return listeClasse;
    }
}

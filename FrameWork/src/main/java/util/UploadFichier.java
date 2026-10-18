package util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadFichier {
    private static String dossier;

    String nomFichier;
    String extention;
    byte[] bytes;

    public UploadFichier(String nomFichier, String extention, byte[] bytes) {
        this.nomFichier = nomFichier;
        this.extention = extention;
        this.bytes = bytes;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getExtention() {
        return extention;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public static String getDossier() {
        return dossier;
    }

    public static void setDossier(String d) {
        dossier = d;
    }

    public String sauvegarder() throws IOException {
        if (dossier == null || dossier.isEmpty()) {
            throw new IOException("Dossier d'upload non configure (context-param upload-dir dans web.xml)");
        }
        Path dir = Paths.get(dossier);
        Files.createDirectories(dir);

        Path cible = dir.resolve(Paths.get(nomFichier).getFileName().toString());
        Files.write(cible, bytes);
        return cible.toAbsolutePath().toString();
    }

    public static Map<String , List<UploadFichier>> getFile (HttpServletRequest request) throws ServletException, IOException {
        Map<String , List<UploadFichier>> file = new HashMap<>();

        if (request.getContentType() == null || !request.getContentType().contains("multipart/form-data")) {
            return file;
        }

        for (Part part : request.getParts()) {
            String fullName = part.getSubmittedFileName();
            if (fullName != null && !fullName.isEmpty()) {
                String inputName = part.getName();
                String extension = "";

                byte[] contenu = part.getInputStream().readAllBytes();

                String[] ext = fullName.split("\\.");

                if (ext.length > 1) {
                    extension = ext[ext.length - 1].toLowerCase();
                }

                UploadFichier fileTarget = new UploadFichier(fullName , extension , contenu);
                file.computeIfAbsent(inputName , k -> new ArrayList<>()).add(fileTarget);
            }
        }

        return file;
    }
}

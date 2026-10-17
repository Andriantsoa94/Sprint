package util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadFichier {
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

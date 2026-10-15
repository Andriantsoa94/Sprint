package util;

public class UploadFichier {
    String nomFichier;
    String extention;
    Byte[] bytes;

    public UploadFichier(String nomFichier, String extention, Byte[] bytes) {
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

    public Byte[] getBytes() {
        return bytes;
    }

    public void setBytes(Byte[] bytes) {
        this.bytes = bytes;
    }
}

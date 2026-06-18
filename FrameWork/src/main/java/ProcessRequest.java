import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.PackageScanner;

public class ProcessRequest extends HttpServlet {
    private List<Class<?>> modelesEtControleurs = new ArrayList<>();

    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String url = req.getRequestURI();
        String[] uri = url.split("/");
        String output = uri[uri.length - 1];

        if (output.endsWith(".html")) {
            String cheminPhysique = getServletContext().getRealPath("/" + output);
            File fichier = new File(cheminPhysique);

            if (fichier.exists()) {
                res.setContentType("text/html;charset=UTF-8");

                Files.copy(fichier.toPath(), res.getOutputStream());
                return;
            } else {
                res.sendError(404, "Fichier introuvable");
                return;
            }
        }

        res.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.println(output);

        for (Class<?> clazz : modelesEtControleurs) {
            out.println("- " + clazz.getSimpleName());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    public void init() {
        try {
            String pack = getInitParameter("package");
            String annotation = getInitParameter("annotation");

            PackageScanner packageScan = new PackageScanner();
            this.modelesEtControleurs = packageScan.scanPackage(pack, annotation);

        } catch (Exception ex) {
            System.out.println("Erreur lors de l'initialisation du Framework : " + ex.getMessage());
        }
    }
}
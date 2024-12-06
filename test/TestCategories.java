import capa.CPOracle;
import p1.t6.model.romeumusetelena.Categoria;

import java.util.List;

public class TestCategories {

    public static void main(String[] args) {
        CPOracle cp;

        try {
            System.out.println("Intent de creació de la capa de persistència");
            cp = new CPOracle();
            System.out.println("Connexió establerta a la base de dades!");
        } catch (Exception ex) {
            System.out.println("Problema en crear la capa de persistència:");
            infoError(ex);
            System.out.println("Avortem el programa.");
            return;
        }

        //Proves separades en mètodes
        //provarAfegirCategoria(cp);
        //provarObtenirCategoria(cp);
        //provarObtenirTotesCategories(cp);
        //provarModificarCategoria(cp);
        //provarEliminarCategoria(cp);

        // Tancament de la capa
        try {
            System.out.println("Tancant la capa de persistència...");
            cp.close();
            System.out.println("Capa de persistència tancada correctament.");
        } catch (Exception ex) {
            System.out.println("Error en tancar la capa de persistència: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarAfegirCategoria(CPOracle cp) {
        try {
            Categoria categoria = new Categoria(7,"Categoria Prova", 18, 35); // Constructor per afegir
            cp.afegirCategoria(categoria);
            System.out.println("Categoria afegida correctament: " + categoria);
        } catch (Exception ex) {
            System.out.println("Error en afegir una categoria: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarObtenirCategoria(CPOracle cp) {
        try {
            int idCategoria = 1; // ID d'exemple
            Categoria categoria = cp.obtenirCategoria(idCategoria);
            if (categoria != null) {
                System.out.println("Categoria obtinguda: " + categoria);
            } else {
                System.out.println("No s'ha trobat cap categoria amb l'ID: " + idCategoria);
            }
        } catch (Exception ex) {
            System.out.println("Error en obtenir una categoria: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarObtenirTotesCategories(CPOracle cp) {
        try {
            List<Categoria> categories = cp.obtenirTotesCategories();
            System.out.println("Llista de categories obtingudes:");
            for (Categoria categoria : categories) {
                System.out.println(categoria);
            }
        } catch (Exception ex) {
            System.out.println("Error en obtenir totes les categories: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarModificarCategoria(CPOracle cp) {
        try {
            Categoria categoria = new Categoria(1,"Benjamí", 7, 8); // Exemple d'actualització
            cp.modificarCategoria(categoria);
            System.out.println("Categoria modificada correctament: " + categoria);
        } catch (Exception ex) {
            System.out.println("Error en modificar una categoria: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarEliminarCategoria(CPOracle cp) {
        try {
            int idCategoria = 1; // ID d'exemple per eliminar
            cp.eliminarCategoria(idCategoria);
            //cp.confirmarCanvis();
            System.out.println("Categoria eliminada correctament amb ID: " + idCategoria);
        } catch (Exception ex) {
            System.out.println("Error en eliminar una categoria: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void infoError(Throwable aux) {
        do {
            if (aux.getMessage() != null) {
                System.out.println("\t" + aux.getMessage());
            }
            aux = aux.getCause();
        } while (aux != null);
    }
}

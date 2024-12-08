import capa.CPOracle;
import p1.t6.model.romeumusetelena.Temporada;

import java.util.List;

public class TestTemporades {

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
        //provarAfegirTemporada(cp);
        //provarObtenirTemporada(cp);
        //provarObtenirTotesTemporades(cp);
        provarEliminarTemporada(cp);

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

    private static void provarAfegirTemporada(CPOracle cp) {
        try {
            Temporada temporada = new Temporada(2020); // Constructor per afegir
            cp.afegirTemporada(temporada);
            System.out.println("Temporada afegida correctament: " + temporada);
        } catch (Exception ex) {
            System.out.println("Error en afegir una temporada: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarObtenirTemporada(CPOracle cp) {
        try {
            int idTemporada = 2024; // any d'exemple
            Temporada temporada = cp.obtenirTemporada(idTemporada);
            if (temporada != null) {
                System.out.println("Temporada obtinguda: " + temporada);
            } else {
                System.out.println("No s'ha trobat cap temporada amb l'ID: " + idTemporada);
            }
        } catch (Exception ex) {
            System.out.println("Error en obtenir una temporada: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarObtenirTotesTemporades(CPOracle cp) {
        try {
            List<Temporada> temporades = cp.obtenirTotesTemporades();
            System.out.println("Llista de temporades obtingudes:");
            for (Temporada temporada : temporades) {
                System.out.println(temporada);
            }
        } catch (Exception ex) {
            System.out.println("Error en obtenir totes les temporades: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarEliminarTemporada(CPOracle cp) {
        try {
            int idTemporada = 2020; // ID d'exemple per eliminar
            cp.eliminarTemporada(idTemporada);
            cp.confirmarCanvis();
            System.out.println("Temporada eliminada correctament amb ID: " + idTemporada);
        } catch (Exception ex) {
            System.out.println("Error en eliminar una temporada: " + ex.getMessage());
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

import capa.CPOracle;
import p1.t6.model.romeumusetelena.Equip;
import p1.t6.model.romeumusetelena.TipusEquip;

import java.util.List;

public class TestEquips {

    public static void main(String[] args) {
        CPOracle cp;

        try {
            System.out.println("Intent de creació de la capa de persistència");
            cp = new CPOracle();
            System.out.println("Connexió establerta a la base de dades!");
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println("Problema en crear la capa de persistència:");
            infoError(ex);
            System.out.println("Avortem el programa.");
            return;
        }

        //Proves separades en mètodes
        provarAfegirEquip(cp);
        provarObtenirEquip(cp);
        provarObtenirTotsEquips(cp);
        provarModificarEquip(cp);
        provarEliminarEquip(cp);

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

    private static void provarAfegirEquip(CPOracle cp) {
        try {
            Equip equip = new Equip("Nom Prova", TipusEquip.H, 2024, 1); // Constructor sense ID per afegir
            cp.afegirEquip(equip);
            //cp.confirmarCanvis();
            System.out.println("Equip afegit correctament: " + equip);
        } catch (Exception ex) {
            System.out.println("Error en afegir un equip: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarObtenirEquip(CPOracle cp) {
        try {
            int idEquip = 8; // ID d'exemple
            Equip equip = cp.obtenirEquip(idEquip);
            if (equip != null) {
                System.out.println("Equip obtingut: " + equip);
            } else {
                System.out.println("No s'ha trobat cap equip amb l'ID: " + idEquip);
            }
        } catch (Exception ex) {
            System.out.println("Error en obtenir un equip: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarObtenirTotsEquips(CPOracle cp) {
        try {
            List<Equip> equips = cp.obtenirTotsEquips();
            System.out.println("Llista d'equips obtinguts:");
            for (Equip equip : equips) {
                System.out.println(equip);
            }
        } catch (Exception ex) {
            System.out.println("Error en obtenir tots els equips: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarModificarEquip(CPOracle cp) {
        try {
            Equip equip = new Equip(40,"Nom Modificat", TipusEquip.D, 2025, 2); // Exemple d'equip modificat
            cp.modificarEquip(equip);
            //cp.confirmarCanvis();
            System.out.println("Equip modificat correctament: " + equip);
        } catch (Exception ex) {
            System.out.println("Error en modificar un equip: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarEliminarEquip(CPOracle cp) {
        try {
            int idEquip = 2; // ID d'exemple per eliminar
            cp.eliminarEquip(idEquip);
            cp.confirmarCanvis();
            System.out.println("Equip eliminat correctament amb ID: " + idEquip);
        } catch (Exception ex) {
            System.out.println("Error en eliminar un equip: " + ex.getMessage());
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


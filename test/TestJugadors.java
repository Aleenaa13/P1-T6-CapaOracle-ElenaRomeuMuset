import capa.CPOracle;
import java.util.Calendar;
import p1.t6.model.romeumusetelena.Jugador;
import p1.t6.model.romeumusetelena.Adreca;

import java.util.List;

public class TestJugadors {

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

        // Proves separades en mètodes
        //provarAfegirJugador(cp);
        //provarObtenirJugador(cp);
        //provarObtenirTotsJugadors(cp);
        //provarModificarJugador(cp);
        //provarEliminarJugador(cp);
        // Proves separades en mètodes
        provarBuscarPerNIFJugador(cp);
        //provarBuscarPerDataNaixJugador(cp);
        //provarBuscarJugadorsOrdenatsPerCognom(cp);
        //provarBuscarNomJugador(cp);


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

    private static void provarAfegirJugador(CPOracle cp) {
        try {
            // Creació de la data de naixement
            Calendar calendar = Calendar.getInstance();
            calendar.set(1995, Calendar.JUNE, 15); // 15 de juny de 1995
            java.util.Date dataNaix = calendar.getTime();

            // Creació de l'adreça i jugador
            Adreca adreca = new Adreca("Carrer Prova", "08001", "Barcelona");
            Jugador jugador = new Jugador(
                "Nom Prova", 
                "Cognoms Prova", 
                adreca, 
                "foto.jpg", 
                2025, 
                "ES1234567890123456789012", 
                "ID040", 
                dataNaix, 
                'H'
            );

            // Afegir el jugador a la base de dades
            cp.afegirJugador(jugador);
            System.out.println("Jugador afegit correctament: " + jugador);
        } catch (Exception ex) {
            System.out.println("Error en afegir un jugador: " + ex.getMessage());
            infoError(ex);
        }
    }


    private static void provarObtenirJugador(CPOracle cp) {
        try {
            int idJugador = 1; // ID d'exemple
            Jugador jugador = cp.obtenirJugador(idJugador);
            if (jugador != null) {
                System.out.println("Jugador obtingut: " + jugador);
            } else {
                System.out.println("No s'ha trobat cap jugador amb l'ID: " + idJugador);
            }
        } catch (Exception ex) {
            System.out.println("Error en obtenir un jugador: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarObtenirTotsJugadors(CPOracle cp) {
        try {
            List<Jugador> jugadors = cp.obtenirTotsJugadors();
            System.out.println("Llista de jugadors obtinguts:");
            for (Jugador jugador : jugadors) {
                System.out.println(jugador);
            }
        } catch (Exception ex) {
            System.out.println("Error en obtenir tots els jugadors: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarModificarJugador(CPOracle cp) {
        try {
            Adreca novaAdreca = new Adreca("Carrer Nou", "08002", "Barcelona");
            Jugador jugador = new Jugador(1, "Nom Modificat", "Cognoms Modificats", novaAdreca, "foto_nova.jpg", 2026, "ES9876543210987654321098", "ID654321", new java.util.Date(), 'D');
            cp.modificarJugador(jugador);
            System.out.println("Jugador modificat correctament: " + jugador);
        } catch (Exception ex) {
            System.out.println("Error en modificar un jugador: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarEliminarJugador(CPOracle cp) {
        try {
            int idJugador = 104; // ID d'exemple per eliminar
            cp.eliminarJugador(idJugador);
            System.out.println("Jugador eliminat correctament amb ID: " + idJugador);
        } catch (Exception ex) {
            System.out.println("Error en eliminar un jugador: " + ex.getMessage());
            infoError(ex);
        }
    }
    
    private static void provarBuscarPerNIFJugador(CPOracle cp) {
        try {
            String nif = "45678901U"; // Exemple de NIF
            List<Jugador> jugadors = cp.buscarPerNIFJugador(nif); // Assumeix que el mètode retorna una llista de jugadors
            if (jugadors == null || jugadors.isEmpty()) {
                System.out.println("No s'ha trobat cap jugador amb el NIF: " + nif);
            } else {
                System.out.println("Jugadors trobats amb NIF: " + nif);
                for (Jugador jugador : jugadors) {
                    System.out.println(jugador);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error en buscar jugadors pel NIF: " + ex.getMessage());
            infoError(ex);
        }
    }


    private static void provarBuscarPerDataNaixJugador(CPOracle cp) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2015, Calendar.JANUARY, 15); // Exemple de data de naixement
            java.util.Date dataNaix = calendar.getTime();

            // Convertim la data de java.util.Date a java.sql.Date
            java.sql.Date sqlDataNaix = new java.sql.Date(dataNaix.getTime());

            // Passar la data convertida al mètode
            List<Jugador> jugadors = cp.buscarPerDataNaixJugador(sqlDataNaix);

            if (jugadors.isEmpty()) {
                System.out.println("No s'ha trobat cap jugador amb la data de naixement: " + sqlDataNaix);
            } else {
                System.out.println("Jugadors trobats amb la data de naixement: " + sqlDataNaix);
                for (Jugador jugador : jugadors) {
                    System.out.println(jugador);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error en buscar jugadors per data de naixement: " + ex.getMessage());
            infoError(ex);
        }
    }


    private static void provarBuscarJugadorsOrdenatsPerCognom(CPOracle cp) {
        try {
            List<Jugador> jugadors = cp.buscarJugadorsOrdenatsPerCognom(true);
            if (jugadors.isEmpty()) {
                System.out.println("No s'han trobat jugadors.");
            } else {
                System.out.println("Jugadors trobats ordenats per cognoms:");
                for (Jugador jugador : jugadors) {
                    System.out.println(jugador);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error en buscar jugadors ordenats per cognoms: " + ex.getMessage());
            infoError(ex);
        }
    }

    private static void provarBuscarNomJugador(CPOracle cp) {
        try {
            String nom = "Elena"; // Exemple de nom
            List<Jugador> jugadors = cp.buscarNomJugador(nom);
            if (jugadors.isEmpty()) {
                System.out.println("No s'ha trobat cap jugador amb el nom: " + nom);
            } else {
                System.out.println("Jugadors trobats amb el nom: " + nom);
                for (Jugador jugador : jugadors) {
                    System.out.println(jugador);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error en buscar jugadors pel nom: " + ex.getMessage());
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

import capa.CPOracle;

public class TestUsuari {

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
        provarValidarUsuari(cp);

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

    private static void provarValidarUsuari(CPOracle cp) {
        try {
            String login = "elena"; // Usuari de prova
            String contrasenya = "elena"; // Contrasenya de prova
            boolean resultat = cp.validarUsuari(login, contrasenya);
            if (resultat) {
                System.out.println("Usuari validat correctament.");
            } else {
                System.out.println("Usuari o contrasenya incorrectes.");
            }
        } catch (Exception ex) {
            System.out.println("Error en validar l'usuari: " + ex.getMessage());
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

package engine;

import mundo.Mapa;
import entidades.Entidade;

public class Renderizador {

    public static void renderizar(Mapa mapa, Entidade... entidades) {
        String[][] tela = mapa.comoArrayString();

        for (Entidade e : entidades)
            tela[e.posicao().y()][e.posicao().x()] = e.cor() + e.simbolo() + "\u001B[0m";

        for (String[] linha : tela) {
            for (String celula : linha)
                System.out.print(celula);
            System.out.println();
        }
    }

    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

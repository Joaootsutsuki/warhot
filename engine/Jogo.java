package engine;

import entidades.Jogador;
import entidades.Monstro;
import mundo.Mapa;

public class Jogo {
    private Mapa mapa;
    private Jogador jogador;
    private Monstro goblin;

    // Cada mob (contando com o jogador) eh representado por uma cor e um caractere

    public void iniciar() {
        mapa = new Mapa(20, 10);
        jogador = new Jogador("Jogador", mapa.posicaoAleatoria());
        goblin = new Monstro("Goblin", "■", "\u001B[31m", mapa.posicaoAleatoria()); // quadrado vermelho

        loopDoJogo();
    }

    private void loopDoJogo() {
        while (true) {
            Renderizador.limparTela();
            Renderizador.renderizar(mapa, jogador, goblin);

            char tecla = Input.getKey();

            switch (tecla) {
                case 'w' -> jogador.mover(0, -1, mapa);
                case 's' -> jogador.mover(0, 1, mapa);
                case 'a' -> jogador.mover(-1, 0, mapa);
                case 'd' -> jogador.mover(1, 0, mapa);
                case 'q' -> System.exit(0);
            }

            goblin.moverAleatorio(mapa);
        }
    }
}

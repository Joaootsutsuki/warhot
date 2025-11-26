package entidades;

import java.util.Random;
import mundo.Mapa;
import mundo.Posicao;

// Movimento aleatorio pra exemplificar o funcionamento

public class Monstro extends Entidade {
    private Random rand = new Random();

    public Monstro(String nome, String simbolo, String cor, Posicao posicao) {
        super(nome, simbolo, cor, posicao);
    }

    public void moverAleatorio(Mapa mapa) {
        int dx = rand.nextInt(3) - 1;
        int dy = rand.nextInt(3) - 1;
        mover(dx, dy, mapa);
    }
}

package entidades;

import mundo.Posicao;

public class Jogador extends Entidade {
    public Jogador(String nome, Posicao posicao) {
        super(nome, "@", "\u001B[32m", posicao); // @ verde
    }
}

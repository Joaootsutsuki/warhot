package entidades;

import mundo.Posicao;

public class Jogador extends Entidade {
    private int manaMax;
    private int manaAtual;
    private int nivel;
    private int xp;

    public Jogador(String nome, Posicao posicao) {
        super(nome, new String[][] {
                { "@", "@", "@" },
                { "@", "@", "@" },
                { "@", "@", "@" }
        }, posicao, 30, 5, 2);

        this.manaMax = 20;
        this.manaAtual = 20;
        this.nivel = 1;
        this.xp = 0;
    }

    public int manaMax() {
        return manaMax;
    }

    public int manaAtual() {
        return manaAtual;
    }

    public int nivel() {
        return nivel;
    }

    public int xp() {
        return xp;
    }

    public boolean usarMana(int custo) {
        if (manaAtual >= custo) {
            manaAtual -= custo;
            return true;
        }
        return false;
    }

    public void ganharXP(int quantidade) {
        xp += quantidade;
    }
}
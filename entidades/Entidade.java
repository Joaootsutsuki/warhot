package entidades;

import mundo.Mapa;
import mundo.Posicao;

public abstract class Entidade {
    protected String nome;
    protected String simbolo;
    protected String cor;
    protected Posicao posicao;

    public Entidade(String nome, String simbolo, String cor, Posicao posicao) {
        this.nome = nome;
        this.simbolo = simbolo;
        this.cor = cor;
        this.posicao = posicao;
    }

    public void mover(int dx, int dy, Mapa mapa) {
        int nx = posicao.x() + dx;
        int ny = posicao.y() + dy;
        if (mapa.podeAndar(nx, ny))
            posicao = new Posicao(nx, ny);
    }

    public Posicao posicao() {
        return posicao;
    }

    public String simbolo() {
        return simbolo;
    }

    public String cor() {
        return cor;
    }
}

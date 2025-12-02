package entidades;

import mundo.Mapa;
import mundo.Position;

public abstract class Entidade {
    protected String nome;
    protected String[][] sprite;
    protected Position position;

    // Combat stats
    protected int hpMax;
    protected int hpAtual;
    protected int ataque;
    protected int defesa;
    protected boolean vivo;

    public Entidade(String nome, String[][] sprite, Position position, int hp, int ataque, int defesa) {
        this.nome = nome;
        this.sprite = sprite;
        this.position = position;
        this.hpMax = hp;
        this.hpAtual = hp;
        this.ataque = ataque;
        this.defesa = defesa;
        this.vivo = true;
    }
    

    public void mover(int dx, int dy, Mapa mapa) {
        int nx = position.x() + dx;
        int ny = position.y() + dy;

        if (mapa.podeAndar(nx, ny))
            position = new Position(nx, ny);
    }

    public void receberDano(int dano) {
        int danoReal = Math.max(1, dano - defesa);
        hpAtual -= danoReal;
        if (hpAtual <= 0) {
            hpAtual = 0;
            vivo = false;
        }
    }

    public int atacar() {
        return ataque + (int) (Math.random() * 3) - 1; // ataque ± 1
    }

    // Getters
    public Position position() {
        return position;
    }

    public void setPosition(Position novaPosicao) {
    this.position = novaPosicao;
    }

    public String[][] sprite() {
        return sprite;
    }

    public String nome() {
        return nome;
    }

    public int hpAtual() {
        return hpAtual;
    }

    public int hpMax() {
        return hpMax;
    }

    public int ataque() {
        return ataque;
    }

    public int defesa() {
        return defesa;
    }

    public boolean vivo() {
        return vivo;
    }
}
package mundo;

import java.util.Random;

public class Mapa {
    private Bloco[][] grade;
    private Random r = new Random();

    public Mapa(int largura, int altura) {
        grade = new Bloco[altura][largura];
        for (int y = 0; y < altura; y++)
            for (int x = 0; x < largura; x++)
                grade[y][x] = Bloco.CHAO;
    }

    public boolean dentroDosLimites(int x, int y) {
        return y >= 0 && y < grade.length && x >= 0 && x < grade[0].length;
    }

    public boolean podeAndar(int x, int y) {
        return dentroDosLimites(x, y) && grade[y][x] == Bloco.CHAO;
    }

    public Posicao posicaoAleatoria() {
        while (true) {
            int x = r.nextInt(grade[0].length);
            int y = r.nextInt(grade.length);
            if (grade[y][x] == Bloco.CHAO)
                return new Posicao(x, y);
        }
    }

    public String[][] comoArrayString() {
        String[][] arr = new String[grade.length][grade[0].length];
        for (int y = 0; y < grade.length; y++)
            for (int x = 0; x < grade[0].length; x++)
                arr[y][x] = grade[y][x].simbolo;
        return arr;
    }
}

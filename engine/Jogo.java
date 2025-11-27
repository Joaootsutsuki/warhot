package engine;

import entidades.Jogador;
import entidades.Monstro;
import mundo.Mapa;
import mundo.Posicao;

public class Jogo {
    private Mapa mapa;
    private Jogador jogador;
    private Monstro goblin;
    private MessageLog log;
    private boolean emCombate;
    private Monstro inimigoAtual;

    public void iniciar() {
        mapa = new Mapa(60, 30);
        jogador = new Jogador("Herói", mapa.posicaoAleatoria());
        log = new MessageLog();

        String VERDE = "\u001B[32m";
        goblin = new Monstro("Goblin", "■", VERDE, mapa.posicaoAleatoria(), 15, 3, 1);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Input.cleanup();
            Renderizador.limparTela();
        }));

        log.adicionar("Você entra na dungeon...");
        loopDoJogo();
    }

    private void loopDoJogo() {
        while (jogador.vivo()) {
            Renderizador.limparTela();
            Renderizador.renderizar(mapa, jogador, log, goblin);

            char tecla = Input.getKey();

            if (emCombate) {
                processarCombate(tecla);
            } else {
                processarMovimento(tecla);
            }
        }

        gameOver();
    }

    private void processarMovimento(char tecla) {
        int oldX = jogador.posicao().x();
        int oldY = jogador.posicao().y();

        switch (tecla) {
            case 'w' -> jogador.mover(0, -1, mapa);
            case 's' -> jogador.mover(0, 1, mapa);
            case 'a' -> jogador.mover(-1, 0, mapa);
            case 'd' -> jogador.mover(1, 0, mapa);
            case 'q' -> {
                Input.cleanup();
                System.exit(0);
            }
        }

        if (jogador.posicao().x() != oldX || jogador.posicao().y() != oldY) {
            verificarColisao();
        }
    }

    private void processarCombate(char tecla) {
        switch (tecla) {
            case '1' -> ataqueBasico();
            case '2' -> ataquePoderoso();
            case '3' -> magiaFogo();
            case 'r' -> tentarFugir();
        }
    }

    private void ataqueBasico() {
        int dano = jogador.atacar();
        inimigoAtual.receberDano(dano);
        log.adicionar(String.format("Você ataca %s por %d de dano!",
                inimigoAtual.nome(), dano));

        if (verificarMorteInimigo())
            return;
        turnoInimigo();
    }

    private void ataquePoderoso() {
        int custoMana = 5;
        if (!jogador.usarMana(custoMana)) {
            log.adicionar("Mana insuficiente!");
            return;
        }

        int dano = (int) (jogador.atacar() * 1.5);
        inimigoAtual.receberDano(dano);
        log.adicionar(String.format("Ataque Poderoso! %d de dano!", dano));

        if (verificarMorteInimigo())
            return;
        turnoInimigo();
    }

    private void magiaFogo() {
        int custoMana = 8;
        if (!jogador.usarMana(custoMana)) {
            log.adicionar("Mana insuficiente!");
            return;
        }

        int dano = 10 + (int) (Math.random() * 5);
        inimigoAtual.receberDano(dano);
        log.adicionar(String.format("Bola de Fogo! %d de dano!", dano));

        if (verificarMorteInimigo())
            return;
        turnoInimigo();
    }

    private void tentarFugir() {
        if (Math.random() < 0.5) {
            log.adicionar("Você fugiu do combate!");
            emCombate = false;
            inimigoAtual = null;

            // Move jogador para longe
            jogador.mover(-5, 0, mapa);
        } else {
            log.adicionar("Você não conseguiu fugir!");
            turnoInimigo();
        }
    }

    private void turnoInimigo() {
        int dano = inimigoAtual.atacar();
        jogador.receberDano(dano);
        log.adicionar(String.format("%s ataca você por %d de dano!",
                inimigoAtual.nome(), dano));

        if (!jogador.vivo()) {
            log.adicionar("Você foi derrotado...");
        }
    }

    private boolean verificarMorteInimigo() {
        if (!inimigoAtual.vivo()) {
            int xpGanho = 10;
            jogador.ganharXP(xpGanho);
            log.adicionar(String.format("%s foi derrotado! +%d XP",
                    inimigoAtual.nome(), xpGanho));
            emCombate = false;
            inimigoAtual = null;
            return true;
        }
        return false;
    }

    private void verificarColisao() {
        Posicao pj = jogador.posicao();

        if (goblin.vivo() && colidem(pj, goblin.posicao())) {
            emCombate = true;
            inimigoAtual = goblin;
            log.adicionar("Você encontrou um " + goblin.nome() + "!");
            log.adicionar("=== COMBATE ===");
            log.adicionar("[1] Ataque Basico  [2] Ataque Poderoso (5 MP)");
            log.adicionar("[3] Bola de Fogo (8 MP)  [R] Fugir");
        }
    }

    private boolean colidem(Posicao p1, Posicao p2) {
        return Math.abs(p1.x() - p2.x()) < 3 && Math.abs(p1.y() - p2.y()) < 3;
    }

    private void gameOver() {
        Renderizador.limparTela();
        System.out.println();
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║        VOCÊ MORREU!            ║");
        System.out.println("╚════════════════════════════════╝");
        System.out.println();
        System.out.println("XP Total: " + jogador.xp());
        System.out.println();
        System.out.println("Pressione Q para sair...");

        while (Input.getKey() != 'q')
            ;
        Input.cleanup();
        System.exit(0);
    }
}
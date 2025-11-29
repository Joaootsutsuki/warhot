package engine;

import mundo.Mapa;
import entidades.Entidade;
import entidades.Jogador;
import items.Chest;
import java.util.List;

public class Renderizador {

    public static void renderizar(Mapa mapa, Jogador jogador, MessageLog log, List<Chest> chests,
            Entidade... outrasEntidades) {
        String[][] tela = mapa.comoArrayString();

        // Render chests first
        for (Chest chest : chests) {
            if (!chest.isOpened()) {
                String[][] s = chest.sprite();
                int baseX = chest.posicao().x();
                int baseY = chest.posicao().y();

                for (int sy = 0; sy < s.length; sy++) {
                    for (int sx = 0; sx < s[0].length; sx++) {
                        int tx = baseX + sx;
                        int ty = baseY + sy;

                        if (tx >= 0 && ty >= 0 && ty < tela.length && tx < tela[0].length)
                            tela[ty][tx] = s[sy][sx];
                    }
                }
            }
        }

        // Then render entities
        Entidade[] todasEntidades = new Entidade[outrasEntidades.length + 1];
        todasEntidades[0] = jogador;
        System.arraycopy(outrasEntidades, 0, todasEntidades, 1, outrasEntidades.length);

        for (Entidade e : todasEntidades) {
            if (!e.vivo())
                continue;

            String[][] s = e.sprite();
            int baseX = e.posicao().x();
            int baseY = e.posicao().y();

            for (int sy = 0; sy < s.length; sy++) {
                for (int sx = 0; sx < s[0].length; sx++) {
                    int tx = baseX + sx;
                    int ty = baseY + sy;

                    if (tx >= 0 && ty >= 0 && ty < tela.length && tx < tela[0].length)
                        tela[ty][tx] = s[sy][sx];
                }
            }
        }

        String barraLateral = gerarBarraLateral(jogador, outrasEntidades);
        String[] linhasLaterais = barraLateral.split("\n");

        for (int i = 0; i < tela.length; i++) {
            for (String celula : tela[i])
                System.out.print(celula);

            System.out.print(" | ");
            if (i < linhasLaterais.length)
                System.out.print(linhasLaterais[i]);

            System.out.println();
        }

        System.out.println("=".repeat(90));

        List<String> msgs = log.getMessages();
        if (msgs.isEmpty()) {
            System.out.println("Bem-vindo ao Warhot! Use WASD para mover, Q para sair.");
        } else {
            for (String msg : msgs) {
                System.out.println(msg);
            }
        }
    }

    private static String gerarBarraLateral(Jogador jogador, Entidade... inimigos) {
        StringBuilder sb = new StringBuilder();

        String CYAN = "\u001B[36m";
        String YELLOW = "\u001B[33m";
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String RESET = "\u001B[0m";

        sb.append(String.format("%s%s%s\n", CYAN, jogador.nome(), RESET));
        sb.append(String.format("Level: %d\n", jogador.nivel()));
        sb.append("─".repeat(25) + "\n");

        // Barra de HP
        String hpBar = gerarBarra(jogador.hpAtual(), jogador.hpMax(), 15, RED);
        sb.append(String.format("HP: %s %d/%d\n", hpBar, jogador.hpAtual(), jogador.hpMax()));

        // Barra de Mana
        String manaBar = gerarBarra(jogador.manaAtual(), jogador.manaMax(), 15, CYAN);
        sb.append(String.format("MP: %s %d/%d\n", manaBar, jogador.manaAtual(), jogador.manaMax()));

        sb.append("─".repeat(25) + "\n");

        // Stats
        sb.append(String.format("%sSTATS:%s\n", YELLOW, RESET));
        sb.append(String.format("VIT:%d END:%d STR:%d\n",
                jogador.stats().vitality(), jogador.stats().endurance(), jogador.stats().strength()));
        sb.append(String.format("DEX:%d INT:%d LCK:%d\n",
                jogador.stats().dexterity(), jogador.stats().intelligence(), jogador.stats().luck()));

        sb.append("─".repeat(25) + "\n");

        // Weight
        double currentWeight = jogador.inventory().getCurrentWeight();
        int maxWeight = jogador.stats().maxCarryWeight();
        sb.append(String.format("Weight: %.1f/%d\n", currentWeight, maxWeight));

        sb.append(String.format("XP: %d\n", jogador.xp()));

        // Show equipped weapon
        if (jogador.inventory().getEquippedWeapon() != null) {
            sb.append("\n");
            sb.append(String.format("%sEquipped:%s\n", YELLOW, RESET));
            sb.append(String.format("%s\n", jogador.inventory().getEquippedWeapon().name()));
        }

        sb.append("\n");

        sb.append(String.format("%s=== ENEMIES ===%s\n", YELLOW, RESET));
        boolean temInimigo = false;
        for (Entidade e : inimigos) {
            if (e.vivo()) {
                String inimigoHpBar = gerarBarra(e.hpAtual(), e.hpMax(), 12, RED);
                sb.append(String.format("%s%s%s\n", GREEN, e.nome(), RESET));
                sb.append(String.format("HP: %s %d/%d\n", inimigoHpBar, e.hpAtual(), e.hpMax()));
                sb.append("\n");
                temInimigo = true;
            }
        }
        if (!temInimigo) {
            sb.append("No living enemies\n");
        }

        return sb.toString();
    }

    private static String gerarBarra(int atual, int max, int largura, String cor) {
        int cheio = (int) ((double) atual / max * largura);
        String RESET = "\u001B[0m";

        StringBuilder barra = new StringBuilder(cor);
        for (int i = 0; i < largura; i++) {
            if (i < cheio)
                barra.append("█");
            else
                barra.append("░");
        }
        barra.append(RESET);
        return barra.toString();
    }

    public static void limparTela() {
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
}
package br.com.cod3r.campoMinado;

import br.com.cod3r.campoMinado.modelo.Tabuleiro;
import br.com.cod3r.campoMinado.visao.TabuleiroConsole;

public class Aplicacao {

    static void main() {
        Tabuleiro tabuleiro = new Tabuleiro(6,6,6);
        new TabuleiroConsole(tabuleiro);
    }
}


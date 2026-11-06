package br.com.cod3r.campoMinado.visao;

import br.com.cod3r.campoMinado.excecao.ExplosaoException;
import br.com.cod3r.campoMinado.excecao.SairException;
import br.com.cod3r.campoMinado.modelo.Tabuleiro;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

public class TabuleiroConsole {
    private Tabuleiro tabuleiro;
    private Scanner entrada = new Scanner(System.in);

    public  TabuleiroConsole(Tabuleiro tabuleiro){
        this.tabuleiro = tabuleiro;
        executarJogo();

    }

    private void executarJogo(){
        try {
            boolean continuar = true;
            while (continuar){
                cicloDoJogo();
                System.out.print("Outra partida? (S/n) ");
                String resposta = entrada.nextLine();
                if("n".equalsIgnoreCase(resposta)){
                    continuar = false;
                }else {
                    tabuleiro.reiniciar();
                }
            }
        }catch (SairException e){
            System.out.println("Flw!");
        } finally {
            entrada.close();
        }
    }

    private void cicloDoJogo(){
        try {
            while (!tabuleiro.objetivoAlcancado()){
                System.out.println(tabuleiro);

                String digitado = camputarValorDigitado("Digite (x, y): ");

                Iterator<Integer> xy = Arrays.stream(digitado.split(","))
                        .map(e -> Integer.parseInt(e.trim()))
                        .iterator();

               digitado = camputarValorDigitado("1 - Abrir ou 2 (Des)Marcar");

               if("1".equalsIgnoreCase(digitado)){
                   tabuleiro.abrir(xy.next(), xy.next());
               }else if("2".equalsIgnoreCase(digitado)){
                   tabuleiro.alterarMarcacao(xy.next(), xy.next());
               }
            }

            System.out.println("Voce ganhou brabo tmj lek");
        }catch (ExplosaoException e){
            System.out.println(tabuleiro);
            System.out.println("Voce perdeu! LIXO");
        }
    }

    private  String camputarValorDigitado(String texto){
        System.out.print(texto);
        String digitado = entrada.nextLine();

        if("sair".equalsIgnoreCase(digitado)){
            throw  new SairException();
        }
        return digitado;
    }
}

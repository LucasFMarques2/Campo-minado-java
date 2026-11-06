package br.com.cod3r.campoMinado.modelo;

import br.com.cod3r.campoMinado.excecao.ExplosaoException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Tabuleiro {
    private int numeroDeLinhas;
    private int numeroDeColunas;
    private int numeroDeMinas;

    private List<Campo> campos = new ArrayList<>();

    public Tabuleiro(int numeroDeLinhas, int numeroDeColunas, int numeroDeMinas){
        this.numeroDeLinhas = numeroDeLinhas;
        this.numeroDeColunas = numeroDeColunas;
        this.numeroDeMinas = numeroDeMinas;

        gerarCampos();
        associarVizinhos();
        sortearMinas();
    }

    public void abrir(int numeroDeLinhas, int numeroDeColunas){
        try {
            campos.parallelStream()
                    .filter(c -> c.getLinha() == numeroDeLinhas && c.getColuna() == numeroDeColunas)
                    .findFirst()
                    .ifPresent(Campo::abrir);
        }catch (ExplosaoException e){
            campos.forEach(c -> c.setAberto(true));
            throw e;
        }
    }

    public void alterarMarcacao(int numeroDeLinhas, int numeroDeColunas){
        campos.parallelStream()
                .filter(c -> c.getLinha() == numeroDeLinhas && c.getColuna() == numeroDeColunas)
                .findFirst()
                .ifPresent(Campo::alternarMarcacao);
    }


    private void gerarCampos() {
        for (int l = 0; l < numeroDeLinhas ; l++) {
            for (int c = 0; c < numeroDeColunas; c++) {
                campos.add(new Campo(l, c));
            }
        }
    }

    private void associarVizinhos() {
        for(Campo c1: campos){
            for(Campo c2: campos){
                c1.adicionarVizinho(c2);
            }
        }
    }

    private void sortearMinas() {
        long minasArmadas = 0;

        do {
            int aleatorio = (int) (Math.random() * campos.size());
            campos.get(aleatorio).minar();
            minasArmadas = campos.stream().filter(Campo::isMinado).count();
        }while (minasArmadas < numeroDeMinas);
    }

    public boolean objetivoAlcancado(){
        return campos.stream().allMatch(Campo::objetivoAlcancado);
    }

    public void reiniciar(){
        campos.forEach(Campo::reiniciar);
        sortearMinas();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int c = 0; c < numeroDeColunas; c++) {
            sb.append(" ");
            sb.append(c);
            sb.append(" ");
        }
        sb.append("\n");
        int i = 0;
        for (int l = 0; l < numeroDeLinhas ; l++) {
            sb.append(l);
            sb.append(" ");
            for (int c = 0; c < numeroDeColunas; c++) {
                sb.append(" ");
                sb.append(campos.get(i));
                sb.append(" ");
                i++;
            }
            sb.append("\n");
        }

        return sb.toString();
    };
}

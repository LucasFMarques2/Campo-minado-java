package br.com.cod3r.campoMinado.modelo;

import br.com.cod3r.campoMinado.colors.Colors;
import br.com.cod3r.campoMinado.excecao.ExplosaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CampoTest {

    private Campo campo;

    @BeforeEach
    void iniciarCampo(){
        campo = new Campo(3,3);
    }

    @Test
    void testVizinhoRealDistancia1(){
        Campo vizinho = new Campo(3, 2);
        boolean resultado = campo.adicionarVizinho(vizinho);

        assertTrue(resultado);
    }

    @Test
    void testVizinhoRealDistancia2(){
        Campo vizinho = new Campo(2, 2);
        boolean resultado = campo.adicionarVizinho(vizinho);
        assertTrue(resultado);
    }

    @Test
    void testNaoEVizinho(){
        Campo vizinho = new Campo(5, 3);
        boolean resultado = campo.adicionarVizinho(vizinho);
        assertFalse(resultado);
    }

    @Test
    void testeValorPadraoMarcado(){
        assertFalse(campo.isMarcado());
    }

    @Test
    void testeAlternarMarcacao(){
        campo.alternarMarcacao();
        assertTrue(campo.isMarcado());
    }

    @Test
    void testeAbrirNaoMinadoNaoMarcado(){
        assertTrue(campo.abrir());
    }

    @Test
    void testeAbrirNaoMinadoMarcado(){
        campo.alternarMarcacao();
        assertFalse(campo.abrir());
    }

    @Test
    void testeAbrioMinadoMarcado(){
        campo.minar();
        campo.alternarMarcacao();
        assertFalse(campo.abrir());
    }

    @Test
    void testeAbrioMinadoNaoMarcado(){
        campo.minar();
        assertThrows(ExplosaoException.class, () ->{
           campo.abrir();
        });
    }

    @Test
    void testeAbrirComVizinho(){
        Campo campo1 = new Campo(2,2);
        Campo campo2 = new Campo(1,1);

        campo.adicionarVizinho(campo1);
        campo1.adicionarVizinho(campo2);

        campo.abrir();

        assertTrue(campo1.isAberto() && campo2.isAberto());
    }

    @Test
    void testeCampoMarcado(){
        campo.alternarMarcacao();
        assertSame(Colors.AMARELO + "X" + Colors.RESET, campo.toString());
    }

    @Test
    void testeCampoMinado(){
        campo.minar();
        assertThrows(ExplosaoException.class, () -> campo.abrir());
        assertEquals(Colors.VERMELHO + "*" + Colors.RESET, campo.toString());
    }

    @Test
    void testeCampoAberto(){
        campo.abrir();
        assertEquals(Colors.CIANO + " " + Colors.RESET, campo.toString());
    }

    @Test
    void testeCampoAbriuVizinhoMinado(){
        Campo vizinho = new Campo(2,2);
        vizinho.minar();
        campo.adicionarVizinho(vizinho);
        campo.abrir();
        assertEquals("1", campo.toString());
    }

}

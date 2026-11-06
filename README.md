# Campo Minado (Console)

Implementação do clássico jogo **Campo Minado** em Java, executado no terminal. O projeto separa o domínio do jogo (modelo), a interface de console (visão) e as exceções de controle de fluxo, e conta com testes unitários em JUnit 5.

## Sumário

1. [Visão geral](#visão-geral)
2. [Requisitos](#requisitos)
3. [Estrutura do projeto](#estrutura-do-projeto)
4. [Como executar](#como-executar)
5. [Regras do jogo](#regras-do-jogo)
6. [Arquitetura e classes](#arquitetura-e-classes)
7. [Representação visual no console](#representação-visual-no-console)
8. [Testes](#testes)
9. [Pontos de atenção e melhorias](#pontos-de-atenção-e-melhorias)

---

## Visão geral

O jogo cria um tabuleiro com N linhas × M colunas e sorteia uma quantidade de minas entre os campos. O jogador abre ou marca campos até:

- **vencer**: todos os campos sem mina foram abertos e todos os campos minados estão marcados; ou
- **perder**: abriu um campo minado (explosão).

Configuração padrão em `Aplicacao`: tabuleiro **6 × 6 com 6 minas**.

## Requisitos

| Item | Versão |
|------|--------|
| JDK | **25 ou superior** (a classe `Aplicacao` usa `static void main()` sem parâmetros, recurso de *instance main methods* finalizado no Java 25) |
| JUnit | 5 (Jupiter) |
| Terminal | Com suporte a **códigos de escape ANSI** (para as cores) |

> No Windows, use Windows Terminal, PowerShell moderno ou o terminal integrado do IntelliJ. O `cmd.exe` antigo pode exibir os códigos de cor como texto.

## Estrutura do projeto

```
campo-minado
├── src
│   └── br/com/cod3r/campoMinado
│       ├── Aplicacao.java              # Ponto de entrada
│       ├── colors
│       │   └── Colors.java             # Constantes de cores ANSI
│       ├── excecao
│       │   ├── ExplosaoException.java  # Lançada ao abrir um campo minado
│       │   └── SairException.java      # Usada para encerrar o jogo
│       ├── modelo
│       │   ├── Campo.java              # Uma célula do tabuleiro
│       │   └── Tabuleiro.java          # Conjunto de campos + regras globais
│       └── visao
│           └── TabuleiroConsole.java   # Interação com o usuário via terminal
└── test
    └── br/com/cod3r/campoMinado/modelo
        ├── CampoTest.java
        └── TabuleiroTest.java
```

## Como executar

### Pelo IntelliJ IDEA

1. Abra o projeto e configure o SDK para o JDK 25+.
2. Execute a classe `br.com.cod3r.campoMinado.Aplicacao`.

### Pela linha de comando

```bash
# Compilar
javac -d out $(find src -name "*.java")

# Executar
java -cp out br.com.cod3r.campoMinado.Aplicacao
```

No PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out br.com.cod3r.campoMinado.Aplicacao
```

## Regras do jogo

- Cada **campo** pode estar *fechado*, *aberto*, *marcado* e/ou *minado*.
- **Abrir** um campo:
  - Se já estiver aberto ou marcado, nada acontece.
  - Se estiver minado, ocorre uma **explosão** e o jogo termina (todos os campos são revelados).
  - Se nenhum vizinho tiver mina, os vizinhos são **abertos automaticamente** (em cascata).
  - Caso contrário, o campo mostra o número de minas na vizinhança.
- **Marcar** um campo (bandeira) indica suspeita de mina. Só é possível marcar/desmarcar campos fechados, e campos marcados **não podem ser abertos**.
- **Vitória**: para todo campo, vale uma das condições:
  - não está minado e está aberto; ou
  - está minado e está marcado.
- **Vizinhança**: são vizinhos os até 8 campos ao redor (horizontal, vertical e diagonal).

## Arquitetura e classes

### `Aplicacao`

Ponto de entrada. Cria um `Tabuleiro(6, 6, 6)` e entrega a um `TabuleiroConsole`, que conduz o jogo.

### `modelo.Campo`

Representa uma célula do tabuleiro.

**Atributos**

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `linha`, `coluna` | `int` (final) | Posição no tabuleiro |
| `aberto` | `boolean` | Se o campo já foi revelado |
| `minado` | `boolean` | Se há mina no campo |
| `marcado` | `boolean` | Se o jogador marcou o campo |
| `vizinhos` | `List<Campo>` | Campos adjacentes |

**Métodos principais**

| Método | Visibilidade | Descrição |
|--------|--------------|-----------|
| `adicionarVizinho(Campo)` | pacote | Adiciona o campo à lista de vizinhos se for adjacente. Retorna `true` se adicionou. |
| `alternarMarcacao()` | pacote | Inverte `marcado`, apenas se o campo estiver fechado. |
| `abrir()` | pacote | Abre o campo. Retorna `true` se abriu, `false` se já estava aberto ou marcado. Lança `ExplosaoException` se minado. Abre os vizinhos recursivamente se a vizinhança for segura. |
| `vizinhacaSegura()` | pacote | `true` se nenhum vizinho tem mina. |
| `minar()` | pacote | Coloca uma mina no campo. |
| `minasNaVizinhaca()` | pacote | Quantidade de vizinhos minados. |
| `objetivoAlcancado()` | pacote | `true` se o campo está "resolvido" (veja [Regras](#regras-do-jogo)). |
| `reiniciar()` | pacote | Volta aberto, marcado e minado para `false`. |
| `isAberto()`, `isMarcado()`, `isMinado()` | público | Consultas de estado. |
| `getLinha()`, `getColuna()` | público | Posição do campo. |
| `toString()` | público | Representação colorida do campo (veja [abaixo](#representação-visual-no-console)). |

**Lógica de vizinhança (`adicionarVizinho`)**

Calcula `deltaLinha` e `deltaColuna` (distância absoluta entre os campos):

| Situação | `deltaGeral` (soma dos deltas) | Resultado |
|----------|-------------------------------|-----------|
| Mesma linha ou mesma coluna (adjacente) | `1` | Vizinho |
| Diagonal (linha **e** coluna diferentes) | `2` | Vizinho |
| Qualquer outro caso (incluindo o próprio campo e campos distantes) | `0` ou `>2` | Não é vizinho |

### `modelo.Tabuleiro`

Gerencia a grade de campos e as regras globais.

**Construtor** `Tabuleiro(linhas, colunas, minas)`: gera os campos, associa os vizinhos e sorteia as minas.

| Método | Descrição |
|--------|-----------|
| `abrir(linha, coluna)` | Abre o campo na posição. Se ocorrer `ExplosaoException`, **revela todos os campos** e relança a exceção. |
| `alterarMarcacao(linha, coluna)` | Marca/desmarca o campo na posição. |
| `objetivoAlcancado()` | `true` se todos os campos satisfazem o objetivo (vitória). |
| `reiniciar()` | Reinicia todos os campos e sorteia novas minas. |
| `toString()` | Monta o tabuleiro em texto, com cabeçalho de colunas e índice de linhas. |

Métodos internos: `gerarCampos()`, `associarVizinhos()` e `sortearMinas()` (sorteia posições aleatórias até atingir o número de minas desejado).

### `visao.TabuleiroConsole`

Camada de apresentação: lê comandos do usuário, chama `abrir`/`alterarMarcacao` no `Tabuleiro`, imprime o tabuleiro e trata `ExplosaoException` (derrota) e `SairException` (encerrar o jogo).

### `excecao`

| Classe | Tipo | Uso |
|--------|------|-----|
| `ExplosaoException` | `RuntimeException` | Sinaliza que o jogador abriu um campo minado. |
| `SairException` | `RuntimeException` | Sinaliza que o jogador quer sair do jogo. |

### `colors.Colors`

Constantes com códigos ANSI para colorir o terminal.

| Constante | Cor |
|-----------|-----|
| `RESET` | Restaura a cor padrão |
| `CINZA_ESCURO` | Cinza escuro |
| `AMARELO` | Amarelo |
| `VERMELHO` | Vermelho |
| `ROXO` | Roxo |
| `CIANO` | Ciano |

## Representação visual no console

Cada campo é exibido por `Campo.toString()`:

| Estado | Símbolo | Cor |
|--------|---------|-----|
| Marcado | `X` | Amarelo |
| Aberto e minado | `*` | Vermelho |
| Aberto com minas na vizinhança | Número de minas (ex.: `1`) | Padrão |
| Aberto sem minas na vizinhança | ` ` (espaço) | Ciano |
| Fechado | `?` | Cinza escuro |

Exemplo de tabuleiro 6 × 6 (sem as cores):

```
   0  1  2  3  4  5
0  ?  ?  ?  ?  ?  ?
1  ?  1     1  ?  ?
2  ?  1     1  ?  ?
3  ?  X  ?  ?  ?  ?
4  ?  ?  ?  ?  ?  ?
5  ?  ?  ?  ?  ?  ?
```

## Testes

Os testes usam **JUnit 5** e ficam em `test/br/com/cod3r/campoMinado/modelo`. Para executar, use o botão de *Run* do IntelliJ na pasta `test` ou o seu gerenciador de build.

### `CampoTest`

Antes de cada teste, `@BeforeEach` cria um `Campo(3, 3)`.

| Teste | O que verifica |
|-------|----------------|
| `testVizinhoRealDistancia1` | Campo (3,2) é vizinho (adjacente na mesma linha). |
| `testVizinhoRealDistancia2` | Campo (2,2) é vizinho (diagonal). |
| `testNaoEVizinho` | Campo (5,3) **não** é vizinho. |
| `testeValorPadraoMarcado` | Campo nasce desmarcado. |
| `testeAlternarMarcacao` | `alternarMarcacao()` marca o campo. |
| `testeAbrirNaoMinadoNaoMarcado` | Abrir campo normal retorna `true`. |
| `testeAbrirNaoMinadoMarcado` | Campo marcado não abre (`false`). |
| `testeAbrioMinadoMarcado` | Campo minado e marcado não abre (`false`). |
| `testeAbrioMinadoNaoMarcado` | Campo minado e desmarcado lança `ExplosaoException`. |
| `testeAbrirComVizinho` | Abertura em cascata para os vizinhos. |
| `testeCampoMarcado` | `toString()` retorna `X` amarelo. |
| `testeCampoMinado` | `toString()` retorna `*` vermelho após explosão. |
| `testeCampoAberto` | `toString()` retorna espaço ciano para campo aberto sem minas ao redor. |
| `testeCampoAbriuVizinhoMinado` | `toString()` retorna `"1"` quando há uma mina vizinha. |

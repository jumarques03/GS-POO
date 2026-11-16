# Territory Predictive Insights - Clima Local (Global Solution 2º/2026 - POO)

Bem-vindo ao repositório do **Territory Predictive Insights**, um sistema de monitoramento territorial feito em Java para responder a uma pergunta simples: *qual problema vai atingir o nosso território nos próximos dias e o que podemos fazer hoje para reduzir o impacto?* No recorte escolhido, **Clima Local**, o problema é a chuva intensa e o risco de alagamento em pontos vulneráveis, como uma passagem subterrânea na Zona Sul de São Paulo (SP).

O projeto foi desenvolvido como Global Solution do 2º semestre de 2026 da disciplina de Programação Orientada a Objetos do curso de Ciência da Computação da FIAP, com o objetivo de aplicar na prática os conceitos da disciplina: domínio orientado a objetos, exceções customizadas, persistência com Spring Data JPA, padrão criacional e testes unitários.

O sistema roda no console. Ele lê sensores simulados (chuva, nível do córrego e umidade do solo), grava cada leitura no banco de dados, usa o histórico para prever o que pode acontecer e emite alertas que explicam o motivo. O território é descrito apenas por região e equipamento, sem nenhum dado de pessoas reais.

Este README reúne o público beneficiado, as decisões do projeto, a justificativa do padrão criacional, a reflexão crítica, o uso de IA e as instruções para rodar.

---

# Links da Entrega

| Item | Link |
| --- | --- |
| 💻 **Repositório** | https://github.com/jumarques03/GS-POO |
| ▶️ **Como rodar** | [Como Rodar o Projeto](#como-rodar-o-projeto) |
| 🏭 **Padrão criacional (Factory)** | [Padrão Criacional e Justificativa](#padrão-criacional-factory-e-justificativa) |
| 🧠 **Reflexão crítica** | [Reflexão Crítica](#reflexão-crítica) |
| 🤖 **Uso de IA** | [Uso de IA](#uso-de-ia) |

---

# Integrantes

- Carolina Monteiro Bernardo - RM 564651
- Isabelle Dias Belini - RM 566464
- Júlia Souza Marques - RM 565010

---

# O Problema

Em áreas sujeitas a alagamento, a chuva forte costuma pegar a comunidade de surpresa. As pessoas se informam pela previsão do tempo, mas ela é geral para a cidade ou a região, e também olhando o céu, o córrego e a rua. O que não existe é algo que meça e guarde o histórico de um trecho específico, como uma passagem subterrânea. Isso traz limitações:

- Previsão genérica: a previsão do tempo não diz como está aquele ponto em particular;
- Falta de histórico por trecho: nada registra as leituras de um trecho ao longo do tempo, então não dá para ver se a situação está piorando ou melhorando;
- Aviso tardio: quando o problema fica visível na rua, já é difícil agir;
- Decisões sem justificativa: não fica claro por que um aviso foi (ou não foi) emitido.

Dessa forma, é preciso uma solução que registre as leituras, use esse histórico para antecipar o risco e explique cada alerta, deixando a decisão final com quem conhece o território.

---

# Público Beneficiado

**Quem sofre com o problema:** moradores de áreas sujeitas a alagamento, principalmente quem depende de pontos vulneráveis como uma passagem subterrânea para ir e voltar do trabalho ou da escola.

**Quem usa o sistema:** a equipe comunitária ou a defesa civil local, que precisa saber com antecedência onde a chuva pode virar problema.

**Decisão que o sistema apoia:** o que fazer hoje para reduzir o impacto, como avisar os moradores, sinalizar uma rota alternativa, interditar a passagem ou enviar uma equipe de campo ao ponto. O alerta não decide nada sozinho: ele mostra qual sensor passou de qual limite e deixa a escolha com a pessoa.

---

# Proposta de Solução

O sistema transforma leituras de sensores em alertas explicados, seguindo um fluxo de cinco passos:

1. **Ler:** os sensores simulados geram valores (`Random`, com uma casa decimal) e cada leitura é validada. Valor fora da faixa lança `DadosInvalidosException`; sensor inativo lança `FonteIndisponivelException`.
2. **Guardar:** cada medição válida é gravada no banco (H2 em arquivo), então o histórico sobrevive quando o programa é fechado.
3. **Prever:** o serviço busca as leituras mais recentes do sensor e a `FabricaPrevisao` cria a estratégia escolhida (média móvel ou comparação com o período anterior).
4. **Alertar:** a previsão é comparada com os limiares do sensor, do maior para o menor, e o alerta mais grave que se aplica (ATENÇÃO, ALERTA ou CRÍTICO) é gravado com sensor, valor previsto, limite ultrapassado e data e hora.
5. **Consultar:** o menu mostra o histórico de medições por sensor, os alertas emitidos, os dados do território e o status completo.

Com poucas leituras, o sistema não prevê: avisa que o histórico é insuficiente em vez de emitir um alerta pouco confiável.

---

# Funcionalidades Implementadas (Status Atual)

O menu principal usa um loop com `Scanner`, dentro do Spring Boot (`CommandLineRunner`):

```text
1 - Verificar sensores
2 - Rodar previsoes
3 - Ver historico
4 - Gerenciar dados do territorio
5 - Status completo
0 - Sair
```

## Verificar Sensores ✅
- Lê os três sensores e grava as medições no banco.
- Se um sensor estiver com falha, mostra a mensagem e continua com os outros.

## Rodar Previsões ✅
- O usuário escolhe a estratégia (média móvel ou comparação com o período anterior).
- Para cada sensor, mostra o alerta com o motivo ou "sem alerta".
- Sem leituras suficientes, mostra a mensagem de histórico insuficiente.

## Ver Histórico ✅
- Medições de um sensor escolhido, da mais antiga para a mais recente.
- Alertas já emitidos, com data, nível e mensagem.

## Gerenciar Dados do Território ✅
- Ver a população exposta e os limiares de cada sensor.
- Alterar a população exposta e os limiares (com validação).
- Ativar e desativar um sensor, para simular uma falha.

## Status Completo ✅
- Status do ponto vulnerável (OPERANDO ou COM FALHA), situação de cada sensor, população exposta, limiares e total de alertas emitidos.

---

# Decisões da Equipe

- **Recorte:** Clima Local. Os três sensores se complementam (chuva, córrego e solo) e os limiares são fáceis de explicar.
- **Persistência:** Spring Boot com Spring Data JPA e H2 **em arquivo** (`./data/tpi`). O enunciado sugere H2 em memória, mas nesse modo os dados somem quando o programa fecha, e a atividade exige que o histórico sobreviva ao reiniciar. O arquivo é criado sozinho na primeira execução e a pasta `data/` não vai para o Git.
- **Interface:** menu de console, sem API REST e sem interface gráfica.
- **Lógica fora do menu:** a regra de alerta fica em `ServicoMonitoramento`, que recebe os repositórios pelo construtor. Assim ela pode ser testada com Mockito, sem banco e sem digitar nada.
- **Sem `@SpringBootTest`:** os testes não sobem o contexto do Spring, porque isso abriria o menu e ficaria esperando digitação.
- **Território:** o ponto vulnerável de exemplo é "Passagem subterrânea - Zona Sul" (Zona Sul de São Paulo, SP) (`PontoVulneravel`, que herda de `ComponenteTerritorial`). O status é OPERANDO se todos os sensores funcionam e COM FALHA se algum não funciona.
- **Dados do território em memória:** a população exposta e os limiares ficam em `DadosTerritorio` e voltam ao valor inicial a cada execução. Só medições e alertas vão para o banco.

### Sensores e limiares

Cada sensor implementa a interface `Sensor` diretamente, como o enunciado pede, e por isso há um pouco de código repetido entre eles. Os limiares ficam na unidade de cada sensor e podem ser alterados pelo menu, respeitando `0 < atenção < alerta < crítico`.

| Sensor | Tipo | Unidade | Faixa válida | ATENÇÃO | ALERTA | CRÍTICO |
| --- | --- | --- | --- | --- | --- | --- |
| PLUV-01 | Pluviômetro | mm/h | 0 a 150 | 20 | 50 | 80 |
| CORR-01 | Nível do córrego | cm | 0 a 400 | 150 | 250 | 320 |
| UMID-01 | Umidade do solo | % | 0 a 100 | 70 | 85 | 95 |

Os limiares são valores de exemplo, escolhidos como ponto de partida, e não foram calibrados com dados reais.

### Estratégias de previsão

| Estratégia | Como calcula | Leituras mínimas |
| --- | --- | --- |
| Média móvel (`PrevisaoMediaMovel`) | Média das 5 leituras mais recentes do sensor | 5 |
| Comparação com o período anterior (`PrevisaoComparacao`) | Média das 5 mais recentes somada à diferença para a média das 5 anteriores (a tendência). Se o resultado der negativo, vira 0 | 10 |

As duas herdam de `EstrategiaPrevisao`, que guarda o nome, o mínimo de leituras, a validação do histórico e o cálculo da média, e cada uma sobrescreve `prever()`.

### Exceções customizadas

| Exceção | Quando é lançada |
| --- | --- |
| `DadosInvalidosException` | Medição fora da faixa (ou NaN), sensor nulo, população negativa, limiares inválidos, tipo de previsão desconhecido e histórico insuficiente |
| `FonteIndisponivelException` | Sensor inativo ou com falha |

O menu captura as duas com mensagem clara para o usuário, e nenhum `catch` fica vazio.

---

# Padrão Criacional (Factory) e Justificativa

O padrão escolhido foi o **Factory**, na classe `FabricaPrevisao`. O método `criar(tipo)` recebe o tipo de previsão (`MEDIA_MOVEL` ou `COMPARACAO`) e devolve a estratégia correspondente. Por dentro existe um `Map` que liga cada tipo ao construtor da classe, então não há `if tipo == ...` nem `instanceof`. Se o tipo não existe, a fábrica lança `DadosInvalidosException` listando as opções válidas.

**Por que ali:** a previsão é o único ponto do sistema em que o objeto criado muda conforme uma escolha feita durante a execução (o usuário escolhe a estratégia no menu). O `ServicoMonitoramento` chama `FabricaPrevisao.criar(...)` e depois usa o resultado como `EstrategiaPrevisao`, sem conhecer as classes concretas. Para criar uma terceira previsão, basta escrever a classe e acrescentar uma linha no mapa, sem mexer no serviço nem no menu.

**Por que não em outro lugar:**
- Sensores, `Medicao` e `Alerta` são criados direto com `new`, porque ali não existe escolha. Uma fábrica só adicionaria uma classe sem resolver nada.
- **Singleton** (`ConfiguracaoSistema`) não se encaixa: os limiares são por sensor e podem ser alterados, então ficam em um `DadosTerritorio` comum, e nada no sistema precisa de uma instância única.
- **Builder** também não: não há um objeto grande, como um relatório, para montar passo a passo.

---

# Testes

Os testes automatizados usam JUnit 5 e Mockito, seguem o padrão AAA (Arrange, Act, Assert) e não dependem de banco nem de digitação no console. No total são **37 testes em 10 classes**.

| Classe de teste | O que verifica |
| --- | --- |
| `MedicaoTest` | Criação válida e `DadosInvalidosException` para valor negativo, acima da faixa e sensor nulo |
| `PluviometroSensorTest` e `SensoresTest` | Leituras dentro da faixa e `FonteIndisponivelException` com sensor inativo |
| `PrevisaoMediaMovelTest` | Cálculo da média, uso só das 5 leituras mais recentes e histórico insuficiente |
| `PrevisaoComparacaoTest` | Tendência de alta, resultado nunca negativo e histórico insuficiente |
| `FabricaPrevisaoTest` | Criação de cada estratégia e erro para tipo desconhecido |
| `LimiaresTest` e `DadosTerritorioTest` | Validação dos limiares e da população, e guarda de limiares por sensor |
| `PontoVulneravelTest` | Status OPERANDO e COM FALHA |
| `ServicoMonitoramentoTest` | Lógica de alerta com `repository` mockado (Mockito): níveis CRÍTICO e ATENÇÃO, sem alerta, histórico insuficiente, estratégia de comparação e gravação da medição |

Também foi feito um roteiro de testes manuais no menu:

| Cenário Testado | Resultado Esperado | Resultado Obtido | Status |
| :--- | :--- | :--- | :--- |
| **Verificar sensores** (opção 1, várias vezes) | Registrar uma leitura de cada sensor e gravar no banco. | As leituras foram exibidas e gravadas. | ✅ Passou |
| **Previsão por média móvel** (opção 2, estratégia 1) | Mostrar o alerta com o motivo ou "sem alerta". | O alerta foi exibido com sensor, valor previsto e limite. | ✅ Passou |
| **Previsão por comparação** (opção 2, estratégia 2, com 10 leituras ou mais) | Calcular a previsão com a tendência e mostrar o resultado. | A previsão foi calculada e exibida. | ✅ Passou |
| **Histórico insuficiente** (sensor com poucas leituras) | Avisar que faltam leituras, sem emitir alerta. | A mensagem de histórico insuficiente apareceu e o programa continuou. | ✅ Passou |
| **Histórico por sensor e alertas** (opção 3) | Listar as medições do sensor e os alertas emitidos. | As duas listas foram exibidas. | ✅ Passou |
| **Entrada inválida** (letra e opção inexistente no menu) | Mostrar mensagem clara sem encerrar o programa. | O programa mostrou a mensagem e voltou ao menu. | ✅ Passou |
| **População negativa e limiares fora de ordem** (opção 4) | Rejeitar o valor com a mensagem da `DadosInvalidosException`. | Os valores foram recusados e os dados anteriores foram mantidos. | ✅ Passou |
| **Simular falha de sensor** (opção 4, desativar um sensor, depois opções 1 e 5) | Mostrar a falha, manter os outros sensores e exibir o status COM FALHA. | A falha foi exibida, os outros sensores continuaram e o status mudou. | ✅ Passou |
| **Reativar o sensor** (opção 4 e opção 5) | O status voltar para OPERANDO. | O status voltou para OPERANDO. | ✅ Passou |
| **Persistência** (fechar o programa e abrir de novo, opção 3) | Manter as medições gravadas na execução anterior. | O histórico continuou no banco. | ✅ Passou |

---

# Reflexão Crítica

**Quando o sistema deve se calar?** Sempre que o histórico é curto demais para sustentar uma previsão. Com uma ou duas leituras, a média móvel é praticamente a própria leitura, e um alerta baseado nisso seria um chute com cara de resultado. Por isso foi definido um mínimo: 5 leituras para a média móvel e 10 para a comparação (duas janelas de 5). Abaixo disso o sistema não prevê nem emite alerta, só informa que o histórico é insuficiente. Mesmo assim, 5 leituras ainda é pouco, e esse número foi uma escolha minha, não uma regra validada.

**O que os dados não representam?** Os valores são simulados com `Random`, então não têm relação com a chuva de verdade: não há tendência nem ligação entre os sensores. Na vida real, chuva forte, nível do córrego e solo saturado andam juntos, mas aqui cada sensor sorteia o seu valor sozinho. Além disso, um pluviômetro, um sensor de córrego e um de umidade do solo dizem pouco sobre o bairro inteiro, só sobre aquele ponto. Os limiares são exemplos que escolhi, sem calibração com dados de uma defesa civil, e uma média simples não enxerga uma tempestade que começa de repente.

**Por que o alerta apoia a decisão em vez de decidir?** Porque quem conhece o território é a pessoa. O alerta mostra qual sensor, qual valor previsto e qual limite foi ultrapassado, e com isso quem decide (avisar moradores, fechar a passagem, enviar uma equipe) consegue avaliar se faz sentido, comparando com o que vê na rua. O sistema pode errar por causa de um sensor com defeito ou de uma previsão simples demais, e se ele decidisse sozinho o erro viraria ação sem ninguém para questionar. Por isso a mensagem explica o motivo do alerta em vez de apenas mandar fazer alguma coisa.

---

# Uso de IA

Eu escrevi o código do projeto. Usei inteligência artificial (o **Claude**, da Anthropic, pelo chat e pelo **Claude Code**) como apoio, e não para escrever o código: a escolha do recorte, do padrão criacional, dos sensores e dos limiares foi minha, assim como a implementação das classes. A IA entrou depois, para revisar o que eu já tinha escrito e ajudar a encontrar bugs.

| Onde usei | Como usei |
| --- | --- |
| **Estrutura do projeto** | Organizei o enunciado em etapas e discuti com a IA os pacotes, as classes e o fluxo do sistema (sensor, medição, banco, previsão, alerta e menu). |
| **Revisão do código que eu escrevi** | Depois de escrever o código, usei a IA para revisá-lo e apontar erros que eu não tinha percebido. |
| **Encontrar e corrigir bugs** | A IA me ajudou a achar problemas no meu código, como o valor do alerta que aparecia com casas decimais demais (resolvido com arredondamento na classe base das previsões) e a falta de um jeito de simular falha de sensor no menu. |

**Como revisei:**
- Li cada ponto levantado pela IA e só alterei o código depois de entender o problema;
- Rodei os 37 testes (`clean test`) e o roteiro completo no menu;
- Quebrei um teste de propósito para confirmar que ele realmente fica vermelho;
- Testei a entrega clonando o repositório em uma pasta nova.

A IA foi apoio nas tomadas de decisão sobre a estrutura do projeto, na revisão e na busca de bugs.

---

# Stack Utilizada

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 17 (testado também com JDK 25) |
| Framework | Spring Boot 4.1.1 |
| Persistência | Spring Data JPA com Hibernate |
| Banco de dados | H2 em arquivo (`./data/tpi`) |
| Testes | JUnit 5 e Mockito |
| Build | Maven, com Maven Wrapper (`mvnw`) |
| Interface | Console (`Scanner` dentro de um `CommandLineRunner`) |

---

# Estrutura do Projeto

```text
src/main/java/br/com/fiap/territorio/
├── SistemaMonitoramento.java      (classe principal e menu)
│
├── dominio/
│   ├── ComponenteTerritorial.java (abstrata)
│   ├── PontoVulneravel.java
│   ├── Sensor.java                (interface)
│   ├── PluviometroSensor.java
│   ├── NivelCorregoSensor.java
│   ├── UmidadeSoloSensor.java
│   ├── DadosTerritorio.java
│   ├── Limiares.java
│   ├── EstrategiaPrevisao.java    (abstrata)
│   ├── PrevisaoMediaMovel.java
│   ├── PrevisaoComparacao.java
│   ├── Medicao.java
│   ├── Alerta.java
│   └── NivelAlerta.java
│
├── excecao/
│   ├── DadosInvalidosException.java
│   └── FonteIndisponivelException.java
│
├── persistencia/
│   ├── MedicaoRepository.java
│   └── AlertaRepository.java
│
└── servico/
    ├── ServicoMonitoramento.java
    └── FabricaPrevisao.java

src/test/java/br/com/fiap/territorio/   (10 classes de teste)
src/main/resources/application.properties
mvnw, mvnw.cmd, pom.xml
```

---

# Como Rodar o Projeto

## Pré-requisitos

- JDK 17 ou superior (testado com o JDK 25).
- Conexão com a internet na primeira execução: o Maven Wrapper baixa o Maven e as dependências sozinho. Não é preciso instalar o Maven.

## Clonar o Repositório

```bash
git clone https://github.com/jumarques03/GS-POO.git
cd GS-POO
```

## Rodar os Testes

Devem passar os 37 testes.

```bash
# Windows (PowerShell)
.\mvnw.cmd clean test

# Linux ou Mac
./mvnw clean test
```

## Executar o Programa

```bash
# Windows (PowerShell)
.\mvnw.cmd spring-boot:run

# Linux ou Mac
./mvnw spring-boot:run
```

O menu abre no próprio terminal. Para ver o sistema funcionando por completo, rode a opção 1 pelo menos 10 vezes antes da opção 2, porque as previsões precisam de 5 leituras (média móvel) ou 10 (comparação) de cada sensor.

## Banco de Dados

O banco é o H2 em arquivo, em `./data/tpi`. Ele é criado sozinho na primeira execução e os dados continuam lá quando o programa é fechado e aberto de novo. Para começar do zero, basta apagar a pasta `data/`.

> Quando o programa roda, o Java e o Maven mostram alguns avisos (`WARNING`) no terminal. Eles não afetam o funcionamento.

---

*Projeto desenvolvido para a Global Solution 2º/2026 de POO - Ciência da Computação FIAP.*
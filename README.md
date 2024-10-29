# Matching Order System
## Requerimentos  
Apache Maven 3.8.7
Maven home: /usr/share/maven
Java version: 17.0.12, vendor: Debian, runtime: /usr/lib/jvm/java-17-openjdk-amd64

Como rodar:
```
mvn clean compile

mvn exec:java -Dexec.mainClass="br.ufscar.dc.internship.App"
```
## Objetivo  
Desenhar um projeto de software utilizando formas eficientes de estruturação de dados, algoritmos e engenharia de software. Os algoritmos devem ter, se possível ordem N. O software pode ser escrito de forma estrutural ou orientada à objetos.

Deve ser possível inserir ordens com as informações:
- Tipo: Limit ou Market
- Side: Buy ou Sell
- Price: Apenas quando ordem for Limit
- Quantidade

Limit orders com preços que gerariam trades podem ser ignoradas ou preenchidas, porém o
comportamento escolhido deve ser justificado.
Quando um trade for realizado, deve-se mostrar na saída:  
"Trade, price: <preço do trade>, <número de shares>"

Exemplo de entrada e saída:  
```
>>> limit buy 10 100
>>> limit sell 20 100
>>> limit sell 20 200
>>> market buy 150
Trade, price: 20, qty: 150
>>> market buy 200
Trade, price: 20, qty: 150
>>> market sell 200
Trade, price: 10, qty: 100
```

**Bônus**  
1. Implementar uma função/método para visualização do livro;

2. Soluções que respeitem a ordem de chegada das ordens. No exemplo anterior, isso significa que a primeira ordem de venda com quantidade 100 deve ser preenchida antes da segunda, com quantidade de 200;  

3. Implementação de cancelamento. Uma ordem cancelada, deve ser retirada do match engine. Exemplo:
```
>>> limit buy 10 100
Order created: buy 100 @ 10 identificador_1
>>> cancel order identificador_1
Order cancelled
```

4. Implementação de alteração de ordem. Uma ordem alterada tem seu preço, quantidade ou ambos modificados. Caso tenha implementado o primeiro item bônus, lembre-se que a ordem com alteração de preço deve ser recolocada para a faixa de preço adequado. Em um livro hipotético:
```
===============================
          BUY              SELL     
     200 @ 10        100 @ 10.5
   100 @ 9.99
===============================
```
Ao alterar a primeira ordem de compra (200 quantidades ao preço R$ 10,00) para um preço de 9.98, devemos ter a seguinte configuração no livro:
```
===============================
          BUY              SELL     
   200 @ 9.99        100 @ 10.5
   100 @ 9.98
===============================
```
Ou seja, perdeu prioridade na fila.  
5. Uma ordem pegged é um tipo de ordem que   segue um determinado preço de referência. **Bid** é o melhor preço de compra disponível no livro de ofertas. **Offer** é o melhor preço de venda. Por exemplo, uma ordem **peg to the bid** irá acompanhar o preço do bid, ou seja, tem sempre o preço atualizado pelo match engine para acompanhar o melhor preço de compra conforme exemplo abaixo:
```
>>> print book
===============================
          BUY              SELL     
     200 @ 10        100 @ 10.5
   100 @ 9.99
===============================

>>> peg bid buy 150

===============================
          BUY              SELL     
     200 @ 10        100 @ 10.5
     150 @ 10
   100 @ 9.98
===============================

>>> limit buy 10.1 300

===============================
          BUY              SELL     
   150 @ 10.1        100 @ 10.5
   300 @ 10.1
     200 @ 10
   100 @ 9.99
===============================
```

O mesmo funciona para o peg to the offer.  

## Suposições Iniciais  
1. Uma ordem Limit só será adicionada no Livro depois de tentar realizar os matches possíveis;

2. Uma ordem Market deverá tentar ser preenchida imediatamente, caso não seja possível completar a ordem, ela não deverá ser armazenada no Livro;

3. Uma ordem pegged também é um tipo de ordem assim como Limit e Market;

4. Ordens pegged não precisam necessariamente serem armazenadas no livro, pode ser um armazenamento abstrato, no qual mantemos as ordens pegged separadas do Livro
porém ao realizar os matches, tentamos fazer o match primeiro com as ordens pegged já que elas sempre vão acompanhar ou o melhor valor de compra ou o melhor valor de venda. Isso vai permitir que evitamos uma complexidade O(n²) no pior caso (onde todas as ordens são pegged e uma com preço melhor é inserida);

5. Não precisamos puxar a lista de todos os preços disponíveis, apenas os N melhores. Pode ocorrer que deixemos de pegar preços que gerariam matches, porém não precisariamos iterar por todos os níveis de preço caso a quantidade da ordem entrante seja suficiente. Isso também evita que preços não tão comuns influenciem diretamente no mercado.

## Ideias Iniciais  
Enquanto eu pesquisava sobre o problema, a primeira ideia que me veio à cabeça foi utilizando duas Priority Queues, uma para as ordens de compra e outra para as ordens de venda. Na minha cabeça seria uma implementação boa onde eu precisaria apenas ordenar primeiro por preço e caso fosse igual, por tempo de entrada.

Porém, eu encontrei com dois artigos que mudaram a forma como eu olhava para o problema.

O primeiro foi uma publicação de [Amitava Biswas, Designing Low Latency High Performance Order Matching Engine](https://medium.com/@amitava.webwork/designing-low-latency-high-performance-order-matching-engine-a07bd58594f4), no qual fui apresentado o conceito de "Níveis de Preço", dessa forma, ao invés de ordenar tudo em uma única PriorityQueue. Primeiro eu armazenava os níveis de preço em uma estrutura de dados de busca eficiente, como uma árvore binária ou um HashMap e depois, em cada nível de preço eu armazenava o objeto em si.

A segunda publicação que influenciou foi uma implementação mais simples de [Harshil Jani, Building Stock Market Engine from scratch in Rust (I)](https://medium.com/@harshiljani2002/building-stock-market-engine-from-scratch-in-rust-i-9be7c110e137).

Decidi então que eu iria me basear na implementação de Harshil Jani, por que apesar de ser menos eficiente, ela estava mais compatível com minhas habilidades no momento. Porém, comentarei sobre a implementação de Amitava Biswas e como eu poderia melhorar meu sistema na seção de Otimizações.

## Implementação

A classe Order, representa um objeto do tipo ordem quando instanciada.

A classe BookOrder possui as seguintes variáveis:
- Um HashMap para conseguir buscar os objetos usando um identificador;
- Dois TreeMap para armazenar os níveis de preço das ordens de compra e de venda;
- Dois vetores para as ordens pegged de compra e venda;
- Duas variáveis do tipo BigDecimal para armazenar os melhores valores de compra e venda. Decidi fazer dessa forma para não precisar ficar buscando toda hora o melhor preço no TreeMap, o que levaria O(log n) a todo momento;

A classe Trade é apenas um wrapper para um printar o trade na saída.

A MatchingEngine é a classe que recebe os inputs e chama o Livro de Ordem para processar as ordens.

## Possíveis Otimizações

Na publicação de Amitava Biswas, eu notei duas coisas:

A primeira é a utilização de apenas tipos primitivos de dados. Decidi então pesquisar e descobri que o Java possui uma classe Wrapper para cada tipo primitivo de dado e que isso impacta muito a eficiência do algoritmo em uma situação real e além disso, o Java em algumas classes, automaticamente empacota o tipo primitivo em seu respectivo wrapper quando utiliza esses dados em suas estruturas built-in, dessa forma é ideial a utilização de bibliotecas próprias para manipulação de tipos primitivos.

A segunda coisa que notei foi a utilização de operações bit-à-bit para cálculos rápidos e eficientes, principalmente ao calcular o identificador da ordem, no qual ao invés de gerar um identificador de forma incremental ou aleatória, ele divide os bits em duas metades, uma para armazenar o índice do Nível de Preço e outra para armazenar o índice do objeto no Nível de preço. Dessa forma ele consegue gerar um índice única de forma rápida.

Amitava também não armazena as ordens nos níveis de preço em um Hard Link, ou seja, em cada nível de preço não está armazenado um objeto, mas sim uma quantidade e um identificador apenas.

Então, para otimizar essa minha implementação, seria necessário refatorar o algoritmo, utilizando alocação estática para rápida inserção e cancelamento das ordens, além de alterar algumas classes.

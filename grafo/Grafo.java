package grafo;

import java.util.ArrayList;
// import java.util.Hashtable;

public class Grafo<T>{
    private ArrayList<Vertice<T>> vertices;
    // T = String
    //private ArrayList<Aresta<Vertice<String>>>

    public Grafo(){
        this.vertices =  new ArrayList<Vertice<T>>();
    }

    public void adicionarVertice(Vertice<T> vertice){
        this.vertices.add(vertice);
    }

    public Vertice<T> getVertice(T dado){
        if(dado == null) return null;
        for(Vertice<T> vert: this.vertices){
            // Verifica se o vertice atual contém o Objeto igual ao que está sendo procurado
            if(vert.getValor().equals(dado)){
                return vert;
            }
        }
        return null;
    }

    public void adicionarAresta(Float peso, T vInicio, T vFim){
        Vertice<T> origem = this.getVertice(vInicio);
        Vertice<T> destino = this.getVertice(vFim);
        origem.adicionarDestino(new Aresta<T>(peso, destino));
    }

    public void obterCidadesVizinhas(T dado){        
        for(Vertice<T> vertice: vertices){
            // Verifica se o vertice atual contém a Cidade igual a que está sendo procurada
            if(vertice.getValor().equals(dado)){
                System.out.println("Cidade escolhida:" + vertice.getValor());
                // Imprime todas as cidades vizinhas a esse cidade
                for(Aresta<T> aresta: vertice.getDestinos()){
                    System.out.println(aresta);
                }
            }
        }
    }    

    public void obterCaminhos(T dado){
        ArrayList<Vertice<T>> marcados = new ArrayList<Vertice<T>>();
        ArrayList<Vertice<T>> fila = new ArrayList<Vertice<T>>();

        Vertice<T> atual = getVertice(dado);
        fila.add(atual);
        //Pego o primeiro vértice como ponto de partida e coloco na fila
        //Poderia escolher qualquer outro...
        //Mas note que dependendo do grafo pode ser que você não caminhe por todos os vétices

        //Enquanto houver vertice na fila...
        while (fila.size()>0){
            //Pego o próximo da fila, marco como visitado e o imprimo
            atual = fila.get(0);
            fila.remove(0);
            marcados.add(atual);
            System.out.println(atual.getValor());
            //Depois pego a lista de adjacencia do nó e se o nó adjacente ainda
            //não tiver sido visitado, o coloco na fila
            
            ArrayList<Aresta<T>> destinos = atual.getDestinos();
            Vertice<T> proximo;

            for (int i=0; i<destinos.size();i++){
                proximo = destinos.get(i).getDestino();
                if(!marcados.contains(proximo) && !fila.contains(proximo)){
                    fila.add(proximo);
                }
            }
        }
    }
    
    // Usa index em um ArrayList como predecessor de um nó
    public void calcularCaminhoMinimo(T origem, T destino){
        // imprimir na tela o caminho minimo da origem para o destino e a distancia total entre os dois
        // Obter todos os vertices para ligar a distancia e o predececor a um vertice
        ArrayList<No<Vertice<T>>> nos = new ArrayList<No<Vertice<T>>>();
        // Povoa a lista de nós com todos os Vertices do grafo de cidades
        No<Vertice<T>> noOrigem = null, noDestino = null;
        for(Vertice<T> vertice: vertices){
            if(vertice.getValor().equals(origem)){
                // Marcar quem é o primeiro
                noOrigem = new No<Vertice<T>>(vertice, No.PRED_ORIGEM);
                nos.add(noOrigem);
            } else if(vertice.getValor().equals(destino)){
                // Marcar quem é o primeiro
                noDestino = new No<Vertice<T>>(vertice);
                nos.add(noDestino);
            } else {
                nos.add(new No<Vertice<T>>(vertice));
            }
        }
        ArrayList<No<Vertice<T>>> rotulados = new ArrayList<No<Vertice<T>>>();
        No<Vertice<T>> noAtual = noOrigem;
        while(this.vertices.size() < rotulados.size() || !rotulados.contains(noDestino)){
            // Adiciona o no atual a lista de rotulados
            rotulados.add(noAtual);
            // Pega distancia do no atual
            float distanciaNoAtual = noAtual.getDistancia();
            // Pega aresta que liga aos nós de destino
            for(Aresta<T> aresta : noAtual.getValor().getDestinos()){
                // Percorre cada vertice vizinho (Destino, nós adjacentes)
                Vertice<T> vert = aresta.getDestino();
                int indexDoNoDestino = -1;
                // Obtem o index do nó de destino
                for(int index = 0; index < nos.size(); index++){
                    No<Vertice<T>> no = nos.get(index);
                    if(no.getValor().equals(vert)){
                        indexDoNoDestino = index;
                        break;
                    }
                }
                // Obtem o index do nó atual
                int indexDoNoAtual = nos.indexOf(noAtual);
                // Obtem o nó de destino do nó atual
                No<Vertice<T>> noDestinoDoNoAtual = nos.get(indexDoNoDestino);
                // Obtem a possível nova distância para o nó de destino
                float novaDistancia = distanciaNoAtual + aresta.getPeso();
                // verifica se a distância atual para o nó de destino é maior que a nova distância
                // Se for troca distância e o predecessor
                if(noDestinoDoNoAtual.getDistancia() > novaDistancia){
                    noDestinoDoNoAtual.setDistancia(novaDistancia);
                    noDestinoDoNoAtual.setPredecessor(indexDoNoAtual);
                }
            }
            No<Vertice<T>> noDeMenorDistancia = null;
            // Encontra o nó de menor distância que não foi rotulado
            for(No<Vertice<T>> no: nos){
                if(!rotulados.contains(no)){
                    // Se nenhum objeto tiver sido atribuído à variável 'noDeMenorDistancia' quer dizer que ainda não existe nenhum
                    // nó de menor distância, logo podemos atribir o nó atual para tal
                    if(noDeMenorDistancia == null){
                        noDeMenorDistancia = no;
                    } else {
                        if(no.getDistancia() == No.INFINITO){
                            continue;
                        }
                        if(noDeMenorDistancia.getDistancia() > no.getDistancia()){
                            noDeMenorDistancia = no;
                        }
                    }
                }
            }
            noAtual = noDeMenorDistancia;
        }
        imprimePredecessor(nos, noDestino, true);
    }

    private void imprimePredecessor(ArrayList<No<Vertice<T>>> nos, No<Vertice<T>> no, Boolean primeiraChamada){
        if(no.getPredecessor() != No.PRED_ORIGEM){
            imprimePredecessor(nos, nos.get(no.getPredecessor()), false);
        }
        Vertice<T> vert = no.getValor();
        // Imprime, neste caso, a cidade
        System.out.println(vert.getValor());
        if(primeiraChamada){
            System.out.println("Distancia total: " + no.getDistancia());
        }
    }

    /* v2 */

    // Usa endereço de objetos ao invés do index deles
    public void calcularCaminhoMinimo_v2(T origem, T destino){
        // imprimir na tela o caminho minimo da origem para o destino e a distancia total entre os dois
        // Obter todos os vertices para ligar a distancia e o predececor a um vertice
        ArrayList<No_v2<Vertice<T>>> nos = new ArrayList<No_v2<Vertice<T>>>();
        // Povoa a lista de nós com todos os Vertices do grafo de cidades
        No_v2<Vertice<T>> noOrigem = null, noDestino = null;
        for(Vertice<T> vertice: vertices){
            if(vertice.getValor().equals(origem)){
                // Marcar quem é o primeiro
                noOrigem = new No_v2<Vertice<T>>(vertice, true);
                nos.add(noOrigem);
            } else if(vertice.getValor().equals(destino)){
                noDestino = new No_v2<Vertice<T>>(vertice, false);
                nos.add(noDestino);
            } else {
                nos.add(new No_v2<Vertice<T>>(vertice, false));
            }
        }
        ArrayList<No_v2<Vertice<T>>> rotulados = new ArrayList<No_v2<Vertice<T>>>();
        No_v2<Vertice<T>> noAtual = noOrigem;
        while(this.vertices.size() < rotulados.size() || !rotulados.contains(noDestino)){
            // Adiciona o no atual a lista de rotulados
            rotulados.add(noAtual);
            // Pega distancia do no atual
            float distanciaNoAtual = noAtual.getDistancia();
            // Pega aresta que liga aos nós de destino
            for(Aresta<T> aresta : noAtual.getValor().getDestinos()){
                // Percorre cada vertice vizinho (Destino, nós adjacentes)
                Vertice<T> vert = aresta.getDestino();
                // Obtem o index do nó de destino
                No_v2<Vertice<T>> noDestinoDoNoAtual = null;
                // Obtem o nó de destino do nó atual
                for(No_v2<Vertice<T>> noDestinoDoAtual: nos){
                    if(noDestinoDoAtual.getValor().equals(vert)){
                        noDestinoDoNoAtual = noDestinoDoAtual;
                        break;
                    }
                }
                // Obtem a possível nova distância para o nó de destino
                float novaDistancia = distanciaNoAtual + aresta.getPeso();
                // verifica se a distância atual para o nó de destino é maior que a nova distância
                // Se for troca distância e o predecessor
                if(noDestinoDoNoAtual.getDistancia() > novaDistancia){
                    noDestinoDoNoAtual.setDistancia(novaDistancia);
                    noDestinoDoNoAtual.setPredecessor(noAtual);
                }
            }
            No_v2<Vertice<T>> noDeMenorDistancia = null;
            // Encontra o nó de menor distância que não foi rotulado
            for(No_v2<Vertice<T>> no: nos){
                if(!rotulados.contains(no)){
                    // Se nenhum objeto tiver sido atribuído à variável 'noDeMenorDistancia' quer dizer que ainda não existe nenhum
                    // nó de menor distância, logo podemos atribir o nó atual para tal
                    if(noDeMenorDistancia == null){
                        noDeMenorDistancia = no;
                    } else {
                        if(no.getDistancia() == No.INFINITO){
                            continue;
                        }
                        if(noDeMenorDistancia.getDistancia() > no.getDistancia()){
                            noDeMenorDistancia = no;
                        }
                    }
                }
            }
            noAtual = noDeMenorDistancia;
        }
        imprimePredecessor_v2(noDestino, true);
    }

    private void imprimePredecessor_v2(No_v2<Vertice<T>> no, Boolean primeiraChamada){
        if(no.getPredecessor() != null){
            imprimePredecessor_v2(no.getPredecessor(), false);
        }
        Vertice<T> vert = no.getValor();
        // Imprime, neste caso, a cidade
        System.out.println(vert.getValor());
        if(primeiraChamada){
            System.out.println("Distancia total: " + no.getDistancia());
        }
    }

    //o algoritmo implementado foi Prim
    public Grafo<T> gerarArvoreGeradoraMinima(T origem){
        Grafo<T> novoGrafo = new Grafo<T>();

        Vertice<T> novoVertice = null;

        // Verifica se objeto passado como origem já está no grafo
        for(Vertice<T> vertice: vertices){
            if(vertice.getValor().equals(origem)){
                novoVertice = vertice;
                break;
            }
        }

        // novoVertice será direfente de 'null' se o objeto estiver no grafo
        if(novoVertice != null){
            novoGrafo.adicionarVertice(novoVertice.clone());
            int tamanhoGrafoAtual = this.vertices.size();
            int tamanhonovoGrafo = novoGrafo.vertices.size();

            Boolean achouAresta;
            T origemDaMenorAresta = null, destinoDaMenorAresta = null;
            float valorDaMenorAresta = 0;
            
            float valorNovaAresta = 0;
            
            // Hashtable<Hashtable<T, T>, Float> pesoDasArestas = new Hashtable<Hashtable<T,T>, Float>();

            ArrayList<T> listaDeOrigens = new ArrayList<T>();
            ArrayList<T> listaDeDestinos = new ArrayList<T>();
            ArrayList<Float> listaDePesos = new ArrayList<Float>();

            // ArrayList<Vertice<T>> verticesDoGrafoAntigo = this.vertices;
            // Vertice<T> verticeDoGrafoAntigo = null;

            // pega menor aresta do que está ligado dentre todos os vértices do novoGrafo
            while(tamanhonovoGrafo < tamanhoGrafoAtual){
                achouAresta = false;

                // Loop para achar a menor aresta
                for(Vertice<T> vertice : novoGrafo.vertices){
                    for(Aresta<T> novaAresta : vertice.getDestinos()){
                        valorNovaAresta = novaAresta.getPeso();

                        if(valorNovaAresta == 0) continue;

                        Vertice<T> v = novoGrafo.getVertice(novaAresta.getDestino().getValor());

                        Boolean verticeDeDestinoJaEstaNoGrafoNovo = v != null;

                        if(verticeDeDestinoJaEstaNoGrafoNovo) continue;

                        if(!achouAresta){
                            origemDaMenorAresta = vertice.getValor();
                            destinoDaMenorAresta = novaAresta.getDestino().getValor();
                            valorDaMenorAresta = valorNovaAresta;
                            achouAresta = true;
                            // verticeDoGrafoAntigo = vertice;
                        } else if(valorDaMenorAresta > valorNovaAresta){ // compara antiga menor aresta com a possível nova menor aresta
                            origemDaMenorAresta = vertice.getValor();
                            destinoDaMenorAresta = novaAresta.getDestino().getValor();
                            valorDaMenorAresta = valorNovaAresta;
                            // verticeDoGrafoAntigo = vertice;
                        }
                    }
                }
                // Verificar se forma Ciclo ao adicionar aresta
                /* IDEIA: como estamos adicionando um destino quando vamos adicionar uma aresta
                          pela lógica, caso o vertice já esteja no grafo, quer dizer que ele já é destino de outro vértice
                   Por exemplo:
                        - temos 2 grafos: grafoAtual{1, 2, 3, 4, 5}, grafoNovo{1, 2}
                        - com a seguinte matriz de adjacência:
                             0;47;78;65; 0
                            47; 0;43;81;77
                            78;43; 0;25;80
                            65;81;25;0 ;47
                             0;77;80;47; 0
                        - pela lógica não é possível adicionar um vertice ao grafo no, caso ele já esteja
                        - logo caso adicionemos aquela aresta, estermos gerando um ciclo
                */
                
                // Adiciona o vértice de destino ao gráfo
                // o clone teve que ser usado para duplicar as informações, pois quando o algoritmo era rodado mais uma vez as modificação era mantidas
                // alterando em novoGrafo novo era alterado em grafoAtual também
                novoGrafo.adicionarVertice(this.getVertice(destinoDaMenorAresta).clone());

                // Adiciona a Aresta
                // Hashtable<T, T> origemDestino = new Hashtable<T, T>();
                // origemDestino.put(origemDaMenorAresta, destinoDaMenorAresta);
                // pesoDasArestas.put(origemDestino, valorDaMenorAresta);
                
                // Adiciona a Aresta v2
                listaDeOrigens.add(origemDaMenorAresta);
                listaDeDestinos.add(destinoDaMenorAresta);
                listaDePesos.add(valorDaMenorAresta);

                // novoGrafo.adicionarAresta(valorDaMenorAresta, origemDaMenorAresta, destinoDaMenorAresta);
                tamanhonovoGrafo = novoGrafo.vertices.size();
                
                // verticesDoGrafoAntigo.add(verticeDoGrafoAntigo);
            }

            // Remove todas as arestas do novoGrafo
            for(Vertice<T> vertice: novoGrafo.vertices){
                vertice.getDestinos().clear();
            }

            // Preenche as arestas do novoGrafo
            // for(Hashtable<T,T> _origemDestino: pesoDasArestas.keySet()){
            //     for(T _origem : _origemDestino.keySet()){
            //         float pesoAresta = pesoDasArestas.get(_origemDestino);
            //         T _destino = _origemDestino.get(_origem);
            //         novoGrafo.adicionarAresta(pesoAresta, _origem, _destino);
            //     }
            // }

            // Preenche as arestas do novoGrafo v2
            float _peso = 0;
            T _origem, _destino;
            for(int i = 0; i < listaDeOrigens.size(); i++){
                _peso = listaDePesos.get(i);
                _origem = listaDeOrigens.get(i);
                _destino = listaDeDestinos.get(i);
                novoGrafo.adicionarAresta(_peso, _origem, _destino);
            }

            return novoGrafo;
        } else {
            return null;
        }
    }

    public void imprimirArestas() {
        T origem, destino;
        float valorAresta;
        String strSaida;
        System.out.println("===>Imprimindo Arestas<==");
        for(Vertice<T> vertice: this.vertices){
            origem = vertice.getValor();
            for(Aresta<T> aresta : vertice.getDestinos()){
                destino = aresta.getDestino().getValor();
                valorAresta = aresta.getPeso();
                strSaida = String.format("origem=(%s) === %.2f ==> destino=(%s)", origem, valorAresta, destino);
                System.out.println(strSaida);
            }
        }
    }
}
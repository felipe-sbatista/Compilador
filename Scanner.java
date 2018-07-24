
import java.io.BufferedReader;
import java.io.IOException;


public class Scanner {
    int coluna;
    int linha;
    static int caractere_atual=Integer.parseInt("32"); //inicia com espaco em branco, 32 em ascii
    String teste = "";

    BufferedReader reader;
    Scanner(BufferedReader reader){
        coluna=0;
        linha=0;
        this.reader=reader;
    }

    public void look_ahead()throws IOException{
        caractere_atual = reader.read();
        coluna=coluna+1;
    }



    //vai lendo e adicionando ao token enquanto estiver nos padroes do identificador
    public void percorrer_identificador(Token token, String teste)throws IOException{
        Caracteres c =  new Caracteres();
        while (c.compara_digito(teste) || teste.charAt(0) == '_' || c.compara_letra(teste)) {
            token.simbolo = token.simbolo+teste;
            look_ahead();
            teste = String.valueOf((char)caractere_atual);
        }
    }


    public void add_linhaColuna(Token token){
        token.coluna = coluna;
        token.linha = linha;
    }

    public Token lexico() throws IOException, ErrorException {
        Token token =  new Token();
        teste="";
        Caracteres  c = new Caracteres();
        loop: while (caractere_atual != -1) {
            teste = String.valueOf((char) caractere_atual);
            if(c.compara_branco(teste) || teste=="") {
                look_ahead();
                continue loop;
            }
            else if (c.compara_digito(teste)) {//começa digitando numero
                while(c.compara_digito(teste)) {
                    if(token.simbolo==null){
                        token.simbolo=teste;
                    }else {
                        token.simbolo = token.simbolo + teste;
                    }
                    look_ahead();
                    teste = String.valueOf((char) caractere_atual);
                }
                if(teste.charAt(0)=='.'){
                    token.simbolo = token.simbolo + teste;

                    //faz um look ahead para ver se continua sendo float
                    look_ahead();
                    teste = String.valueOf((char) caractere_atual);
                    if(!c.compara_digito(teste)){
                        token.numero=Codigo.FLOAT_MAL_FORMATADO.getCodigo();
                        add_linhaColuna(token);
                        verifica(token);
                        return token;
                    }
                    while(c.compara_digito(teste)) {
                        token.simbolo = token.simbolo + teste;
                        look_ahead();
                        teste = String.valueOf((char) caractere_atual);
                    }
                    token.numero=Codigo.DIGITO_FLOAT.getCodigo();
                    add_linhaColuna(token);
                    return token;
                }else {
                    token.numero = Codigo.DIGITO_INTEIRO.getCodigo();
                    add_linhaColuna(token);
                    return token;
                }
            }

            else if (c.compara_letra(teste)) { //comeca com letra
                while(c.compara_letra(teste)) {
                    if(token.simbolo==null)
                        token.simbolo=teste;
                    else {
                        token.simbolo = token.simbolo + teste;
                    }
                    caractere_atual = reader.read();
                    coluna++;
                    teste = String.valueOf((char)caractere_atual);
                }
                if(c.compara_digito(teste) || teste.charAt(0)=='_'){
                    percorrer_identificador(token,teste);
                }
                else {
                    Codigo a[], auxiliar;
                    a=Codigo.values();
                    for (int i=0;i<9;i++) { //verifica se é uma das palavras reservadas. <9 pois é quantidade de palavras reservadas q estao no inicio no ENUM para nao percorrer todos os pontos do vetor de enum
                        auxiliar= a[i];
                        if (token.simbolo.equals(auxiliar.name().toLowerCase())) {
                            token.numero = auxiliar.getCodigo();
                            add_linhaColuna(token);
                            return token;
                        }
                    }
                }
                add_linhaColuna(token);
                token.numero = Codigo.IDENTIFICADOR.getCodigo();
                return token;
            }

            else if(teste.charAt(0)=='_'){
                token.simbolo=String.valueOf('_');
                look_ahead();
                teste = String.valueOf((char)caractere_atual);
                if(c.compara_digito(teste) || teste.charAt(0)=='_' || c.compara_letra(teste)) {
                    percorrer_identificador(token,teste);
                    token.numero=Codigo.IDENTIFICADOR.getCodigo();
                }
                else {
                    token.numero = Codigo.UNDERLINE.getCodigo();
                    add_linhaColuna(token);
                    return token;
                }
            }

            else if(teste.charAt(0)==39){ //aspas simples na tabela ASCII
                token.simbolo=teste;
                caractere_atual=reader.read();
                coluna++;
                teste = String.valueOf((char)caractere_atual);
                token.simbolo = token.simbolo + teste;
                if(c.compara_letra(teste) || c.compara_digito(teste)) {
                    caractere_atual = reader.read();
                    coluna++;
                    teste = String.valueOf((char) caractere_atual);
                    token.simbolo = token.simbolo + teste;
                    if(teste.charAt(0)==39){
                        token.numero=Codigo.DIGITO_CHAR.getCodigo();
                        caractere_atual=reader.read(); //look ahead
                        add_linhaColuna(token);
                        return token;
                    }
                }
                token.numero=Codigo.CHARACTER_MAL_FORMULADO.getCodigo();

            }

            else if(teste.charAt(0)=='!'){
                token.simbolo=teste;
                caractere_atual =  reader.read();
                teste = String.valueOf((char)caractere_atual);
                if(teste.charAt(0)=='='){
                    token.simbolo+=teste;
                    token.numero=Codigo.DIFERENTE.getCodigo();
                    look_ahead();
                }else{
                    token.numero = Codigo.CHARACTER_INVALIDO.getCodigo();
                }
                add_linhaColuna(token);
                return token;
            }
            else if(teste.charAt(0)=='('){
                token.simbolo=String.valueOf('(');
                token.numero = Codigo.PARENTESE_ESQUERDO.getCodigo();
            }

            else if(teste.charAt(0)==')'){
                token.simbolo=String.valueOf(')');
                token.numero = Codigo.PARENTESE_DIREITO.getCodigo();
            }

            else if(teste.charAt(0)=='{'){
                token.simbolo=String.valueOf('{');
                token.numero = Codigo.CHAVE_ESQUERDA.getCodigo();
            }

            else if(teste.charAt(0)=='}'){
                token.simbolo=String.valueOf('}');
                token.numero = Codigo.CHAVE_DIREITA.getCodigo();
            }

            else if(teste.charAt(0)=='='){
                token.simbolo=String.valueOf('=');
                look_ahead();
                teste = String.valueOf((char) caractere_atual);
                if(teste.charAt(0)=='='){
                    token.simbolo=token.simbolo+String.valueOf('=');
                    token.numero=Codigo.COMPARACAO.getCodigo();
                    look_ahead();
                }else {
                    token.numero = Codigo.RECEBE.getCodigo();
                }
                add_linhaColuna(token);
                return token;
            }

            else if(teste.charAt(0)=='.'){
                token.simbolo=String.valueOf('.');
                look_ahead();
                teste = String.valueOf((char) caractere_atual);
                if(c.compara_digito(teste)){
                    while(c.compara_digito(teste)){
                        token.simbolo = token.simbolo + teste;
                        look_ahead();
                        teste = String.valueOf((char) caractere_atual);
                    }
                }else{
                    token.numero = Codigo.CHARACTER_INVALIDO.getCodigo();
                    add_linhaColuna(token);
                    return token;
                }
                token.numero = Codigo.DIGITO_FLOAT.getCodigo();
                add_linhaColuna(token);
                return token;
            }

            else if(teste.charAt(0)=='<'){
                look_ahead();
                teste = String.valueOf((char) caractere_atual);
                if(teste.charAt(0)=='='){
                    token.simbolo="<=";
                    token.numero = Codigo.MENOR_IGUAL.getCodigo();
                    look_ahead();
                }else{
                    token.simbolo="<";
                    token.numero = Codigo.MENOR.getCodigo();
                }
                add_linhaColuna(token);
                return token;
            }

            else if(teste.charAt(0)=='>'){
                look_ahead();
                teste = String.valueOf((char) caractere_atual);
                if(teste.charAt(0)=='='){
                    token.simbolo=">=";
                    token.numero = Codigo.MAIOR_IGUAL.getCodigo();
                    look_ahead();
                }else{
                    token.simbolo=">";
                    token.numero = Codigo.MAIOR.getCodigo();
                }
                add_linhaColuna(token);
                return token;
            }


            else if(teste.charAt(0)=='-'){
                token.simbolo=String.valueOf('-');
                token.numero = Codigo.SUBTRACAO.getCodigo();
            }

            else if(teste.charAt(0)=='+'){
                token.simbolo=String.valueOf('+');
                token.numero = Codigo.SOMA.getCodigo();
            }

            else if(teste.charAt(0)=='*'){
                token.simbolo=String.valueOf('*');
                token.numero = Codigo.ASTERISTICO.getCodigo();
            }

            else if(teste.charAt(0)=='/'){
                look_ahead();
                teste = String.valueOf((char) caractere_atual);
                if(teste.charAt(0)=='/'){
                    reader.readLine();//readline para ler o resto da linha que ta comentado e quebrar a linha
                    look_ahead();
                    linha=linha+1;
                    coluna=0;
                    continue loop;
                }
                else if(teste.charAt(0)=='*'){ //Achou comentario
                    int linhaComentario = this.linha, colunaComentario= this.coluna;
                    boolean flag=false;
                    while(caractere_atual!=-1){
                        look_ahead();
                        teste = String.valueOf((char) caractere_atual);
                        if(teste.charAt(0)=='\n' || teste.charAt(0)=='\r'){
                            linha=linha+1;
                            coluna=0;
                        }
                        if(flag){
                            if(teste.charAt(0)=='/'){
                                look_ahead();
                                continue loop;
                            }else {
                                flag=false;
                            }
                        }
                        if(teste.charAt(0)=='*') {
                            flag=true;
                        }
                    }
                    token.numero=Codigo.COMENTARIO_NAO_FECHADO.getCodigo();
                    System.out.println("Inicia em : "+"coluna = "+colunaComentario+", linha = "+linhaComentario);
                    System.out.print("Termina em: ");
                    add_linhaColuna(token);
                    verifica(token);
                    return token;
                }else {
                    token.simbolo = String.valueOf('/');
                    token.numero = Codigo.BARRA.getCodigo();
                    add_linhaColuna(token);
                    return token;
                }
            }

            else if(teste.charAt(0)=='\t'){
                coluna+=4;
                look_ahead();
                continue loop;    
            }

            else if(teste.charAt(0)=='\n' || teste.charAt(0)=='\r'){ // \r pois estava recebendo \r
                look_ahead();
                linha++;
                coluna=0;
                continue loop;
            }

            else if(teste.charAt(0)==','){
                token.simbolo=String.valueOf(',');
                token.numero = Codigo.VIRGULA.getCodigo();
            }

            else if(teste.charAt(0)==';'){
                token.simbolo=String.valueOf(';');
                token.numero = Codigo.PONTO_E_VIRGULA.getCodigo();
            }

            else{
                add_linhaColuna(token);
                token.simbolo = teste;
                token.numero = Codigo.CHARACTER_INVALIDO.getCodigo();
            }

            add_linhaColuna(token);
            look_ahead();
            verifica(token);
            return token;
        }
        token.numero=Codigo.FIM_DE_ARQUIVO.getCodigo();
        add_linhaColuna(token);
        return token;
    }

    public void verifica(Token token) throws ErrorException {
        //Numero de erros entre 22 e 27 no Enum
        if(token.numero>22 && token.numero < 27) {
            throw new ErrorException(token);

        }
    }

}

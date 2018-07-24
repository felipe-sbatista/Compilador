
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Stack;

public class Parser {
    Token token;
    Scanner scanner;
    Stack<Simbolo> stack;
    int escopo;
    int contadorGeral;
    int contadorLabel;

    Codigo tipo_variavel;
    boolean flag_atribuicao;

    Parser(BufferedReader reader){
        contadorGeral = 0;
        contadorLabel=0;
        scanner=new Scanner(reader);
        stack = new Stack();
        escopo = 0;
        tipo_variavel=null;
        flag_atribuicao=false;
    }

    public void run() throws IOException, ErrorException {
        if(programa()){
            System.out.println("Parser concluido com sucesso!");
        }
    }

    public boolean programa() throws IOException, ErrorException {
        token = scanner.lexico();
        if(token.numero != Codigo.INT.getCodigo()) {
            throw new ErrorException(token,"Erro no fechamento de bloco", this.escopo);
        }
        token=scanner.lexico();
        if(token.numero != Codigo.MAIN.getCodigo()) {
            throw new ErrorException(token,"necessidade do MAIN", this.escopo);
        }
        token=scanner.lexico();
        if(token.numero != Codigo.PARENTESE_ESQUERDO.getCodigo()) {
            throw new ErrorException(token,"Erro na abertura de parenteses", this.escopo);
        }
        token=scanner.lexico();
        if(token.numero != Codigo.PARENTESE_DIREITO.getCodigo()) {
            throw new ErrorException(token,"Erro no fechamento de parenteses", this.escopo);
        }
        token = scanner.lexico();
        if(bloco()){
            // token = scanner.lexico();
            if(token.numero!=Codigo.FIM_DE_ARQUIVO.getCodigo()){
                throw new ErrorException(token,"Argumentos apos o fechamento de bloco", this.escopo);
            }
            return true;
        }else {
            throw new ErrorException(token, "Erro de bloco no PROGRAMA", this.escopo);
        }
    }

    public boolean bloco() throws IOException, ErrorException {
        if(token.numero == Codigo.CHAVE_ESQUERDA.getCodigo()){
            token = scanner.lexico();
            escopo++;
            while(declaracao_variavel()){
                token = scanner.lexico();
            }
            // já entra aqui com o look ahead
            while(comando());

            if(token.numero == Codigo.CHAVE_DIREITA.getCodigo()){
                token = scanner.lexico();
                desempilha_escopo();
                escopo--;
                return true;
            }
            throw new ErrorException(token,"Erro no fechamento de bloco", this.escopo);

        }
        return false;
    }

    public boolean declaracao_variavel() throws IOException, ErrorException {
        if (token.numero == Codigo.INT.getCodigo() || token.numero == Codigo.FLOAT.getCodigo() || token.numero == Codigo.CHAR.getCodigo()) {
            Codigo codigo=null;
            if(token.numero == Codigo.INT.getCodigo()) {
                codigo=Codigo.DIGITO_INTEIRO;
            }
            else if(token.numero == Codigo.FLOAT.getCodigo()) {
                codigo=Codigo.DIGITO_FLOAT;
            }
            else{
                codigo=Codigo.DIGITO_CHAR;
            }
            token = scanner.lexico();

            while (true) {
                Simbolo simbolo = new Simbolo();
                simbolo.codigo = codigo;
                simbolo.escopo=this.escopo;
                if (token.numero == Codigo.IDENTIFICADOR.getCodigo()) {
                    simbolo.token=token;
                    checa_declaracao_variavel(simbolo);
                    token = scanner.lexico();
                    if (token.numero == Codigo.VIRGULA.getCodigo()) {
                        token = scanner.lexico();
                    } else if (token.numero == Codigo.PONTO_E_VIRGULA.getCodigo()) {
                        return true;
                    } else {
                        throw new ErrorException(token,"Erro na finalizacao da declaracao de variavel", this.escopo);
                    }
                } else if (token.numero == Codigo.PONTO_E_VIRGULA.getCodigo()) {
                    throw new ErrorException(token,"Erro na declaracao de variavel", this.escopo);
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean comando() throws IOException, ErrorException {
        if(token.numero == Codigo.IF.getCodigo()){
            int aux = contadorLabel;
            token = scanner.lexico();
            if(token.numero == Codigo.PARENTESE_ESQUERDO.getCodigo()){
                token = scanner.lexico();
                CodigoIntermediario codigoIntermediario;
                codigoIntermediario=expressao_relacional();
                if(token.numero == Codigo.PARENTESE_DIREITO.getCodigo()){
                    token = scanner.lexico();
                    System.out.println("    if "+codigoIntermediario.expressao+" == FALSE: GOTO Label"+aux);
                    contadorLabel++;
                    if(!comando()) {
                        throw new ErrorException(token, "Erro no comando do IF", this.escopo);
                    }
                    if(token.numero == Codigo.ELSE.getCodigo()){
                        System.out.println("    GOTO Fim"+aux);
                        token = scanner.lexico();
                        System.out.println("Label"+aux+": ");
                        if(comando()){
                            System.out.println("Fim"+aux+": ");
                            return true;
                        }else{
                            throw new ErrorException(token,"Erro no comando do ELSE", this.escopo);
                        }
                    }else{
                        System.out.println("Label"+aux+": ");
                        return true;
                    }

                }

            }
        }else if(iteracao()){
            return  true;
        }else  if(comando_basico()) {
            return true;
        }
        return false;
    }

    public boolean iteracao() throws IOException, ErrorException {
        if(token.numero == Codigo.WHILE.getCodigo() || token.numero == Codigo.DO.getCodigo()) {
            int aux =  contadorLabel;
            if (token.numero == Codigo.WHILE.getCodigo()) {
                token = scanner.lexico();
                if (token.numero == Codigo.PARENTESE_ESQUERDO.getCodigo()) {
                    token = scanner.lexico();
                    CodigoIntermediario codigoIntermediario;
                    System.out.println("Inicio"+aux+": ");
                    codigoIntermediario = expressao_relacional();
                    if (token.numero == Codigo.PARENTESE_DIREITO.getCodigo()) {
                        System.out.println("    if "+codigoIntermediario.expressao+" == FALSE: GOTO Fim"+contadorLabel);
                        token = scanner.lexico();
                        contadorLabel++;
                        if (!comando()) {
                            throw new ErrorException(token,"Erro no comando do while", this.escopo);
                        }
                        System.out.println("    GOTO Inicio"+aux);
                        System.out.println("Fim"+aux+": ");
                        return true;
                    }else{
                        throw new ErrorException(token,"Erro no fechamento de parentes do WHILE", this.escopo);
                    }
                } else {
                    throw new ErrorException(token,"Erro na abertura de parentes do WHILE", this.escopo);
                }
            } else{
                token = scanner.lexico();
                System.out.println("Inicio"+contadorLabel+": ");
                contadorLabel++;
                if (!comando()) {
                    throw new ErrorException(token,"Erro no comando DO-WHILE", this.escopo);
                }
                if (token.numero != Codigo.WHILE.getCodigo()) {
                    throw new ErrorException(token,"Necessidade do While para o comando DO-WHILE", this.escopo);
                }
                token = scanner.lexico();
                if (token.numero != Codigo.PARENTESE_ESQUERDO.getCodigo()) {
                    throw new ErrorException(token, "Erro na abertura do parentese", this.escopo);
                }
                token = scanner.lexico();
                CodigoIntermediario codigoIntermediario;
                codigoIntermediario = expressao_relacional();
                if (token.numero != Codigo.PARENTESE_DIREITO.getCodigo()) {
                    throw new ErrorException(token, "Erro no fechamento de parentes do while", this.escopo);
                }
                token = scanner.lexico();
                if (token.numero != Codigo.PONTO_E_VIRGULA.getCodigo()) {
                    throw new ErrorException(token,"Erro no fechamento do DO-WHILE", this.escopo);
                }
                token = scanner.lexico();
                System.out.println("    if "+codigoIntermediario.expressao+" == TRUE: GOTO Inicio"+aux);
                return true;
            }
        }
        return false;
    }

    public boolean comando_basico() throws IOException, ErrorException {
        if(token.numero == Codigo.IDENTIFICADOR.getCodigo()) {
            CodigoIntermediario codigoIntermediario = new CodigoIntermediario();
            codigoIntermediario.codigo= buscaTabelaSimboloTopoDaPilha(token);
            codigoIntermediario.expressao = token.simbolo;
            token = scanner.lexico();
            if (atribuicao(codigoIntermediario)) {
                return true;
            } else {
                throw new ErrorException(token,"Erro na atribuicao", this.escopo);
            }
        }else if (bloco()) {
            return true;
        }
        return false;
    }

    public boolean atribuicao(CodigoIntermediario tipo_recebedor) throws IOException, ErrorException {
        if(token.numero == Codigo.RECEBE.getCodigo()){
            token = scanner.lexico();
            CodigoIntermediario retorno_exp;
            retorno_exp = expressao_aritmetica();
            if(retorno_exp.codigo == tipo_recebedor.codigo || (tipo_recebedor.codigo==Codigo.DIGITO_FLOAT && retorno_exp.codigo==Codigo.DIGITO_INTEIRO)){
                compara_tipos(tipo_recebedor,retorno_exp);
                if(token.numero == Codigo.PONTO_E_VIRGULA.getCodigo()){
                    System.out.println(tipo_recebedor.expressao+" = "+ retorno_exp.expressao+token.simbolo);
                    token = scanner.lexico();
                    return true;
                }else{
                    throw new ErrorException(token,"Erro de ponto e virgula na atribuicao", this.escopo);
                }
            }else if(retorno_exp.codigo == Codigo.DIGITO_FLOAT && tipo_recebedor.codigo == Codigo.DIGITO_INTEIRO){
                throw new ErrorException(token,"Erro na atribuicao, um inteiro nao pode receber um float", this.escopo);
            }
            else{
                throw new ErrorException(token,"Erro na atribuicao, tipos incompativeis =>", this.escopo, tipo_recebedor.codigo, retorno_exp.codigo);
            }
        }
        return false;
    }

    public CodigoIntermediario expressao_relacional() throws IOException, ErrorException {
        CodigoIntermediario primeiro_operador =  expressao_aritmetica();
        CodigoIntermediario segundo_operador;
        if(token.numero == Codigo.DIFERENTE.getCodigo() ||
                token.numero == Codigo.MAIOR_IGUAL.getCodigo() ||
                token.numero == Codigo.MAIOR.getCodigo() ||
                token.numero == Codigo.MENOR.getCodigo() ||
                token.numero == Codigo.MENOR_IGUAL.getCodigo() ||
                token.numero == Codigo.COMPARACAO.getCodigo()){
            String relacional = token.simbolo;
            token = scanner.lexico();
            segundo_operador = expressao_aritmetica();
            compara_tipos(primeiro_operador, segundo_operador);
            System.out.println("T"+contadorGeral+" =: "+primeiro_operador.expressao + " " +relacional+" "+segundo_operador.expressao);
            primeiro_operador.expressao = "T"+contadorGeral;
            contadorGeral++;
            return primeiro_operador;
        }
        throw new ErrorException(token,"Erro na expressao relacional!", this.escopo);
    }

    public CodigoIntermediario expressao_aritmetica () throws IOException, ErrorException {
        CodigoIntermediario tipo_atual;
        CodigoIntermediario tipo_anterior;
        CodigoIntermediario tipo_expressao;
        String operador;

        //ja recebe do termo com o atributo (STRING) expressao preenchido
        tipo_anterior= termo();
        tipo_expressao = tipo_anterior;
        while(true){
            if (token.numero == Codigo.SOMA.getCodigo() || token.numero == Codigo.SUBTRACAO.getCodigo()) {
                operador = token.simbolo;
                token = scanner.lexico();
            }else {
                return tipo_expressao;
            }
            tipo_atual = termo();
            compara_tipos(tipo_anterior, tipo_atual);
            System.out.println("T"+contadorGeral+" = "+tipo_anterior.expressao+operador+tipo_atual.expressao);
            tipo_expressao.expressao = "T"+contadorGeral;
            contadorGeral++;
            tipo_anterior.expressao = tipo_expressao.expressao;
            tipo_anterior.codigo = tipo_atual.codigo;

            if(tipo_expressao.codigo==Codigo.DIGITO_INTEIRO){
                tipo_expressao.codigo = tipo_atual.codigo;
            }
        }
    }

    public CodigoIntermediario termo() throws IOException, ErrorException {
        CodigoIntermediario tipo_atual;
        CodigoIntermediario tipo_anterior;
        CodigoIntermediario tipo_geral;
        boolean retornoDivisao=false;
        String operador;
        //já retorna com a string
        tipo_anterior = fator();
        tipo_geral = tipo_anterior;
        token=scanner.lexico();
        while(true){
            if(token.numero == Codigo.ASTERISTICO.getCodigo() || token.numero == Codigo.BARRA.getCodigo()){
                operador = token.simbolo;
                if(token.numero == Codigo.BARRA.getCodigo() && tipo_anterior.codigo!=Codigo.DIGITO_CHAR && !retornoDivisao){
                    retornoDivisao=true;
                }
                token = scanner.lexico();
            }else{
                if(retornoDivisao){
                    tipo_geral.codigo = Codigo.DIGITO_FLOAT;
                }
                return tipo_geral;
            }
            tipo_atual=fator();
            compara_tipos(tipo_anterior, tipo_atual);
            System.out.println("T"+contadorGeral+" = "+tipo_anterior.expressao+operador+tipo_atual.expressao);
            tipo_geral.expressao = "T"+contadorGeral;
            contadorGeral++;
            tipo_anterior.expressao = tipo_geral.expressao;
            if(operador=="/"){
                tipo_anterior.codigo = Codigo.DIGITO_FLOAT;
                tipo_geral.codigo = Codigo.DIGITO_FLOAT;
            }else{
                tipo_anterior.codigo = tipo_atual.codigo;
            }

            if(tipo_geral.codigo==Codigo.DIGITO_INTEIRO){
                tipo_geral.codigo = tipo_atual.codigo;
            }
            token=scanner.lexico();
        }
    }


    public CodigoIntermediario fator() throws IOException, ErrorException {
        if(token.numero == Codigo.PARENTESE_ESQUERDO.getCodigo()){
            token=scanner.lexico();
            CodigoIntermediario retorno_exp=expressao_aritmetica();
            if(retorno_exp == null){
                throw new ErrorException(token,"Erro na expressao aritmetica dentro do fator", this.escopo);
            }
            if(token.numero == Codigo.PARENTESE_DIREITO.getCodigo()){
                return retorno_exp;
            }else{
                throw new ErrorException(token,"Erro no fechamento de paranteses no FATOR", this.escopo);
            }
        }else{
            CodigoIntermediario codigoIntermediario = new CodigoIntermediario();
            if(token.numero == Codigo.IDENTIFICADOR.getCodigo()){
                codigoIntermediario.codigo =buscaTabelaSimboloTopoDaPilha(token);
                codigoIntermediario.expressao=token.simbolo;
                return codigoIntermediario;
            }else if( token.numero == Codigo.DIGITO_CHAR.getCodigo() ){
                codigoIntermediario.codigo =Codigo.DIGITO_CHAR;
                codigoIntermediario.expressao=token.simbolo;
                return codigoIntermediario;
            }else if(token.numero == Codigo.DIGITO_INTEIRO.getCodigo()){
                codigoIntermediario.codigo =Codigo.DIGITO_INTEIRO;
                codigoIntermediario.expressao=token.simbolo;
                return codigoIntermediario;
            }else if(token.numero == Codigo.DIGITO_FLOAT.getCodigo()){
                codigoIntermediario.codigo =Codigo.DIGITO_FLOAT;
                codigoIntermediario.expressao=token.simbolo;
                return codigoIntermediario;
            }else{
                throw new ErrorException(token,"Erro, na expressao aritmetica", this.escopo);
            }
        }
    }


    public void compara_tipos(CodigoIntermediario codigo_anterior, CodigoIntermediario codigo_atual) throws ErrorException {
        if(codigo_anterior.codigo == codigo_atual.codigo){
            return;
        }else{
            if(codigo_anterior.codigo == Codigo.DIGITO_CHAR || codigo_atual.codigo == Codigo.DIGITO_CHAR){
                if(codigo_atual.codigo == Codigo.DIGITO_INTEIRO || codigo_anterior.codigo == Codigo.DIGITO_INTEIRO){
                    throw new ErrorException(token,"Erro de incompatibilidade de tipos entre Char e Inteiro", this.escopo);
                }else{
                    throw new ErrorException(token,"Erro de incompatibilidade de tipos entre Char e Float", this.escopo);
                }
            }else{//conversao de tipos
                if(codigo_anterior.codigo == Codigo.DIGITO_FLOAT){
                    codigo_atual.codigo = Codigo.DIGITO_FLOAT;
                    System.out.println("T"+contadorGeral+" = "+"(FLOAT) "+codigo_atual.expressao);
                    codigo_atual.expressao = "T"+contadorGeral;
                }else{
                    codigo_anterior.codigo = Codigo.DIGITO_FLOAT;
                    System.out.println("T"+contadorGeral+" = "+"(FLOAT) "+codigo_anterior.expressao);
                    codigo_anterior.expressao = "T"+contadorGeral;
                }
                contadorGeral++;
            }
        }
    }


    public void checa_declaracao_variavel(Simbolo simbolo) throws ErrorException {
        for (Simbolo simboloPilha: stack) {
            if (simbolo.escopo == simboloPilha.escopo && simboloPilha.token.simbolo.equals(simbolo.token.simbolo)) {
                throw new ErrorException(token,"Variavel já declarada no mesmo escopo!", this.escopo);
            }
        }

        stack.push(simbolo);
    }

    public Codigo buscaTabelaSimboloTopoDaPilha(Token token) throws ErrorException {
        Simbolo simbolo;
        Stack<Simbolo> stackAux;
        stackAux = (Stack)stack.clone();
        while(!stackAux.isEmpty()) {
            simbolo = stackAux.pop();
            if (simbolo.token.simbolo.equals(token.simbolo)) {
                return simbolo.codigo;
            }
        }
        throw new ErrorException(token,"Variavel nao declarada!", this.escopo, token.simbolo);
    }

    public void desempilha_escopo(){
        while (!stack.isEmpty() && (stack.peek()).escopo == this.escopo) {
            stack.pop();
        }
    }
}

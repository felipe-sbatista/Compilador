

public enum Codigo {
    //reseverdas
    INT(1), WHILE(2),DO(3),IF(4),ELSE(5),FOR(6), FLOAT(7),MAIN(8),CHAR(9),

    //operacoes relacionais
    MENOR(10),MAIOR(11),MENOR_IGUAL(12),DIFERENTE(13),MAIOR_IGUAL(14),COMPARACAO(15),

    //aritmeticos
    BARRA(16),SOMA(17),SUBTRACAO(18),ASTERISTICO(19),

    //tipos
    DIGITO_FLOAT(20),DIGITO_CHAR(21),DIGITO_INTEIRO(22),

    //erros
    CHARACTER_MAL_FORMULADO(23),COMENTARIO_NAO_FECHADO(24), FLOAT_MAL_FORMATADO(25),CHARACTER_INVALIDO(26),

    PONTO_E_VIRGULA(27), VIRGULA(28), RECEBE(29),UNDERLINE(30) , IDENTIFICADOR(31),
    PARENTESE_ESQUERDO(32),PARENTESE_DIREITO(33),CHAVE_ESQUERDA(34), CHAVE_DIREITA(35),
    COLCHETE_ESQUERDO(36),COLCHETE_DIREITO(37),

    FIM_DE_ARQUIVO(38);


    private  int codigo;

    Codigo(int codigo){
        this.codigo=codigo;
    }

    public int getCodigo(){
        return codigo;
    }

    public static String getNome(int codigo){
        for (Codigo c: Codigo.values()) {
            if(codigo == c.getCodigo()){
                String nomeCompleto[]=c.name().split("_");
                String nome="";
                for (String n : nomeCompleto) {
                    nome = nome + n;
                }
                return nome;
            }
        }
        return null;
    }

}

package br.com.orcagov.api.entity.enums;

public enum TipoDespesa {
    OBRA_EDIFICACAO("Obra de Edificação", "OE"),
    OBRA_RODOVIAS("Obra de Rodovias", "OR"),
    OUTROS("Outros", "OU");
    
    private final String descricao;
    private final String codigo;
    
    TipoDespesa(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    // Método para buscar enum por descrição
    public static TipoDespesa fromDescricao(String descricao) {
        for (TipoDespesa tipo : TipoDespesa.values()) {
            if (tipo.descricao.equalsIgnoreCase(descricao)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de despesa não encontrado: " + descricao);
    }
    
    // Método para buscar enum por código
    public static TipoDespesa fromCodigo(String codigo) {
        for (TipoDespesa tipo : TipoDespesa.values()) {
            if (tipo.codigo.equalsIgnoreCase(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de tipo de despesa não encontrado: " + codigo);
    }
    
    // Método para verificar se é obra
    public boolean isObra() {
        return this == OBRA_EDIFICACAO || this == OBRA_RODOVIAS;
    }
}

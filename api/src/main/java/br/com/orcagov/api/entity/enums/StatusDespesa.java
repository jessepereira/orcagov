package br.com.orcagov.api.entity.enums;

public enum StatusDespesa {
    AGUARDANDO_EMPENHO("Aguardando Empenho"),
    PARCIALMENTE_EMPENHADA("Parcialmente Empenhada"),
    AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),
    PARCIALMENTE_PAGA("Parcialmente Paga"),
    PAGA("Paga");
    
    private final String descricao;
    
    StatusDespesa(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    // Método para buscar enum por descrição
    public static StatusDespesa fromDescricao(String descricao) {
        for (StatusDespesa status : StatusDespesa.values()) {
            if (status.descricao.equalsIgnoreCase(descricao)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status não encontrado: " + descricao);
    }
    
    // Método para verificar se o status permite alterações
    public boolean permiteAlteracao() {
        return this != PAGA;
    }
    
    // Método para verificar se pode adicionar empenho
    public boolean permiteEmpenho() {
        return this == AGUARDANDO_EMPENHO || this == PARCIALMENTE_EMPENHADA;
    }
    
    // Método para verificar se pode adicionar pagamento
    public boolean permitePagamento() {
        return this == AGUARDANDO_PAGAMENTO || this == PARCIALMENTE_PAGA;
    }
}
package br.com.orcagov.api.entity.enums;

public enum FormatoRelatorio {
    PDF("Portable Document Format", "application/pdf", ".pdf"),
    EXCEL("Microsoft Excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    CSV("Comma Separated Values", "text/csv", ".csv"),
    JSON("JavaScript Object Notation", "application/json", ".json");
    
    private final String descricao;
    private final String mimeType;
    private final String extensao;
    
    FormatoRelatorio(String descricao, String mimeType, String extensao) {
        this.descricao = descricao;
        this.mimeType = mimeType;
        this.extensao = extensao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public String getExtensao() {
        return extensao;
    }
    
    // Método para buscar formato por extensão
    public static FormatoRelatorio fromExtensao(String extensao) {
        for (FormatoRelatorio formato : FormatoRelatorio.values()) {
            if (formato.extensao.equalsIgnoreCase(extensao)) {
                return formato;
            }
        }
        throw new IllegalArgumentException("Formato não encontrado: " + extensao);
    }
}
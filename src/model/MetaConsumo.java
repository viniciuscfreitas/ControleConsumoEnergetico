public class MetaConsumo {
    private float valorMeta;
    private String tipo;

    // Construtor
    public MetaConsumo(float valorMeta, String tipo) {
        this.valorMeta = valorMeta;
        this.tipo = tipo;
    }

    // Getter e Setter para valorMeta
    public float getValorMeta() {
        return valorMeta;
    }

    public void setValorMeta(float valorMeta) {
        this.valorMeta = valorMeta;
    }

    // Getter e Setter para tipo
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}